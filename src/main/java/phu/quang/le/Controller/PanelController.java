package phu.quang.le.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.AdditionalTag;
import phu.quang.le.Model.Bookmark;
import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Model.OnlineHistory;
import phu.quang.le.Model.RecommendTag;
import phu.quang.le.Model.Topic;
import phu.quang.le.Model.User;
import phu.quang.le.TopicModeling.ModelLoadThread;
import phu.quang.le.TopicModeling.ModelUtility;
import phu.quang.le.Utility.BookmarkSQL;
import phu.quang.le.Utility.DBUtility;
import phu.quang.le.Utility.TagSQL;
import phu.quang.le.Utility.UrlUtility;
import phu.quang.le.Utility.UserSQL;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

@Controller
@RequestMapping(value = "/dashboard")
public class PanelController {
	public List<Topic> topics = new ArrayList<Topic>();
	public static List<String> availableTags = new ArrayList<String>();

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getDashBoard(HttpSession session)
			throws FileNotFoundException, ClassNotFoundException, IOException {
		if (session.getAttribute("firstName") == null) {
			System.out.println("Session does not exist -> index");
			return new ModelAndView("redirect:/");
		} else {
			System.out.println("Get Dashboard Page");
			if (!ModelUtility.isLoaded && !ModelUtility.isLoading) {
				(new ModelLoadThread()).start();
			}
			if (session.getAttribute("sortBy") == null) {
				session.setAttribute("sortBy", 5);
			}
			int userID = (int) session.getAttribute("userID");
			User u = UserSQL.userStatistic(userID);
			ModelAndView dashboard = new ModelAndView("dashboard");
			dashboard.addObject("user", u);
			dashboard.addObject("firstName", session.getAttribute("firstName"));
			dashboard.addObject("lastName", session.getAttribute("lastName"));
			return dashboard;
		}

	}

	@RequestMapping(value = "/getBookmarks", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getBookmarks(HttpSession session,
			@RequestParam int offset) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		List<Bookmark> bookmarks = UserSQL.getAllBookmark(userID, offset);
		if (bookmarks.size() == 0) {
			rs.setStatus("EMPTY");
		} else {
			rs.setStatus("SUCCESS");
		}
		rs.setResult(bookmarks);
		return rs;
	}

	@RequestMapping(value = "/checkBookmark", method = RequestMethod.POST)
	@ResponseBody
	public JsonResponse checkBookmark(String url, ModelMap modelMap) {
		JsonResponse res = new JsonResponse();
		UrlValidator urlValidator = new UrlValidator();
		if (url == null) {
			res.setStatus("FAIL");
			res.setResult("Url can not be blank");
			return res;
		} else {
			if (urlValidator.isValid(url)) {
				res.setStatus("SUCCESS");
				Bookmark newBookmark = new Bookmark();
				newBookmark.setUrl(url);
				try {
					Response response= Jsoup.connect(url)
					           .ignoreContentType(true)
					           .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")  
					           .referrer("http://www.google.com")   
					           .timeout(0) 
					           .followRedirects(true)
					           .execute();

					Document doc = response.parse();
					String title = doc.title();
					newBookmark.setTitle(title);
					modelMap.addAttribute("newBookmark", newBookmark);
					res.setResult(newBookmark);
				} catch (IOException e) {
					e.printStackTrace();
					res.setStatus("FAIL");
					res.setResult("Something has happend while loading URL");
				}
			} else {
				res.setStatus("FAIL");
				res.setResult("Url is invalid");

			}
		}
		return res;
	}

