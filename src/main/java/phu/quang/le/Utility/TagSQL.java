package phu.quang.le.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	public static boolean isTagExisted (String tag) {
		boolean isExisted = false;
		Connection c = DBUtility.getConnection ();
		String sql = "SELECT * FROM tags_new WHERE tag_content = ?";
		try {
			PreparedStatement pst = c.prepareStatement (sql);
			pst.setString (1, tag);
			ResultSet rs = pst.executeQuery ();
			if (rs.next ()) {
				isExisted = true;
			}
		} catch (SQLException e) {
			System.err.println ("Check Tag Existed: " + e);
		}
		//
		return isExisted;
	}
}
