package phu.quang.le.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.AdditionalTag;
import phu.quang.le.Model.AdvanceBookmark;
import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Model.OtherUser;
import phu.quang.le.Model.RateResult;
import phu.quang.le.Model.RecommendUser;
import phu.quang.le.Utility.BookmarkSQL;
import phu.quang.le.Utility.DBUtility;
import phu.quang.le.Utility.TagSQL;
import phu.quang.le.Utility.UserSQL;

@Controller
@RequestMapping(value = "/network")
public class NetworkController {
	public List<AdvanceBookmark> friendBookmaks = new ArrayList<AdvanceBookmark>();

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getNetworkView(HttpSession session, ModelMap map) {
		if (session.getAttribute("userID") == null) {
			return new ModelAndView("redirect:/");
		} else {
			int userID = (int) session.getAttribute("userID");
			if (session.getAttribute("sortBy") == null) {
				session.setAttribute("sortBy", 5);
			}
			List<RecommendUser> recommendUsers = UserSQL
					.getRecommendUsers(userID);
			ModelAndView dashboard = new ModelAndView("dashboard");
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
			rs.setResult(false);
		} else {
			sql = "INSERT INTO users_follow VALUES (?, ?)";
			System.out.println("UserID: " + userID + " followed UserID: "
					+ targetUserID);
			rs.setResult(true);
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
			DBUtility.closeConnection(c);
		}

		return rs;
	}

	@RequestMapping(value = "/getBookmarks", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getBookmarksFromFollowed(
			HttpSession session, @RequestParam int offset,
			@RequestParam int sortBy) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		if (sortBy == 0) {
			rs.setStatus("FAIL");
		} else {
			session.setAttribute("sortBy", sortBy);
			friendBookmaks = UserSQL.getAllBookmarksFromFriend(userID, offset,
					sortBy);
			System.out.println("Get bookmarks from friend: "
					+ friendBookmaks.toString());
			if (friendBookmaks.size() == 0) {
				rs.setStatus("EMPTY");
			} else {
				rs.setStatus("SUCCESS");
			}
			rs.setResult(friendBookmaks);
		}
		return rs;
	}

	@RequestMapping(value = "/addTags", method = RequestMethod.POST)
	public @ResponseBody JsonResponse addTag(HttpSession session,
			@RequestParam int bookmarkID, @RequestParam List<String> tags) {
		System.out.println("Adding tag to bookmark ID: " + bookmarkID);
		JsonResponse rs = new JsonResponse();
		int result = -1;
		int userID = (int) session.getAttribute("userID");
		for (int i = 0; i < tags.size(); i++) {
			String tag = tags.get(i);
			int tagID = TagSQL.addTagToDB(userID, tag);
			result = TagSQL.addTagToBookmark(bookmarkID, tagID);
			if (result == 0) {
				rs.setStatus("FAIL");
				rs.setResult("Could not add tag to bookmark");
				return rs;
			} else {
				rs.setStatus("SUCCESS");
				System.out.println("Added tag ID: " + tagID + " to bookmark");
			}
			result = UserSQL.userTaggedBookmark(userID, bookmarkID, tagID);
			if (result == 0) {
				rs.setStatus("FAIL");
				rs.setResult("Could not add information about user tag bookmark");
			} else {
				rs.setStatus("SUCCESS");
				System.out.println("Added information user added " + tagID
						+ " to bookmark");
			}
		}

		return rs;
	}

