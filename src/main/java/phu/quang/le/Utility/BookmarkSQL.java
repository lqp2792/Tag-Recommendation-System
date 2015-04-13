package phu.quang.le.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import phu.quang.le.Model.Bookmark;
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
		String sql = "INSERT INTO bookmarks_new VALUES (default, ?, ?, ?, ?, ?, NOW())";
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

	public static List<Bookmark> discoverBookmarks(
			List<Integer> subscriptionTopicIDs, int userID) {
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		Connection c = DBUtility.getConnection();
		/* Tạo SQL để sắp xếp các Bookmark theo thứ tự */
		String sql = "SELECT * FROM bookmarks_new b LEFT JOIN (SELECT bookmarkID, SUM(topicProbability) as s FROM bookmark_topics WHERE topicID IN (";
		for (int i = 0; i < subscriptionTopicIDs.size() - 1; i++) {
			sql += "?,";
		}
		sql += "?) GROUP BY bookmarkID) as t ON b.bookmarkID = t.bookmarkID AND b.userID <> userID ORDER BY t.s DESC LIMIT 10";
		System.out.println("SQL: " + sql);
		/* ================================================ */
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			for (int i = 0; i < subscriptionTopicIDs.size(); i++) {
				pst.setInt(i + 1, subscriptionTopicIDs.get(i));
			}
			System.out.println("PST: " + pst.toString());
			ResultSet rs = pst.executeQuery();
			System.out.println("Result :");
			while (rs.next()) {
				System.out.println(rs.getInt(1) + " - " + rs.getString(2)
						+ " - " + rs.getString(3));
				Bookmark b = new Bookmark();
				b.setUrl(rs.getString(2));
				b.setTitle(rs.getString(3));
				sql = "SELECT tag_content FROM bookmark_tags_new, tags_new "
						+ "WHERE bookmarkID = ? AND bookmark_tags_new.tagID = tags_new.tagID";
				PreparedStatement pst1 = c.prepareStatement(sql);
				pst1.setInt(1, Integer.parseInt(rs.getString(1)));
				ResultSet rs1 = pst1.executeQuery();
				List<String> tags = new ArrayList<String>();
				while (rs1.next()) {
					tags.add(rs1.getString(1));
				}
				b.setTags(tags);
				bookmarks.add(b);
			}
		} catch (SQLException e) {
			System.err.println("Discover Bookmark: " + e);
		}
		return bookmarks;
	}
}
