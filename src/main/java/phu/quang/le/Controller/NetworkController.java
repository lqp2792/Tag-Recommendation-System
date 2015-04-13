package phu.quang.le.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.Bookmark;
import phu.quang.le.Model.FollowBookmark;
import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Model.RecommendUser;
import phu.quang.le.TopicModeling.ModelUtility;
import phu.quang.le.Utility.DBUtility;
import phu.quang.le.Utility.TagSQL;
import phu.quang.le.Utility.UserSQL;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

@Controller
@RequestMapping(value = "/network")
public class NetworkController {
	public List<FollowBookmark> friendBookmaks = new ArrayList<FollowBookmark>();

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getNetworView(HttpSession session, ModelMap map) {
		if (session.getAttribute("userID") == null) {
			return new ModelAndView("index");
		} else {
			int userID = (int) session.getAttribute("userID");
			List<RecommendUser> recommendUsers = UserSQL
					.getRecommendUsers(userID);
			System.out.println(recommendUsers.toString());
			Bookmark newBookmark = new Bookmark();
			ModelAndView dashboard = new ModelAndView("dashboard");
			dashboard.addObject("newBookmark", newBookmark);
			dashboard.addObject("recommendUsers", recommendUsers);
			dashboard.addObject("firstName", session.getAttribute("firstName"));
			dashboard.addObject("lastName", session.getAttribute("lastName"));

			return dashboard;
		}
	}

	@RequestMapping(value = "/follow", method = RequestMethod.POST)
	public @ResponseBody JsonResponse followUser(HttpSession session,
			@RequestParam int targetUserID) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		boolean isFollowed = UserSQL.isFollowed(userID, targetUserID);
		Connection c = DBUtility.getConnection();
		String sql = null;
		if (isFollowed) {
			sql = "DELETE FROM users_follow WHERE userIDa = ? AND userIDb = ?";
			System.out.println("UserID: " + userID + " unfollowed UserID: "
					+ targetUserID);
		} else {
			sql = "INSERT INTO users_follow VALUES (?, ?)";
			System.out.println("UserID: " + userID + " followed UserID: "
					+ targetUserID);
		}
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, targetUserID);
			pst.executeUpdate();
			rs.setStatus("SUCCESS");
		} catch (SQLException e) {
			rs.setStatus("FAIL");
			System.out.println("Follow: " + e);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				System.err.println("Follow: close connection: " + e);
			}
		}

		return rs;
	}

	@RequestMapping(value = "/getBookmarks", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getBookmarksFromFollowed(
			HttpSession session, @RequestParam int offset) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		friendBookmaks = UserSQL.getAllBookmarksFromFriend(userID, offset);
		System.out.println("Get bookmarks from friend: "
				+ friendBookmaks.toString());
		rs.setStatus("SUCCESS");
		rs.setResult(friendBookmaks);
		return rs;
	}

	@RequestMapping(value = "/addTags", method = RequestMethod.POST)
	public @ResponseBody JsonResponse addTag(HttpSession session,
			@RequestParam int bookmarkID, @RequestParam List<String> tags) {
		JsonResponse rs = new JsonResponse();
		System.out.println("adding tag");
		System.out.println(bookmarkID);
		System.out.println(tags.toString());
//		int result = -1;
//		int userID = (int) session.getAttribute("userID");
//		for (int i = 0; i < tags.size(); i++) {
//			String tag = tags.get(i);
//			int tagID = TagSQL.isTagExisted(tag);
//			if (tagID == -1) {
//				tagID = TagSQL.addTagToDB(userID, tag);
//			}
//			result = TagSQL.addTagToBookmark(bookmarkID, tagID);
//			if (result == 0) {
//				System.err.println("Can not add tag to bookmark");
//			} else {
//				System.out.println("Added tag ID: " + tagID + " to bookmark");
//			}
//			result = UserSQL.userTaggedBookmark(userID, bookmarkID, tagID);
//			if (result == 0) {
//				System.err
//						.println("Can not add information about user tag bookmark");
//			} else {
//				System.out.println("Added information user added " + tagID
//						+ " to bookmark");
//			}
//		}

		return rs;
	}

	@RequestMapping(value = "/getAlphabet", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getAlphabet()
			throws FileNotFoundException, ClassNotFoundException, IOException {
		JsonResponse rs = new JsonResponse();
		List<String> availableTags = new ArrayList<String>();
		try {
			InstanceList instances = InstanceList.load(new File(
					ModelUtility.class.getClassLoader()
							.getResource("Instance.lda").toURI()));
			Alphabet dataAlphabet = instances.getDataAlphabet();
			ParallelTopicModel model = ModelUtility.getTopicModel();
			ArrayList<TreeSet<IDSorter>> topicSortedWords = model
					.getSortedWords();
			for (int i = 0; i < model.getNumTopics(); i++) {
				Iterator<IDSorter> iterator = topicSortedWords.get(i)
						.iterator();
				int rank = 0;
				while (iterator.hasNext() && rank < 25) {
					IDSorter idCountPair = iterator.next();
					availableTags.add((String) dataAlphabet
							.lookupObject(idCountPair.getID()));
					rank++;
				}
			}
			rs.setStatus("SUCCESS");
			rs.setResult(availableTags);
		} catch (URISyntaxException e) {
			rs.setStatus("FAIL");
			System.err.println("Get Alphabet: " + e);
		}
		return rs;
	}
}
