package phu.quang.le.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import phu.quang.le.Model.AdvanceBookmark;
import phu.quang.le.Model.Bookmark;
import phu.quang.le.Model.OnlineHistory;
import phu.quang.le.Model.RecommendUser;
import phu.quang.le.Model.OtherUser;
import phu.quang.le.Model.TagWeight;
import phu.quang.le.Model.User;

/**
 * @author phule
 *
 */
public class UserSQL {
	public static String getFirstNameByID(int userID) {
		String firstName = null;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT first_name FROM users WHERE id = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				firstName = rs.getString(1);
			}
		} catch (SQLException e) {
			System.out.println("Get First Name Exception: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return firstName;
	}

	public static String getLastNameByID(int userID) {
		String lastName = null;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT last_name FROM users WHERE id = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				lastName = rs.getString(1);
			}
		} catch (SQLException e) {
			System.out.println("Get First Name Exception: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return lastName;
	}

	public static int updateLoginHistory(int userID) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM login_history WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				sql = "UPDATE login_history SET login_count = login_count + 1, lastest_login = CURRENT_TIMESTAMP WHERE userID = ?";
				PreparedStatement pst1 = c.prepareStatement(sql);
				pst1.setInt(1, userID);
				result = pst1.executeUpdate();
			} else {
				sql = "INSERT INTO login_history VALUES(?, default, default)";
				PreparedStatement pst1 = c.prepareStatement(sql);
				pst1.setInt(1, userID);
				result = pst1.executeUpdate();
			}
			sql = "UPDATE users SET online = 0 WHERE id = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Update Login History Exception: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return result;
	}

	public static boolean isAccountExisted(String email) {
		boolean isExisted = false;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM users WHERE email = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setString(1, email);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				isExisted = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}
		return isExisted;
	}

	public static int registerAccount(String firstName, String lastName,
			String email, String password) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		String sql = "INSERT INTO users VALUES (default, ?, ?, ?, ?, default)";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setString(1, email);
			pst.setString(2, password);
			pst.setString(3, firstName);
			pst.setString(4, lastName);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}
		return result;
	}

	/**
	 * Add information about user tagged a bookmark
	 * 
	 * @param userID
	 * @param bookmarkID
	 * @param tagID
	 * @return
	 */
	public static int userTaggedBookmark(int userID, int bookmarkID, int tagID) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		String sql = "INSERT INTO user_tag_bookmark VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, bookmarkID);
			pst.setInt(3, tagID);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Add user tagged bookmark: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return result;
	}

	public static User userStatistic(int userID) {
		User u = new User();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT COUNT(userID) FROM bookmarks_new WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				u.setBookmarkCount(rs.getInt(1));
			}
			sql = "SELECT COUNT(userIDa) FROM users_follow WHERE userIDa = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			rs = pst.executeQuery();
			if (rs.next()) {
				u.setFollowingCount(rs.getInt(1));
			}
			sql = "SELECT COUNT(userIDb) FROM users_follow WHERE userIDb = ?";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			rs = pst.executeQuery();
			if (rs.next()) {
				u.setFollowerCount(rs.getInt(1));
			}
		} catch (SQLException e) {
			System.err.println("User Statistics: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return u;
	}

	public static List<Bookmark> getAllBookmark(int userID, int offset) {
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM bookmarks_new WHERE userID = ? LIMIT ?";
		PreparedStatement pst;
		try {
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, offset + 5);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Bookmark b = new Bookmark();
				List<String> tags = new ArrayList<String>();
				int bookmarkID = rs.getInt(1);
				b.setBookmarkID(rs.getInt(1));
				b.setUrl(rs.getString(2));
				b.setTitle(rs.getString(3));
				b.setDate(rs.getDate(7));
				b.setViewTimes(rs.getInt(8));
				b.setTotalRating(rs.getDouble(9));
				b.setCopyTimes(rs.getInt(10));
				String sql1 = "SELECT tag_content FROM bookmark_tags_new bt, tags_new t "
						+ "WHERE bookmarkID = ? AND bt.tagID = t.tagID";
				PreparedStatement pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, bookmarkID);
				ResultSet rs1 = pst1.executeQuery();
				while (rs1.next()) {
					tags.add(rs1.getString(1));
				}
				b.setTags(tags);
				b.setRated(UserSQL.getRateByUserID(userID, bookmarkID));
				bookmarks.add(b);
			}
		} catch (SQLException e) {
			System.out.println("Get All Bookmark: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return bookmarks;
	}

	public static List<TagWeight> getMostUsedTags(int userID) {
		List<TagWeight> mostUsedTags = new ArrayList<TagWeight>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT tag_content, tag_weight FROM user_tag, tags_new "
				+ "WHERE userID = ? AND user_tag.tagID = tags_new.tagID AND tag_weight > 1 "
				+ "ORDER BY tag_weight DESC LIMIT 5";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				TagWeight t = new TagWeight(rs.getString(1), rs.getInt(2));
				mostUsedTags.add(t);
			}
		} catch (SQLException e) {
			System.out.println("Get Most Used Tags: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return mostUsedTags;
	}

	public static int getUserID(String firstName, String lastName) {
		int userID = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT id FROM users WHERE first_name = ? AND last_name =?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setString(1, firstName);
			pst.setString(2, lastName);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				userID = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Get User ID: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return userID;
	}

	/**
	 * Get Tag subscription
	 * 
	 * @param userID
	 * @return
	 */
	public static List<String> getTagSubscription(int userID) {
		List<String> tagSubscription = new ArrayList<>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT subscription FROM user_subscription WHERE userID = ?";

		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				tagSubscription.add(rs.getString(1));
			}
		} catch (SQLException e) {
			System.err.println("Get Tags Subscription: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return tagSubscription;
	}

	/**
	 * Gợi ý những người dùng phù hợp nhất cho người dùng follow
	 * 
	 * @param userID
	 * @return
	 */
	public static List<RecommendUser> getRecommendUsers(int userID) {
		List<RecommendUser> recommendUsers = new ArrayList<RecommendUser>();
		/* Kiểm tra theo số lượng tag giống nhau */
		Connection c = DBUtility.getConnection();
		List<Integer> usedTagIDs = new ArrayList<Integer>();
		List<Integer> usedTopicIDs = new ArrayList<Integer>();
		try {
			String sql = "SELECT tagID FROM user_tag WHERE userID = ?";
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				usedTagIDs.add(rs.getInt(1));
			}
			System.out.println("used tag ids: " + usedTagIDs);
			sql = "SELECT topicID FROM bookmark_topics bt, bookmarks_new b WHERE userID = ?"
					+ " AND b.bookmarkID = bt.bookmarkID";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (!usedTopicIDs.contains(rs.getInt(1))) {
					usedTopicIDs.add(rs.getInt(1));
				}
			}
			System.out.println("used topic ids: " + usedTopicIDs);
			/*
			 * Sau khi lấy xong ID của các tag, các topic mà người dùng đã sử
			 * dụng, bắt đầu so sánh với các ng dùng khác
			 */
			sql = "SELECT * FROM users WHERE id <> ? AND "
					+ "id NOT IN (SELECT userIDb FROM users_follow WHERE userIDa = ?)";
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, userID);
			rs = pst.executeQuery();
			while (rs.next()) {
				int targetUserID = rs.getInt(1);
				System.out.println("Target user ID: " + targetUserID);
				RecommendUser user = new RecommendUser();
				List<Integer> targetUsedTagIDs = new ArrayList<Integer>();
				List<Integer> targetUsedTopicIDs = new ArrayList<Integer>();
				/* Lấy danh sách ID các tag đã sử dụng của đối tượng */
				String sql1 = "SELECT tagID FROM user_tag WHERE userID = ?";
				PreparedStatement pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, targetUserID);
				ResultSet rs1 = pst1.executeQuery();
				while (rs1.next()) {
					targetUsedTagIDs.add(rs1.getInt(1));
				}
				System.out.println("target used tag ids: " + targetUsedTagIDs);
				/* Lấy danh sách ID các topic đã sử dụng của đối tượng */
				sql1 = "SELECT topicID FROM bookmark_topics bt, bookmarks_new b WHERE userID = ?"
						+ " AND b.bookmarkID = bt.bookmarkID";
				pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, targetUserID);
				rs1 = pst1.executeQuery();
				while (rs1.next()) {
					if (!targetUsedTopicIDs.contains(rs1.getInt(1))) {
						targetUsedTopicIDs.add(rs1.getInt(1));
					}
				}
				System.out.println("target used topic ids: "
						+ targetUsedTopicIDs);
				int sameTagCount = 0;
				int sameTopicCount = 0;
				/* So sánh 2 list tag ID của 2 người */
				for (int i = 0; i < targetUsedTagIDs.size(); i++) {
					int tagID = targetUsedTagIDs.get(i);
					sameTagCount += Collections.frequency(usedTagIDs, tagID);
				}
				/* So sánh 2 list topic ID của 2 người */
				for (int i = 0; i < targetUsedTopicIDs.size(); i++) {
					int topicID = targetUsedTopicIDs.get(i);
					sameTagCount += Collections
							.frequency(usedTopicIDs, topicID);
				}
				/* Nếu cố tag trùng và topic trùng > 0 thì có thể đưa vào gợi ý */
				if (sameTagCount > 0 || sameTopicCount > 0) {
					user.setUserID(targetUserID);
					user.setFirstName(rs.getString(4));
					user.setLastName(rs.getString(5));
					user.setMostUsedTags(getMostUsedTags(targetUserID));
					user.setMostUsedTopics(getMostInterestedTopics(targetUserID));
					recommendUsers.add(user);

				}
			}
		} catch (SQLException e) {
			System.out.println("Get Recommend Users: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		/* Kiểm tra theo số lượng topic giống nhau */
		Collections.sort(recommendUsers, new Comparator<RecommendUser>() {
			public int compare(RecommendUser u1, RecommendUser u2) {
				return (u1.getSameTagCount() - u2.getSameTagCount())
						+ (u1.getSameTopicCount() - u2.getSameTopicCount()) * 5;
			}
		});
		return recommendUsers;
	}

	/**
	 * Lấy ra top những topic ID mà người dùng quan tâm nhất
	 * 
	 * @param userID
	 * @return
	 */
	public static List<Integer> getMostInterestedTopics(int userID) {
		List<Integer> mostInterestedTopics = new ArrayList<Integer>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT bt.topicID, COUNT(topicID) c FROM bookmarks_new b, bookmark_topics bt WHERE userID = ? "
				+ "AND b.bookmarkID = bt.bookmarkID GROUP BY topicID ORDER BY c DESC LIMIT 5";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next() && mostInterestedTopics.size() <= 5) {
				mostInterestedTopics.add(rs.getInt(1));
			}
			c.close();
		} catch (SQLException e) {
			System.out.println("Get most interested topics: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return mostInterestedTopics;
	}

	/**
	 * Kiểm tra 2 người có quan hệ Follow hay không
	 * 
	 * @param userIDa
	 *            ID của người đầu tiên
	 * @param userIDb
	 *            ID của người thứ 2
	 * @return true / false
	 */
	public static boolean isFollowed(int userIDa, int userIDb) {
		boolean isFollowed = false;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM users_follow WHERE userIDa = ? AND userIDb = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userIDa);
			pst.setInt(2, userIDb);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				isFollowed = true;
			}
		} catch (SQLException e) {
			System.out.println("Check Follow: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return isFollowed;
	}

	public static List<AdvanceBookmark> getAllBookmarksFromFriend(int userID,
			int offset, int sortBy) {
		CompareUltility compareUltility = new CompareUltility(sortBy, userID);
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM bookmarks_new WHERE "
				+ " userID IN (SELECT userIDb FROM users_follow WHERE userIDa = ?)";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				AdvanceBookmark b = new AdvanceBookmark();
				int bookmarkID = rs.getInt(1);
				b.setBookmarkID(bookmarkID);
				b.setPostedUserID(rs.getInt(6));
				b.setFirstName(UserSQL.getFirstNameByID(rs.getInt(6)));
				b.setLastName(UserSQL.getLastNameByID(rs.getInt(6)));
				b.setUrl(rs.getString(2));
				b.setTitle(rs.getString(3));
				b.setTags(BookmarkSQL.getTaggedTags(rs.getInt(1), rs.getInt(6)));
				b.setSameTags(BookmarkSQL.sameTags(
						UserSQL.getAllUsedTags(userID), b.getTags()));
				b.setDate(rs.getDate(7));
				b.setCopyTimes(rs.getInt(10));
				b.setViewTimes(rs.getInt(8));
				b.setTotalRating(rs.getDouble(9));
				b.setRated(UserSQL.getRateByUserID(userID, rs.getInt(1)));
				b.setFriend(UserSQL.isFollowed(userID, rs.getInt(6)));
				b.setCopied(BookmarkSQL.isCopied(userID, rs.getInt(1)));
				b.setPoint(0);
				b.setRatedTimes(BookmarkSQL.getRatedTimes(rs.getInt(1)));
				compareUltility.calculatePoint(b);
			}
			compareUltility.sort();
		} catch (SQLException e) {
			System.out.println("Get All Bookmark from friend: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return compareUltility.getSortedBookmarksByOffset(offset);
	}

	public static int editSubscription(int userID,
			List<String> subscriptionTags, List<String> deletedTags) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		if (subscriptionTags != null) {
			String sql = "INSERT INTO user_subscription VALUES (?, ?)";
			System.out
					.println("Insert subscription tags into user_subscription");
			try {
				PreparedStatement pst = c.prepareStatement(sql);
				pst.setInt(1, userID);
				for (int i = 0; i < subscriptionTags.size(); i++) {
					pst.setString(2, subscriptionTags.get(i));
					result = pst.executeUpdate();
					if (result < 1) {
						System.err.println("Insert subscription tags fail!");
					}
				}
			} catch (SQLException e) {
				System.err.println("Insert subscription tags exception: " + e);
			}
		} else {
			System.out.println("Subscription tags is empty");
		}
		if (deletedTags != null) {
			String sql = "DELETE FROM user_subscription WHERE userID = ? AND subscription IN (";
			for (int i = 0; i < deletedTags.size() - 1; i++) {
				sql += "?, ";
			}
			sql += "?)";
			try {
				System.out
						.println("Delete subscription tags from user_subscription");
				PreparedStatement pst = c.prepareStatement(sql);
				pst.setInt(1, userID);
				for (int i = 0; i < deletedTags.size(); i++) {
					pst.setString(i + 2, deletedTags.get(i));
				}
				result = pst.executeUpdate();
				if (result < 1) {
					System.err.println("Delete subscription tags fail!");
				}
			} catch (SQLException e) {
				System.err.println("Delete subscription tags exception: " + e);
			}
		} else {
			System.out.println("Deleted subscription tags is empty");
		}

		DBUtility.closeConnection(c);
		return result;
	}

	public static OnlineHistory getLoginHistory(int userID) {
		OnlineHistory history = new OnlineHistory();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM login_history WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				history.setUserID(userID);
				history.setOnlineCount(rs.getInt(2));
				history.setLastedOnline((new Date(rs.getTimestamp(3).getTime()))
						.toString());
			}
		} catch (SQLException e) {
			System.err.println("Get Login History Exception : " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return history;
	}

	public static int onlineUsers() {
		int onlineUsers = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT COUNT(online) FROM users WHERE online = 1";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				onlineUsers = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Get Online Users Exception: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return onlineUsers;
	}

	public static List<OtherUser> search(String searchInput, int userID) {
		List<OtherUser> users = new ArrayList<OtherUser>();
		String search = searchInput.substring(1).trim();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM users WHERE ( lower(email) LIKE '%"
				+ search + "%' OR lower(first_name) LIKE '%" + search + "%' "
				+ "OR lower(last_name) LIKE '%" + search + "%') AND id <> ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				OtherUser u = new OtherUser();
				int targetID = rs.getInt(1);
				u.setUserID(targetID);
				u.setFirstName(rs.getString(4));
				u.setLastName(rs.getString(5));
				if (rs.getInt(6) == 1) {
					u.setOnline(true);
				} else {
					u.setOnline(false);
				}
				u.setMostUsedTags(getMostUsedTags(targetID));
				u.setFollowed(isFollowed(userID, targetID));
				User user = userStatistic(targetID);
				u.setFollowerCount(user.getFollowerCount());
				u.setFollowingCount(user.getFollowingCount());
				u.setBookmarkCount(user.getBookmarkCount());
				users.add(u);
			}
		} catch (SQLException e) {
			System.err.println("Search User Exception: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return users;
	}

	public static double getRateByUserID(int userID, int bookmarkID) {
		double rated = 0;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT rating FROM user_rating WHERE userID = ? AND bookmarkID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, bookmarkID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				rated = rs.getDouble(1);
			}
		} catch (SQLException e) {
			System.err.println("Get Rate by UserID: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return rated;
	}

	public static List<TagWeight> getAllUsedTags(int userID) {
		List<TagWeight> usedTags = new ArrayList<TagWeight>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT tag_content, tag_weight FROM user_tag, tags_new "
				+ "WHERE userID = ? AND user_tag.tagID = tags_new.tagID "
				+ "ORDER BY tag_weight DESC";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				TagWeight t = new TagWeight(rs.getString(1), rs.getInt(2));
				usedTags.add(t);
			}
		} catch (SQLException e) {
			System.out.println("Get Most Used Tags: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return usedTags;
	}

	public static List<OtherUser> getFollowing(int userID) {
		List<OtherUser> following = new ArrayList<OtherUser>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM users u, users_follow uf WHERE u.id = uf.userIDb AND uf.userIDa = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				OtherUser u = new OtherUser();
				int targetID = rs.getInt(1);
				u.setUserID(targetID);
				u.setFirstName(rs.getString(4));
				u.setLastName(rs.getString(5));
				if (rs.getInt(6) == 1) {
					u.setOnline(true);
				} else {
					u.setOnline(false);
				}
				u.setMostUsedTags(getMostUsedTags(targetID));
				u.setFollowed(true);
				User user = userStatistic(targetID);
				u.setFollowerCount(user.getFollowerCount());
				u.setFollowingCount(user.getFollowingCount());
				u.setBookmarkCount(user.getBookmarkCount());
				following.add(u);
			}
		} catch (SQLException e) {
			System.err.println("Search User Exception: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return following;
	}

	public static List<OtherUser> getFollower(int userID) {
		List<OtherUser> follower = new ArrayList<OtherUser>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM users u, users_follow uf WHERE u.id = uf.userIDa AND uf.userIDb = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				OtherUser u = new OtherUser();
				int targetID = rs.getInt(1);
				u.setUserID(targetID);
				u.setFirstName(rs.getString(4));
				u.setLastName(rs.getString(5));
				if (rs.getInt(6) == 1) {
					u.setOnline(true);
				} else {
					u.setOnline(false);
				}
				u.setMostUsedTags(getMostUsedTags(targetID));
				u.setFollowed(isFollowed(userID, targetID));
				User user = userStatistic(targetID);
				u.setFollowerCount(user.getFollowerCount());
				u.setFollowingCount(user.getFollowingCount());
				u.setBookmarkCount(user.getBookmarkCount());
				follower.add(u);
			}
		} catch (SQLException e) {
			System.err.println("Search User Exception: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return follower;
	}
}
