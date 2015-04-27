package phu.quang.le.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
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

import phu.quang.le.Model.AdvanceBookmark;
import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Model.TagWeight;
import phu.quang.le.TopicModeling.ModelUtility;
import phu.quang.le.Utility.BookmarkSQL;
import phu.quang.le.Utility.UserSQL;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;

@Controller
@RequestMapping(value = "/discover")
public class DiscoverController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getDiscoverView(HttpSession session, ModelMap map)
			throws FileNotFoundException, ClassNotFoundException,
			URISyntaxException, IOException {
		if (session.getAttribute("userID") == null) {
			return new ModelAndView("index");
		} else {
			if (session.getAttribute("sortBy") == null) {
				session.setAttribute("sortBy", 4);
			}
			ModelAndView dashboard = new ModelAndView("dashboard");
			dashboard.addObject("firstName", session.getAttribute("firstName"));
			dashboard.addObject("lastName", session.getAttribute("lastName"));

			return dashboard;
		}
	}

	@RequestMapping(value = "/subscription", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getTagsSubcription(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		List<Object> result = new ArrayList<Object>();
		List<String> subscriptionTags = UserSQL.getTagSubscription(userID);
		List<TagWeight> mostUsedTags = UserSQL.getMostUsedTags(userID);
		rs.setStatus("SUCCESS");
		result.add(mostUsedTags);
		result.add(subscriptionTags);
		rs.setResult(result);

		return rs;
	}

	@RequestMapping(value = "/editSubscription", method = RequestMethod.POST)
	public @ResponseBody JsonResponse editTagsSubcription(HttpSession session,
			@RequestParam(required = false) List<String> subscriptionTags,
			@RequestParam(required = false) List<String> deletedTags) {
		JsonResponse rs = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		UserSQL.editSubscription(userID, subscriptionTags, deletedTags);
		return rs;
	}

	@RequestMapping(value = "/discoverBookmarks", method = RequestMethod.GET)
	public @ResponseBody JsonResponse discoverBookmarks(HttpSession session,
			@RequestParam List<String> subscriptionTags,
			@RequestParam int offset, @RequestParam int sortBy) {
		JsonResponse rs = new JsonResponse();
		List<Integer> subscriptionTopicIDs = new ArrayList<>();

		Alphabet dataAlphabet = ModelUtility.model.getAlphabet();
		ArrayList<TreeSet<IDSorter>> topicSortedWords = ModelUtility.model
				.getSortedWords();
		for (int i = 0; i < ModelUtility.model.numTopics; i++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(i).iterator();
			int rank = 0;
			List<String> checkingTags = new ArrayList<String>();
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				checkingTags.add((String) dataAlphabet.lookupObject(idCountPair
						.getID()));
				rank++;
			}
			for (int j = 0; j < subscriptionTags.size(); j++) {
				if (checkingTags.contains(subscriptionTags.get(j))) {
					if (!subscriptionTopicIDs.contains(i)) {
						subscriptionTopicIDs.add(i);
					}
				}
			}
		}
		session.setAttribute("sortBy", sortBy);
		int userID = (int) session.getAttribute("userID");
		List<AdvanceBookmark> discoveredBookmarks = BookmarkSQL
				.discoverBookmarks(subscriptionTopicIDs, userID, offset, sortBy);
		if (discoveredBookmarks.size() > 0) {
			rs.setStatus("SUCCESS");
		} else {
			rs.setStatus("EMPTY");
		}
		rs.setResult(discoveredBookmarks);
		return rs;
	}
}
