package phu.quang.le.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
		Connection connection = DBUtility.getConnection ();
		String sql = "INSERT INTO bookmarks_new VALUES (default, ?, ?, ?, ?)";
		try {
			PreparedStatement pst = connection.prepareStatement (sql);
			pst.setString (1, url);
			pst.setString (2, title);
			pst.setString (3, urlKeywords);
			pst.setString (4, urlDescription);
			result = pst.executeUpdate ();
		} catch (SQLException e) {
			System.err.println ("Add Bookmark to DB " + e);
		}
		return result;
	}
}
