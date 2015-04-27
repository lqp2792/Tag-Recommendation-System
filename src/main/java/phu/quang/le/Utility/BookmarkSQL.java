package phu.quang.le.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import phu.quang.le.Model.AdvanceBookmark;
import phu.quang.le.Model.RateResult;
import phu.quang.le.Model.Topic;

/**
 * @author phule
 *
 */
public class BookmarkSQL {

	/**
	 * Add a bookmark into Database
	 * 
	 * @param url
	 *            Bookmark URL
	 * @param title
	 *            Bookmark title
	 * @param urlKeywords
	 *            Bookmark keywords crawled from metadata tag
	 * 
	 * @param urlDescription
	 *            Bookmark description crawled from metada tag
	 * @return true / false
	 */
	public static int addBookmarkToDB(String url, String title,
			String urlKeywords, String urlDescription, int userID) {
		int result = -1;
		int bookmarkID = -1;
		Connection connection = DBUtility.getConnection();
		String sql = "INSERT INTO bookmarks_new VALUES (default, ?, ?, ?, ?, ?, NOW(), default, default, default)";
		try {
			PreparedStatement pst = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, url);
			pst.setString(2, title);
			pst.setString(3, urlKeywords);
			pst.setString(4, urlDescription);
			pst.setInt(5, userID);
			result = pst.executeUpdate();
			if (result == 0) {
				System.err.println("Can not add Bookmark to DB");
			} else {
				ResultSet rs = pst.getGeneratedKeys();
				if (rs.next()) {
					bookmarkID = (int) rs.getLong(1);
				}
			}
		} catch (SQLException e) {
			System.err.println("Add Bookmark to DB " + e);
		}
		return bookmarkID;
	}

	/**
	 * Check if bookmark already tagged with that tag
	 * 
	 * @param bookmarkID
	 * @param tagID
	 * @return tag weight
	 */
	public static int bookmarkHasTag(int bookmarkID, int tagID) {
		int tagWeight = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT tag_weight FROM bookmark_tags_new WHERE bookmarkID = ? AND tagID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			pst.setInt(2, tagID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				tagWeight = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Check bookmark already has tag: " + e);
		}
		//
		return tagWeight;
	}

	/**
	 * Add bookmark 's topics into database
	 * 
	 * @param bookmarkID
	 * @param topicIDs
	 * @return
	 */
	public static int addBookmarkTopics(int bookmarkID, List<Topic> topics) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		String sql = "INSERT INTO bookmark_topics VALUES (?, ?, ?)";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			for (int i = 0; i < topics.size(); i++) {
				Topic topic = topics.get(i);
				pst.setInt(2, topic.getTopicID());
				pst.setDouble(3, topic.getTopicProbality());
				result = pst.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Add Bookmark Topics: " + e);
		}
		return result;
	}

	public static List<AdvanceBookmark> discoverBookmarks(
			List<Integer> subscriptionTopicIDs, int userID, int offset,
			int sortBy) {
		List<AdvanceBookmark> bookmarks = new ArrayList<AdvanceBookmark>();
		Connection c = DBUtility.getConnection();
		/* Tạo SQL để sắp xếp các Bookmark theo thứ tự */
		String sql = "SELECT * FROM bookmarks_new b WHERE bookmarkID IN (SELECT DISTINCT bookmarkID"
				+ " FROM bookmark_topics WHERE ";
		switch (sortBy) {
		case 1:
			sql += " rating > 0 AND ";
			break;
		case 2:
			sql += " view_count > 1 AND ";
			break;
		case 3:
			sql += " copy_count > 0 AND ";
			break;
		}
		sql += " topicID IN (";
		for (int i = 0; i < subscriptionTopicIDs.size() - 1; i++) {
			sql += "?,";
		}
		sql += "?)) AND b.userID <> ? AND b.userID NOT IN (SELECT userIDb FROM users_follow WHERE userIDa = ?) ORDER BY ";
		switch (sortBy) {
		case 1:
			sql += "rating ";
			break;
		case 2:
			sql += "view_count ";
			break;
		case 3:
			sql += "copy_count ";
			break;
		case 4:
			sql += "timestamp ";
			break;
		}
		sql += "LIMIT 5 OFFSET ?";
		System.out.println("Discovering Boookmarks");
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			int index = 1;
			for (int i = 0; i < subscriptionTopicIDs.size(); i++) {
				pst.setInt(index, subscriptionTopicIDs.get(i));
				index++;
			}
			pst.setInt(index, userID);
			pst.setInt(index + 1, userID);
			pst.setInt(index + 2, offset);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				AdvanceBookmark b = new AdvanceBookmark();
				List<String> tags = new ArrayList<String>();
				int bookmarkID = rs.getInt(1);
				b.setBookmarkID(bookmarkID);
				b.setPostedUserID(rs.getInt(6));
				b.setUrl(rs.getString(2));
				b.setTitle(rs.getString(3));
				b.setDate(rs.getDate(7));
				b.setViewTimes(rs.getInt(8));
				b.setTotalRating(rs.getDouble(9));
				b.setCopyTimes(rs.getInt(10));
				b.setFriend(UserSQL.isFollowed(userID, rs.getInt(6)));
				String sql1 = "SELECT tag_content FROM user_tag_bookmark utb, tags_new t "
						+ "WHERE userID = ? AND bookmarkID = ? AND utb.tagID = t.tagID";
				PreparedStatement pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, rs.getInt(6));
				pst1.setInt(2, bookmarkID);
				ResultSet rs1 = pst1.executeQuery();
				while (rs1.next()) {
					tags.add(rs1.getString(1));
				}
				b.setTags(tags);
				sql1 = "SELECT first_name, last_name FROM users WHERE id = ?";
				pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, rs.getInt(6));
				rs1 = pst1.executeQuery();
				if (rs1.next()) {
					b.setFirstName(rs1.getString(1));
					b.setLastName(rs1.getString(2));
				}
				sql1 = "SELECT rating FROM user_rating WHERE userID = ? AND bookmarkID = ?";
				pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, userID);
				pst1.setInt(2, bookmarkID);
				rs1 = pst1.executeQuery();
				if (rs1.next()) {
					b.setRated(rs1.getDouble(1));
				} else {
					b.setRated(0);
				}
				sql1 = "SELECT * FROM user_copy WHERE userID = ? AND bookmarkID = ?";
				pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, userID);
				pst1.setInt(2, bookmarkID);
				rs1 = pst1.executeQuery();
				if (rs1.next()) {
					b.setCopied(true);
				} else {
					b.setCopied(false);
				}
				bookmarks.add(b);
			}
		} catch (SQLException e) {
			System.err.println("Discover Bookmark: " + e);
		}
		return bookmarks;
	}

	public static int bookmarkClick(int bookmarkID) {
		int count = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT view_count FROM bookmarks_new WHERE bookmarkID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1) + 1;
				sql = "UPDATE bookmarks_new SET view_count = ? WHERE bookmarkID = ?";
				pst = c.prepareStatement(sql);
				pst.setInt(1, count);
				pst.setInt(2, bookmarkID);
				pst.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Bookmark click: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return count;
	}

	public static double rateBookmark(int userID, int bookmarkID, double rating) {
		double totalRating = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT rating FROM bookmarks_new WHERE bookmarkID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				totalRating = rs.getDouble(1);
			}
			sql = "SELECT count(bookmarkID) FROM user_rating WHERE bookmarkID = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			rs = pst.executeQuery();
			int ratedTimes = 0;
			if (rs.next()) {
				ratedTimes = rs.getInt(1);
			}
			sql = "SELECT * FROM user_rating WHERE userID = ? AND bookmarkID = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, bookmarkID);
			rs = pst.executeQuery();
			PreparedStatement pst1 = null;
			if (rs.next()) {
				sql = "UPDATE user_rating SET rating = ? WHERE userID = ? AND bookmarkID = ?";
				pst1 = c.prepareStatement(sql);
				pst1.setDouble(1, rating);
				pst1.setInt(2, userID);
				pst1.setInt(3, bookmarkID);
				pst1.executeUpdate();
				sql = "SELECT ROUND(AVG(rating), 2) FROM user_rating WHERE bookmarkID = ?";
				pst1 = c.prepareStatement(sql);
				pst1.setInt(1, bookmarkID);
				rs = pst1.executeQuery();
				if (rs.next()) {
					totalRating = rs.getDouble(1);
				}
			} else {
				sql = "INSERT INTO user_rating VALUES (?, ?, ?)";
				pst1 = c.prepareStatement(sql);
				pst1.setInt(1, userID);
				pst1.setInt(2, bookmarkID);
				pst1.setDouble(3, rating);
				pst1.executeUpdate();
				ratedTimes++;
				totalRating = (totalRating + rating) / ratedTimes;
			}
			sql = "UPDATE bookmarks_new SET rating = ? WHERE bookmarkID = ?";
			pst = c.prepareStatement(sql);
			pst.setDouble(1, totalRating);
			pst.setInt(2, bookmarkID);
			pst.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Rating Bookmark: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return totalRating;
	}

	public static RateResult getRateResult(int bookmarkID) {
		RateResult rateResult = new RateResult();
		int totalCount = 0;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT rating, COUNT(rating) FROM user_rating WHERE bookmarkID = ? GROUP BY rating";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int count = rs.getInt(2);
				totalCount += count;
				if (rs.getDouble(1) == 1) {
					rateResult.setVeryPoorCount(count);
				}
				if (rs.getDouble(1) == 1.5) {
					rateResult.setQuitePoorCount(count);
				}
				if (rs.getDouble(1) == 2) {
					rateResult.setPoorCount(count);
				}
				if (rs.getDouble(1) == 2.5) {
					rateResult.setQuiteFairCount(count);
				}
				if (rs.getDouble(1) == 3) {
					rateResult.setFairCount(count);
				}
				if (rs.getDouble(1) == 3.5) {
					rateResult.setAcceptableCount(count);
				}
				if (rs.getDouble(1) == 4) {
					rateResult.setQuiteGoodCount(count);
				}
				if (rs.getDouble(1) == 4.5) {
					rateResult.setGoodCount(count);
				}
				if (rs.getDouble(1) == 5) {
					rateResult.setVeryGoodCount(count);
				}
			}
			rateResult.setTotalCount(totalCount);
		} catch (SQLException e) {
			System.err.println("Get Rate Result: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return rateResult;
	}

	public static double getTotalRating(int bookmarkID) {
		double totalRating = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT rating FROM bookmarks_new WHERE bookmarkID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				totalRating = rs.getDouble(1);
			}
		} catch (SQLException e) {
			System.err.println("Get Total Rating: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return totalRating;
	}

	public static int getCopyTimes(int bookmarkID) {
		int countTimes = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT copy_count FROM bookmarks_new WHERE bookmarkID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				countTimes = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Get Copy Count: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return countTimes;
	}

	public static List<String> getTags(int bookmarkID) {
		List<String> tags = new ArrayList<String>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT tag_content FROM bookmark_tags_new bt, tags_new t "
				+ "WHERE bookmarkID = ? AND bt.tagID = t.tagID";

		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				tags.add(rs.getString(1));
			}
		} catch (SQLException e) {
			System.err.println("Get Tags: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return tags;
	}

	public static int editBookmark(int bookmarkID, int userID,
			List<String> addTags, List<String> deletedTags) {
		int result = -1;
		if (addTags != null) {
			for (int i = 0; i < addTags.size(); i++) {
				int tagID = TagSQL.addTagToDB(userID, addTags.get(i));
				TagSQL.addTagToBookmark(bookmarkID, tagID);
				UserSQL.userTaggedBookmark(userID, bookmarkID, tagID);
			}
		}
		if (deletedTags != null) {
			for (int i = 0; i < deletedTags.size(); i++) {
				deleteTag(bookmarkID, userID,
						TagSQL.isTagExisted(deletedTags.get(i)));
			}
		}
		return result;
	}

	public static int deleteTag(int bookmarkID, int userID, int tagID) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		String sql = "DELETE FROM user_tag_bookmark WHERE userID = ? AND tagID = ? AND bookmarkID = ? "
				+ "ORDER BY bookmark_timestamp ASC LIMIT 1";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, tagID);
			pst.setInt(3, bookmarkID);
			result = pst.executeUpdate();
			if (result == 0) {
				System.out.println("Delete row in user_tag_bookmark fail");
			} else {
				System.out.println("Delete row in user_tag_bookmark success");
				result = 0;
			}
			sql = "SELECT tag_weight FROM user_tag WHERE userID = ? AND tagID = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, tagID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				int tagWeight = rs.getInt(1);
				if (tagWeight > 1) {
					sql = "UPDATE user_tag SET tag_weight = ? WHERE userID = ? AND tagID = ?";
					PreparedStatement pst1 = c.prepareStatement(sql);
					pst1.setInt(1, tagWeight - 1);
					pst1.setInt(2, userID);
					pst1.setInt(3, tagID);
					result = pst1.executeUpdate();
					if (result == 0) {
						System.out
								.println("Change tag weight in user_tag fail");
					} else {
						System.out
								.println("Change tag weight in user_tag success");
						result = 0;
					}
				}
				if (tagWeight == 1) {
					sql = "DELETE FROM user_tag WHERE userID = ? AND tagID = ?";
					PreparedStatement pst1 = c.prepareStatement(sql);
					pst1.setInt(1, userID);
					pst1.setInt(2, tagID);
					result = pst1.executeUpdate();
					if (result == 0) {
						System.out.println("Delete row in user_tag fail");
					} else {
						System.out.println("Delete row in user_tag success");
						result = 0;
					}
				}
			}
			sql = "SELECT tag_weight FROM bookmark_tags_new WHERE bookmarkID = ? AND tagID = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, tagID);
			rs = pst.executeQuery();
			if (rs.next()) {
				int tagWeight = rs.getInt(1);
				if (tagWeight > 1) {
					sql = "UPDATE bookmark_tags_new SET tag_weight = ? WHERE bookmarkID = ? AND tagID = ?";
					PreparedStatement pst1 = c.prepareStatement(sql);
					pst1.setInt(1, tagWeight - 1);
					pst1.setInt(2, bookmarkID);
					pst1.setInt(3, tagID);
					result = pst1.executeUpdate();
					if (result == 0) {
						System.out
								.println("Change tag weight in bookmark_tags_new fail");
						result = 0;
					}
				}
				if (tagWeight == 1) {
					sql = "DELETE FROM bookmark_tags_new WHERE bookmarkID = ? AND tagID = ?";
					PreparedStatement pst1 = c.prepareStatement(sql);
					pst1.setInt(1, bookmarkID);
					pst1.setInt(2, tagID);
					result = pst1.executeUpdate();
					if (result == 0) {
						System.out
								.println("Delete row in bookmark_tags_new fail");
						result = 0;
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Delete tag: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return result;
	}

	public static int copyBookmark(int userID, int bookmarkID) {
		int result = -1;
		int copiedBookmarkID = -1;
		Connection c = DBUtility.getConnection();
		String sql = "INSERT INTO bookmarks_new (bookmark_url, bookmark_title, bookmark_keywords, bookmark_description, userID, timestamp, view_count, rating,copy_count)"
				+ " SELECT bookmark_url, bookmark_title, bookmark_keywords, bookmark_description, ?, NOW(), 1, 0, 0 "
				+ " FROM bookmarks_new WHERE bookmarkID = ?";
		System.out.println("User ID: " + userID + " copied Bookmark ID: "
				+ bookmarkID);
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pst.setInt(1, userID);
			pst.setInt(2, bookmarkID);
			result = pst.executeUpdate();
			if (result == 0) {
				ResultSet rs = pst.getGeneratedKeys();
				if (rs.next()) {
					bookmarkID = (int) rs.getLong(1);
				}
			}
			sql = "INSERT INTO user_copy VALUES (?, ?, NOW(), ?)";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, bookmarkID);
			pst.setInt(3, copiedBookmarkID);
			result = pst.executeUpdate();
			sql = "UPDATE bookmarks_new SET copy_count = copy_count + 1 WHERE bookmarkID = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static int uncopyBookmark(int userID, int bookmarkID) {
		int result = -1;
		int copiedBookmarkID = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT copiedBookmarkID FROM user_copy WHERE userID = ? AND bookmarkID = ?";
		System.out.println("User ID: " + userID + " uncopied Bookmark ID: "
				+ bookmarkID);
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, bookmarkID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				copiedBookmarkID = rs.getInt(1);
			}
			sql = "DELETE FROM user_copy WHERE userID = ? AND bookmarkID = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, bookmarkID);
			pst.executeUpdate();
			sql = "DELETE FROM bookmarks_new WHERE bookmarkID = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, copiedBookmarkID);
			pst.executeUpdate();
			sql = "UPDATE bookmarks_new SET copy_count = copy_count - 1 WHERE bookmarkID = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static List<AdvanceBookmark> getTrendingBookmarks() {
		List<AdvanceBookmark> trendingBookmarks = new ArrayList<AdvanceBookmark>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM bookmarks_new ORDER BY rating DESC, view_count DESC, copy_count DESC, timestamp DESC LIMIT 15";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				AdvanceBookmark b = new AdvanceBookmark();
				int bookmarkID = rs.getInt(1);
				b.setBookmarkID(bookmarkID);
				b.setPostedUserID(rs.getInt(6));
				b.setUrl(rs.getString(2));
				b.setTitle(rs.getString(3));
				b.setDate(rs.getDate(7));
				b.setViewTimes(rs.getInt(8));
				b.setTotalRating(rs.getDouble(9));
				b.setCopyTimes(rs.getInt(10));
				trendingBookmarks.add(b);
			}
		} catch (SQLException e) {
			System.out.println("Get Trending Bookmarks: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return trendingBookmarks;
	}
}
