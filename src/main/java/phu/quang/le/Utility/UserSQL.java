package phu.quang.le.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import phu.quang.le.Model.Bookmark;
import phu.quang.le.Model.TagWeight;

/**
 * @author phule
 *
 */
public class UserSQL {

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
		}
		//
		return result;
	}

	public static List<Bookmark> getAllBookmark(int userID) {
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT DISTINCT bookmarkID FROM user_tag_bookmark WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Bookmark b = new Bookmark();
				List<String> tags = new ArrayList<String>();
				int bookmarkID = rs.getInt(1);
				sql = "SELECT * FROM bookmarks_new WHERE bookmarkID = ?";
				PreparedStatement pst1 = c.prepareStatement(sql);
				pst1.setInt(1, bookmarkID);
				ResultSet rs1 = pst1.executeQuery();
				if (rs1.next()) {
					b.setUrl(rs1.getString(2));
					b.setTitle(rs1.getString(3));
				}
				sql = "SELECT tag_content FROM bookmark_tags_new, tags_new "
						+ "WHERE bookmarkID = ? AND bookmark_tags_new.tagID = tags_new.tagID";
				pst1 = c.prepareStatement(sql);
				pst1.setInt(1, bookmarkID);
				rs1 = pst1.executeQuery();
				while (rs1.next()) {
					tags.add(rs1.getString(1));
				}
				b.setTags(tags);
				bookmarks.add(b);
			}
		} catch (SQLException e) {
			System.err.println("Get all bookmark: " + e);
		}
		//
		return bookmarks;
	}

	public static List<TagWeight> getMostUsedTags(int userID) {
		List<TagWeight> mostUsedTags = new ArrayList<TagWeight>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT tag_content, tag_weight FROM user_tag, tags_new "
				+ "WHERE userID = ? AND user_tag.tagID = tags_new.tagID "
				+ "ORDER BY tag_weight DESC LIMIT 5";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			System.out.println(pst.toString());
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				TagWeight t = new TagWeight(rs.getString(1), rs.getInt(2));
				System.out.println(t);
				mostUsedTags.add(t);
			}
		} catch (SQLException e) {
			System.out.println("Get Most Used Tags: " + e);
		}
		//
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
			System.out.println(pst.toString());
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				userID = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Get User ID: " + e);
		}
		return userID;
	}
}
