package phu.quang.le.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import phu.quang.le.Model.AdditionalTag;
import phu.quang.le.Model.AdvanceBookmark;
import phu.quang.le.Model.TagWeight;

/**
 * @author phule
 *
 */
public class TagSQL {

	/**
	 * Check if the tag existed in database
	 * 
	 * @param tag
	 * @return
	 */
	public static int isTagExisted(String tag) {
		int tagID = -1;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM tags_new WHERE tag_content = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setString(1, tag);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				tagID = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Check Tag Existed: " + e);
		}
		//
		return tagID;
	}

	/**
	 * Add Tag to Database
	 * 
	 * @param tag
	 * @return tagID
	 */
	public static int addTagToDB(int userID, String tag) {
		int tagID = -1;
		if (tag.indexOf("#") == 0) {
			tag = tag.split("#")[1];
		}
		Connection c = DBUtility.getConnection();
		/* Kiểm tra nếu tag đã tồn tại trong DB */
		String sql = "SELECT tagID FROM tags_new WHERE tag_content = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			PreparedStatement pst1 = null;
			pst.setString(1, tag);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				tagID = Integer.parseInt(rs.getString(1));
				/* Lấy số lần sử dụng tag trong user_tag */
				sql = "SELECT * FROM user_tag WHERE userID = ? AND tagID = ?";
				pst1 = c.prepareStatement(sql);
				pst1.setInt(1, userID);
				pst1.setInt(2, tagID);
				ResultSet rs1 = pst1.executeQuery();
				if (rs1.next()) {
					int tagWeight = rs1.getInt(3);
					/* Đẩy tag weight + 1 khi sử dụng lại tag */
					sql = "UPDATE user_tag SET tag_weight = ? WHERE userID = ? AND tagID = ?";
					pst1 = c.prepareStatement(sql);
					pst1.setInt(1, tagWeight + 1);
					pst1.setInt(2, userID);
					pst1.setInt(3, tagID);
					pst1.executeUpdate();
				} else {
					pst1.close();
					/* Thêm thông tin user - tag */
					sql = "INSERT INTO user_tag VALUES (?, ?, 1)";
					pst1 = c.prepareStatement(sql);
					pst1.setInt(1, userID);
					pst1.setInt(2, tagID);
					pst1.executeUpdate();
				}

			} else {
				/* Nếu tag chưa tồn tại trong dữ liệu của người dùng này */
				sql = "INSERT INTO tags_new VALUES (default, ?) ";
				pst1 = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pst1.setString(1, tag);
				pst1.executeUpdate();
				ResultSet rs1 = pst1.getGeneratedKeys();
				rs1.next();
				tagID = (int) rs1.getLong(1);
				/* Thêm thông tin user - tag */
				sql = "INSERT INTO user_tag VALUES (?, ?, 1)";
				pst1 = c.prepareStatement(sql);
				pst1.setInt(1, userID);
				pst1.setInt(2, tagID);
				pst1.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Add tag to DB: " + e);
		}
		//
		return tagID;
	}

	/**
	 * Add Tag to Bookmark
	 * 
	 * @param tag
	 * @return
	 */
	public static int addTagToBookmark(int bookmarkID, int tagID) {
		Connection c = DBUtility.getConnection();
		int result = -1;
		int tagWeight = BookmarkSQL.bookmarkHasTag(bookmarkID, tagID);
		try {
			if (tagWeight == -1) {
				String sql = "INSERT INTO bookmark_tags_new VALUES (?, ?, 1)";
				PreparedStatement pst = c.prepareStatement(sql);
				pst.setInt(1, bookmarkID);
				pst.setInt(2, tagID);
				result = pst.executeUpdate();
			} else {
				String sql = "UPDATE bookmark_tags_new SET tag_weight = ? WHERE bookmarkID = ? AND tagID = ?";
				PreparedStatement pst = c.prepareStatement(sql);
				pst.setInt(1, tagWeight + 1);
				pst.setInt(2, bookmarkID);
				pst.setInt(3, tagID);
				result = pst.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Add Tag to Bookmark: " + e);
		}
		//
		return result;
	}

	public static List<AdditionalTag> getOtherTags(int postedUserID,
			int bookmarkID) {
		List<AdditionalTag> additionalTags = new ArrayList<AdditionalTag>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT DISTINCT userID FROM user_tag_bookmark WHERE bookmarkID = ? AND userID <> ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, bookmarkID);
			pst.setInt(2, postedUserID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				AdditionalTag additionalTag = new AdditionalTag();
				List<String> tags = new ArrayList<String>();
				sql = "SELECT first_name, last_name FROM users WHERE id = ?";
				PreparedStatement pst1 = c.prepareStatement(sql);
				pst1.setInt(1, rs.getInt(1));
				ResultSet rs1 = pst1.executeQuery();
				if (rs1.next()) {
					additionalTag.setUserID(rs.getInt(1));
					additionalTag.setFirstName(rs1.getString(1));
					additionalTag.setLastName(rs1.getString(2));
				}
				sql = "SELECT t.tag_content FROM tags_new t, user_tag_bookmark ut WHERE ut.userId = ? AND ut.bookmarkID = ? AND ut.tagID = t.tagID";
				pst1 = c.prepareStatement(sql);
				pst1.setInt(1, rs.getInt(1));
				pst1.setInt(2, bookmarkID);
				rs1 = pst1.executeQuery();
				while (rs1.next()) {
					tags.add(rs1.getString(1));
				}
				additionalTag.setTags(tags);
				additionalTags.add(additionalTag);
			}
		} catch (SQLException e) {
			System.err.println("Get Other Tag: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return additionalTags;
	}

	public static List<AdvanceBookmark> searchBookmarksByTag(int userID,
			String tag, int sortBy, int offset) {
		List<AdvanceBookmark> search = new ArrayList<AdvanceBookmark>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM bookmarks_new WHERE userID <> ? AND bookmarkID IN "
				+ "(SELECT DISTINCT bookmarkID FROM user_tag_bookmark utb, tags_new t "
				+ "WHERE t.tag_content = ? AND t.tagID = utb.tagID) ORDER BY ";
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
		sql += " LIMIT 5 OFFSET ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setString(2, tag);
			pst.setInt(3, offset);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				AdvanceBookmark bookmark = new AdvanceBookmark();
				List<String> tags = new ArrayList<String>();
				bookmark.setBookmarkID(rs.getInt(1));
				bookmark.setUrl(rs.getString(2));
				bookmark.setTitle(rs.getString(3));
				bookmark.setPostedUserID(rs.getInt(6));
				bookmark.setDate(rs.getDate(7));
				bookmark.setViewTimes(rs.getInt(8));
				bookmark.setTotalRating(rs.getDouble(9));
				bookmark.setCopyTimes(rs.getInt(10));
				System.out.println(UserSQL.isFollowed(userID, rs.getInt(6)));
				bookmark.setFriend(UserSQL.isFollowed(userID, rs.getInt(6)));
				String sql1 = "SELECT tag_content FROM user_tag_bookmark utb, tags_new t "
						+ "WHERE userID = ? AND bookmarkID = ? AND utb.tagID = t.tagID";
				PreparedStatement pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, rs.getInt(6));
				pst1.setInt(2, rs.getInt(1));
				ResultSet rs1 = pst1.executeQuery();
				while (rs1.next()) {
					tags.add(rs1.getString(1));
				}
				bookmark.setTags(tags);
				sql1 = "SELECT first_name, last_name FROM users WHERE id = ?";
				pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, rs.getInt(6));
				rs1 = pst1.executeQuery();
				if (rs1.next()) {
					bookmark.setFirstName(rs1.getString(1));
					bookmark.setLastName(rs1.getString(2));
				}
				//
				search.add(bookmark);
			}
		} catch (SQLException e) {
			System.err.println("Search Bookmark By Tag: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return search;
	}

	public static List<TagWeight> getTopMostUsedTags() {
		List<TagWeight> tagWeights = new ArrayList<TagWeight>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT t.tag_content, COUNT(ut.tag_weight) w FROM tags_new t, user_tag ut "
				+ "WHERE ut.tagID = t.tagID GROUP BY ut.tagID ORDER BY w DESC";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			int count = 0;
			while(rs.next() && count < 15) {
				TagWeight tw = new TagWeight(rs.getString(1), rs.getInt(2));
				tagWeights.add(tw);
				count++;
			}
		} catch (SQLException e) {
			System.err.println("Get Top Most Used Tags: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}
		return tagWeights;
	}
}
