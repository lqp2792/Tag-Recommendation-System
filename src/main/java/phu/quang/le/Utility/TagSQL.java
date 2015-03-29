package phu.quang.le.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author phule
 *
 */
/**
 * @author phule
 *
 */
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
		tag = tag.split("#")[1];
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
				rs1.next();
				int tagWeight = rs1.getInt(3);
				/* Đẩy tag weight + 1 khi sử dụng lại tag */
				sql = "UPDATE user_tag SET tag_weight = ? WHERE userID = ? AND tagID = ?";
				pst1 = c.prepareStatement(sql);
				pst1.setInt(1, tagWeight+1);
				pst1.setInt(2, userID);
				pst1.setInt(3, tagID);
				pst1.executeUpdate();
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
				pst.setInt(1, tagWeight++);
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
}
