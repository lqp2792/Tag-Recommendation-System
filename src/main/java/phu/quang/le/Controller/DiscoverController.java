package phu.quang.le.Controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import phu.quang.le.Model.Bookmark;
import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Model.TagWeight;
import phu.quang.le.TopicModeling.ModelUtility;
import phu.quang.le.Utility.BookmarkSQL;
import phu.quang.le.Utility.UserSQL;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;

@Controller
public class DiscoverController {
	@RequestMapping(value = "/dashboard/subscription", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTagsSubcription(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		List<Object> result = new ArrayList<Object>();
		List<TagWeight> mostUsedTags = new ArrayList<TagWeight>();
		List<String> tagSubscription = new ArrayList<String>();
		mostUsedTags = UserSQL.getMostUsedTags((int) session
				.getAttribute("userID"));
		rs.setStatus("SUCCESS");
		result.add(mostUsedTags);
		if (!tagSubscription.isEmpty()) {
			result.add(tagSubscription);
		} else {
			result.add("EMPTY");
		}

		rs.setResult(result);
		//
		return rs;
	}

	@RequestMapping(value = "/dashboard/defaultDiscover", method = RequestMethod.POST)
	public @ResponseBody JsonResponse defaultDiscover(HttpSession session,
			@RequestParam(value = "subscriptionTags") String[] subscriptionTags) {
		JsonResponse rs = new JsonResponse();
		List<Integer> subscriptionTopicIDs = new ArrayList<>();
		try {
			ParallelTopicModel model = ModelUtility.getTopicModel();
			Alphabet dataAlphabet = model.getAlphabet();
			ArrayList<TreeSet<IDSorter>> topicSortedWords = model
					.getSortedWords();
			for (int i = 0; i < model.numTopics; i++) {
				Iterator<IDSorter> iterator = topicSortedWords.get(i)
						.iterator();
				int rank = 0;
				List<String> checkingTags = new ArrayList<String>();
				while (iterator.hasNext() && rank < 5) {
					IDSorter idCountPair = iterator.next();
					checkingTags.add((String) dataAlphabet
							.lookupObject(idCountPair.getID()));
					rank++;
				}
				for (int j = 0; j < subscriptionTags.length; j++) {
					if (checkingTags.contains(subscriptionTags[j])) {
						if (!subscriptionTopicIDs.contains(i)) {
							subscriptionTopicIDs.add(i);
						}
					}
				}
			}
			List<Bookmark> discover = BookmarkSQL.discoverBookmarks(
					subscriptionTopicIDs, (int) session.getAttribute("userID"));
			rs.setStatus("SUCCESS");
			rs.setResult(discover);

		} catch (ClassNotFoundException | URISyntaxException | IOException e) {
			System.err.println("Default Discover : " + e);
		}
		return rs;
	}
}
