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
import phu.quang.le.Model.Bookmark;
import phu.quang.le.Model.AdvanceBookmark;
import phu.quang.le.Model.JsonResponse;
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
			return new ModelAndView("index");
		} else {
			int userID = (int) session.getAttribute("userID");
			if (session.getAttribute("sortBy") == null) {
				session.setAttribute("sortBy", 4);
			}
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
		// size - 1 do sẽ có một phần tử trống ở cuối array
		for (int i = 0; i < tags.size() - 1; i++) {
			String tag = tags.get(i);
			int tagID = TagSQL.addTagToDB(userID, tag);
			result = TagSQL.addTagToBookmark(bookmarkID, tagID);
			if (result == 0) {
				System.err.println("Can not add tag to bookmark");
				rs.setStatus("FAIL");
			} else {
				rs.setStatus("SUCCESS");
				System.out.println("Added tag ID: " + tagID + " to bookmark");
			}
			result = UserSQL.userTaggedBookmark(userID, bookmarkID, tagID);
			if (result == 0) {
				System.err
						.println("Can not add information about user tag bookmark");
				rs.setStatus("FAIL");
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
			BookmarkSQL.bookmarkClick(bookmarkID);
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
}
