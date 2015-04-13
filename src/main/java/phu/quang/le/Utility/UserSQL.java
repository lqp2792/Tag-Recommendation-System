package phu.quang.le.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import phu.quang.le.Model.Bookmark;
import phu.quang.le.Model.FollowBookmark;
import phu.quang.le.Model.RecommendUser;
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
		String sql = "SELECT * FROM bookmarks_new WHERE userID = ?";
		PreparedStatement pst;
		try {
			pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Bookmark b = new Bookmark();
				List<String> tags = new ArrayList<String>();
				int bookmarkID = rs.getInt(1);
				b.setUrl(rs.getString(2));
				b.setTitle(rs.getString(3));
				b.setDate(rs.getDate(7));
				String sql1 = "SELECT tag_content FROM bookmark_tags_new bt, tags_new t "
						+ "WHERE bookmarkID = ? AND bt.tagID = t.tagID";
				PreparedStatement pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, bookmarkID);
				ResultSet rs1 = pst1.executeQuery();
				while (rs1.next()) {
					tags.add(rs1.getString(1));
				}
				b.setTags(tags);
				bookmarks.add(b);
			}
		} catch (SQLException e) {
			System.out.println("Get All Bookmark: " + e);
		}

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
			try {
				c.close();
			} catch (SQLException e) {
				System.err.println("Is Followed - Close connection: " + e);
			}
		}

		return isFollowed;
	}

	public static List<FollowBookmark> getAllBookmarksFromFriend(int userID,
			int offset) {
		List<FollowBookmark> bookmarks = new ArrayList<FollowBookmark>();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM bookmarks_new WHERE userID IN "
				+ "(SELECT userIDb FROM users_follow WHERE userIDa = ?) ORDER BY timestamp LIMIT 5 OFFSET ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, offset);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				FollowBookmark b = new FollowBookmark();
				List<String> tags = new ArrayList<String>();
				int bookmarkID = rs.getInt(1);
				b.setBookmarkID(bookmarkID);
				b.setUrl(rs.getString(2));
				b.setTitle(rs.getString(3));
				b.setDate(rs.getDate(7));
				String sql1 = "SELECT tag_content FROM bookmark_tags_new bt, tags_new t "
						+ "WHERE bookmarkID = ? AND bt.tagID = t.tagID";
				PreparedStatement pst1 = c.prepareStatement(sql1);
				pst1.setInt(1, bookmarkID);
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
				//
				bookmarks.add(b);
			}
		} catch (SQLException e) {
			System.out.println("Get All Bookmark from friend: " + e);
		}

		return bookmarks;
	}
}