	@RequestMapping(value = "/getOtherTags", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getOtherTags(HttpSession session,
			@RequestParam int bookmarkID, @RequestParam int postedUserID) {
		JsonResponse rs = new JsonResponse();
		if (postedUserID == 0 || bookmarkID == 0) {
			rs.setStatus("FAIL");
		} else {
			List<AdditionalTag> additionalTags = TagSQL.getOtherTags(
					postedUserID, bookmarkID);
			if (additionalTags.size() == 0) {
				rs.setStatus("FAIL");
			} else {
				rs.setStatus("SUCCESS");
				rs.setResult(additionalTags);
			}
		}

		return rs;
	}

	@RequestMapping(value = "/linkClick", method = RequestMethod.POST)
	public @ResponseBody JsonResponse linkClick(HttpSession session,
			@RequestParam int bookmarkID) {
		JsonResponse rs = new JsonResponse();
		System.out.println("Click bookmarkd: " + bookmarkID);
		if (bookmarkID == 0) {
			rs.setStatus("FAIL");
		} else {
			rs.setStatus("SUCCESS");
			int clickCount = BookmarkSQL.bookmarkClick(bookmarkID);
			rs.setResult(clickCount);
		}
		return rs;
	}

	@RequestMapping(value = "/rateBookmark", method = RequestMethod.POST)
	public @ResponseBody JsonResponse rateBookmark(HttpSession session,
			@RequestParam int bookmarkID, @RequestParam double rating) {
		JsonResponse rs = new JsonResponse();
		System.out.println("Rate bookmark: " + bookmarkID + " " + "Rating: "
				+ rating);
		int userID = (int) session.getAttribute("userID");
		if (bookmarkID == 0) {
			rs.setStatus("FAIL");
		} else {
			double totalRating = BookmarkSQL.rateBookmark(userID, bookmarkID,
					rating);
			rs.setResult(totalRating);
		}
		return rs;
	}

	@RequestMapping(value = "/getRateResult", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getRateResult(HttpSession session,
			@RequestParam int bookmarkID) {
		JsonResponse rs = new JsonResponse();
		System.out.println("Get Rate Result Bookmark: " + bookmarkID);
		if (bookmarkID == 0) {
			rs.setStatus("FAIL");
		} else {

			RateResult rateResult = BookmarkSQL.getRateResult(bookmarkID);
			if (rateResult.getTotalCount() == 0) {
				rs.setStatus("EMPTY");
			} else {
				rs.setStatus("SUCCESS");
			}
			rs.setResult(rateResult);
		}

		return rs;
	}

	@RequestMapping(value = "/copyBookmark", method = RequestMethod.POST)
	public @ResponseBody JsonResponse copyBookmark(HttpSession session,
			@RequestParam int bookmarkID) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		int result = BookmarkSQL.copyBookmark(userID, bookmarkID);
		if (result != 0) {
			rs.setStatus("SUCCESS");
			rs.setResult(BookmarkSQL.getCopyTimes(bookmarkID));
		} else {
			rs.setStatus("FAIL");
		}
		return rs;
	}

	@RequestMapping(value = "/uncopyBookmark", method = RequestMethod.POST)
	public @ResponseBody JsonResponse uncopyBookmark(HttpSession session,
			@RequestParam int bookmarkID) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		int result = BookmarkSQL.uncopyBookmark(userID, bookmarkID);
		if (result != 0) {
			rs.setStatus("SUCCESS");
			rs.setResult(BookmarkSQL.getCopyTimes(bookmarkID));
		} else {
			rs.setStatus("FAIL");
		}
		return rs;
	}

	@RequestMapping(value = "/updateOnlineUsers", method = RequestMethod.GET)
	public @ResponseBody JsonResponse uncopyBookmark(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		rs.setResult(UserSQL.onlineUsers());
		return rs;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody JsonResponse search(HttpSession session,
			@RequestParam String searchInput, @RequestParam int sortBy) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		if (searchInput.charAt(0) == '#') {
			System.out.println("Search Tag: " + searchInput);
			session.setAttribute("sortBy", sortBy);
			List<AdvanceBookmark> bookmarks = TagSQL.search(searchInput,
					userID, sortBy);
			if (bookmarks == null) {
				rs.setStatus("FAIL");
				rs.setResult("Something wrong was happen!");
			} else {
				rs.setStatus("SUCCESS");
				rs.setResult(bookmarks);
			}
		} else if (searchInput.charAt(0) == '@') {
			List<OtherUser> users = UserSQL.search(searchInput, userID);
			if (users == null) {
				rs.setStatus("FAIL");
				rs.setResult("Something wrong was happen!");
			} else {
				rs.setStatus("SUCCESS");
				rs.setResult(users);
			}
		} else {
			BookmarkSQL.search(searchInput);
		}
		return rs;
	}

	@RequestMapping(value = "/deleteOtherTag", method = RequestMethod.POST)
	public @ResponseBody JsonResponse deleteOtherTag(HttpSession session,
			@RequestParam int bookmarkID, @RequestParam String otherTag) {
		JsonResponse rs = new JsonResponse();
		int result = -1;
		int userID = (int) session.getAttribute("userID");
		result = TagSQL.deleteOtherTag(userID, bookmarkID, otherTag);
		if (result > 0) {
			rs.setStatus("SUCCESS");
		} else {
			rs.setStatus("FAIL");
			rs.setResult("Something wrong was happen!");
		}
		return rs;
	}

	@RequestMapping(value = "/systemUsers", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getSystemUsers(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		List<Object> systemUsers = new ArrayList<Object>();
		int userID = (int) session.getAttribute("userID");
		systemUsers.add(UserSQL.getFollowing(userID));
		systemUsers.add(UserSQL.getFollower(userID));
		rs.setResult(systemUsers);
		return rs;
	}

	@RequestMapping(value = "/recommendBookmarks", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getRecommendBookmarks(
			HttpSession session, @RequestParam int bookmarkID) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		List<AdvanceBookmark> recommendBookmarks = BookmarkSQL
				.getRecommendBookmarks(bookmarkID, userID);
		if (recommendBookmarks.size() > 0) {
			rs.setStatus("SUCCESS");
		} else {
			rs.setStatus("EMPTY");
		}
		rs.setResult(recommendBookmarks);
		return rs;
	}
}