	@RequestMapping(value = "/gettags", method = RequestMethod.POST)
	@ResponseBody
	public JsonResponse addBookmarkTags(@RequestParam String url,
			@RequestParam String title) throws FileNotFoundException,
			ClassNotFoundException, IOException, URISyntaxException {
		JsonResponse res = new JsonResponse();
		if (url.isEmpty() || title.isEmpty()) {
			res.setStatus("FAIL");
			res.setResult("Url or Title can not be blank");
		} else {
			System.out.println(url);
			System.out.println(title);
			topics.clear();
			res.setStatus("SUCCESS");
			// Make url document more information
			Response response= Jsoup.connect(url)
			           .ignoreContentType(true)
			           .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")  
			           .referrer("http://www.google.com")   
			           .timeout(0) 
			           .followRedirects(true)
			           .execute();

			Document doc = response.parse();
			String keywords = null, description = null;
			Elements meta = doc.select("meta[name=keywords]");
			if (!meta.isEmpty()) {
				for (int i = 0; i < meta.size(); i++) {
					keywords = meta.get(i).attr("content");
					System.out.println("Meta keyword : " + keywords);
					StringTokenizer tokens = new StringTokenizer(keywords, ",");
					description = tokens.nextToken() + " ";
					while (tokens.hasMoreTokens()) {
						description += tokens.nextToken() + " ";
					}
				}
			}
			meta = doc.select("meta[name=description]");
			if (!meta.isEmpty()) {
				if (description == null) {
					description = meta.get(0).attr("content");
				} else {
					description += meta.get(0).attr("content");
				}
				System.out.println("Meta description : " + description);
			}
			InstanceList instances = new InstanceList(ModelUtility.getPipe());
			while (true) {
				if (ModelUtility.isLoaded) {
					break;
				}
			}
			TopicInferencer inferencer = ModelUtility.model.getInferencer();
			instances.addThruPipe(new Instance(title + " " + description, null,
					url, null));
			double[] testProbabilities = inferencer.getSampledDistribution(
					instances.get(0), 10, 1, 5);
			//
			for (int i = 0; i < ModelUtility.model.getNumTopics(); i++) {
				if (testProbabilities[i] >= 0.09) {
					Topic t = new Topic();
					System.out.println(i + " " + testProbabilities[i]);
					List<RecommendTag> tags = ModelUtility.getTopWords(i,
							ModelUtility.model, instances);
					t.setTopicID(i);
					t.setTopicProbality(testProbabilities[i]);
					t.setRecommendTags(tags);
					topics.add(t);
				}
			}
			//
			res.setResult(topics);
		}
		return res;
	}

