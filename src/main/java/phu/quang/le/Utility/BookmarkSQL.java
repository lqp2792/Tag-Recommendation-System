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
	public static int addBookmarkToDB (String url, String title, String urlKeywords,
			String urlDescription) {
		int result = -1;
		int bookmarkID = -1;
		Connection connection = DBUtility.getConnection ();
		String sql = "INSERT INTO bookmarks_new VALUES (default, ?, ?, ?, ?)";
		try {
			PreparedStatement pst = connection.prepareStatement (sql,
					Statement.RETURN_GENERATED_KEYS);
			pst.setString (1, url);
			pst.setString (2, title);
			pst.setString (3, urlKeywords);
			pst.setString (4, urlDescription);
			result = pst.executeUpdate ();
			if (result == 0) {
				System.err.println ("Can not add Bookmark to DB");
			} else {
				ResultSet rs = pst.getGeneratedKeys ();
				if (rs.next ()) {
					bookmarkID = (int) rs.getLong (1);
				}
			}
		} catch (SQLException e) {
			System.err.println ("Add Bookmark to DB " + e);
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
	public static int bookmarkHasTag (int bookmarkID, int tagID) {
		int tagWeight = -1;
		Connection c = DBUtility.getConnection ();
		String sql = "SELECT tag_weight FROM bookmark_tags_new WHERE bookmarkID = ? AND tagID = ?";
		try {
			PreparedStatement pst = c.prepareStatement (sql);
			pst.setInt (1, bookmarkID);
			pst.setInt (2, tagID);
			ResultSet rs = pst.executeQuery ();
			if (rs.next ()) {
				tagWeight = rs.getInt (1);
			}
		} catch (SQLException e) {
			System.err.println ("Check bookmark already has tag: " + e);
		}
		//
		return tagWeight;
	}
}