	@RequestMapping(value = "/addBookmark", method = RequestMethod.POST)
	public @ResponseBody JsonResponse addBookmark(@RequestParam String url,
			@RequestParam String title, @RequestParam String tags,
			@RequestParam String comment, HttpSession session) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		if (url.isEmpty() || title.isEmpty()) {
			rs.setStatus("FAIL");
		} else {
			rs.setStatus("SUCCESS");
			int bookmarkID = -1;
			int result = -1;
			// add new bookmark to bookmark_new
			try {
				String urlKeywords = UrlUtility.getUrlKeywords(url);
				String urlDescription = UrlUtility.getUrlDesciption(url);
				bookmarkID = BookmarkSQL.addBookmarkToDB(url, title,
						urlKeywords, urlDescription, userID);
				System.out.println("Adding BookmarkID:" + bookmarkID);
				BookmarkSQL.addBookmarkTopics(bookmarkID, topics);
				System.out.println(tags);
				StringTokenizer tokens = new StringTokenizer(tags, " ");
				while (tokens.hasMoreTokens()) {
					String tag = tokens.nextToken().toLowerCase();
					int tagID = TagSQL.addTagToDB(userID, tag);
					result = TagSQL.addTagToBookmark(bookmarkID, tagID);
					if (result == 0) {
						System.err.println("Can not add tag to bookmark");
					} else {
						System.out.println("Added tag ID: " + tagID
								+ " to bookmark");
					}
					result = UserSQL.userTaggedBookmark(userID, bookmarkID,
							tagID);
					if (result == 0) {
						System.err
								.println("Can not add information about user tag bookmark");
					} else {
						System.out.println("Added information user added "
								+ tagID + " to bookmark");
					}
				}
			} catch (IOException e) {
				System.err.println("Read URL information: " + e);
				rs.setStatus("FAIL");
				rs.setResult("Something has happend while adding URL");
			}
			// process tags added to bookmark
		}
		return rs;
	}

	@RequestMapping(value = "/getOtherTags", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getOtherTags(HttpSession session,
			@RequestParam int bookmarkID) {
		JsonResponse rs = new JsonResponse();
		if (bookmarkID == 0) {
			rs.setStatus("FAIL");
		} else {
			int userID = (int) session.getAttribute("userID");
			List<AdditionalTag> additionalTags = TagSQL.getOtherTags(userID,
					bookmarkID);
			if (additionalTags.size() == 0) {
				rs.setStatus("FAIL");
			} else {
				rs.setStatus("SUCCESS");
				rs.setResult(additionalTags);
			}
		}

		return rs;
	}

	@RequestMapping(value = "/getTotalRating", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getTotalRating(HttpSession session,
			@RequestParam int bookmarkID) {
		JsonResponse rs = new JsonResponse();
		if (bookmarkID == 0) {
			rs.setStatus("FAIL");
		} else {
			double totalRating = BookmarkSQL.getTotalRating(bookmarkID);
			rs.setStatus("SUCCESS");
			rs.setResult(totalRating);
		}
		return rs;
	}

	@RequestMapping(value = "/getBookmarkTags", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getBookmarkTags(HttpSession session,
			@RequestParam int bookmarkID) {
		JsonResponse rs = new JsonResponse();
		if (bookmarkID == 0) {
			rs.setStatus("FAIL");
		} else {
			List<String> tags = BookmarkSQL.getTags(bookmarkID);
			rs.setStatus("SUCCESS");
			rs.setResult(tags);
		}
		return rs;
	}

	@RequestMapping(value = "/editBookmark", method = RequestMethod.POST)
	public @ResponseBody JsonResponse editBookmark(HttpSession session,
			@RequestParam int bookmarkID,
			@RequestParam(required = false) List<String> addedTags,
			@RequestParam(required = false) List<String> deletedTags) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		BookmarkSQL.editBookmark(bookmarkID, userID, addedTags, deletedTags);
		return rs;
	}

	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getLoginHistory(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		OnlineHistory history = UserSQL.getLoginHistory(userID);
		rs.setStatus("SUCCESS");
		rs.setResult(history);
		return rs;
	}

	@RequestMapping(value = "/availableTags", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getAvailableTags(HttpSession session,
			@RequestParam String term) {
		List<String> filterdAvailableTags = new ArrayList<String>();
		while (availableTags.size() == 0) {

		}
		JsonResponse rs = new JsonResponse();
		for (int i = 0; i < availableTags.size(); i++) {
			String tag = availableTags.get(i);
			if (tag.startsWith(term.toLowerCase())) {
				filterdAvailableTags.add(tag);
			}
		}
		rs.setResult(filterdAvailableTags);
		return rs;
	}

	@RequestMapping(value = "/feedback", method = RequestMethod.POST)
	public @ResponseBody JsonResponse feedback(HttpSession session,
			@RequestParam String feedback) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		Connection c = DBUtility.getConnection();
		String sql = "INSERT INTO feedback VALUES (?, ?, default)";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setString(2, feedback);
			int result = pst.executeUpdate();
			if (result > 0) {
				rs.setStatus("SUCCESS");
				rs.setResult("Thanks you for your message!");
			} else {
				rs.setStatus("FAIL");
				rs.setResult("Something wrong has happened!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}
		return rs;
	}

	@RequestMapping(value = "/offlineTemp", method = RequestMethod.POST)
	public @ResponseBody JsonResponse offlineTemp(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		Connection c = DBUtility.getConnection();
		String sql = "UPDATE users SET online = 0 WHERE id = ?";

		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	@RequestMapping(value = "/onlineTemp", method = RequestMethod.POST)
	public @ResponseBody JsonResponse onlineTemp(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		Connection c = DBUtility.getConnection();
		String sql = "UPDATE users SET online = 1 WHERE id = ?";

		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	@RequestMapping(value = "/postNotifyDashboard", method = RequestMethod.POST)
	public @ResponseBody JsonResponse checkPostHistory(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		Connection c = DBUtility.getConnection();
		String sql = "SELECT count(userID) FROM bookmarks_new WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet result = pst.executeQuery();
			if (!result.next()) {
				rs.setResult(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

}
