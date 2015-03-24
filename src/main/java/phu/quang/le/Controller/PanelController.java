package phu.quang.le.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.Bookmark;
import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Model.RecommendTag;
import phu.quang.le.Model.Topic;
import phu.quang.le.TopicModeling.ModelUtility;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

@Controller
public class PanelController {

	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public ModelAndView getDashBoard () {
		ModelAndView dashboard = new ModelAndView (
				"dashboard");
		Bookmark newBookmark = new Bookmark ();
		dashboard.addObject ("newBookmark", newBookmark);
		//
		return dashboard;
	}

	@RequestMapping(value = "/dashboard/add", method = RequestMethod.POST)
	@ResponseBody
	public JsonResponse addBookmark (
			@ModelAttribute("newLink") Bookmark newBookmark,
			ModelMap modelMap)
			throws IOException {
		String url = newBookmark.getUrl ();
		JsonResponse res = new JsonResponse ();
		UrlValidator urlValidator = new UrlValidator ();
		if (url.isEmpty ()) {
			res.setStatus ("FAIL");
			res.setResult ("Url can not be blank");
			return res;
		} else {
			if (urlValidator.isValid (url)) {
				res.setStatus ("SUCCESS");
				Document doc = Jsoup.connect (url).get ();
				String title = doc.title ();
				newBookmark.setTitle (title);
				modelMap.addAttribute ("newBookmark",
						newBookmark);
				res.setResult (newBookmark);
				return res;
			} else {
				res.setStatus ("FAIL");
				res.setResult ("Url is invalid");
				return res;
			}
		}
	}

	@RequestMapping(value = "/dashboard/gettags", method = RequestMethod.POST)
	@ResponseBody
	public JsonResponse addBookmarkTags (@RequestParam String url,
			@RequestParam String title) throws FileNotFoundException,
			ClassNotFoundException, IOException, URISyntaxException {
		JsonResponse res = new JsonResponse ();
		if (url.isEmpty () || title.isEmpty ()) {
			res.setStatus ("FAIL");
			res.setResult ("Url or Title can not be blank");
		} else {
			System.out.println (url);
			System.out.println (title);
			//
			res.setStatus ("SUCCESS");
			// Make url document more information
			Document doc = Jsoup.connect (url).get ();
			String keywords = null, description = null;
			Elements meta = doc.select ("meta[name=keywords]");
			if (!meta.isEmpty ()) {
				for (int i = 0; i < meta.size (); i++) {
					keywords = meta.get (i).attr ("content");
					System.out.println ("Meta keyword : " + keywords);
					StringTokenizer tokens = new StringTokenizer (keywords, ",");
					description = tokens.nextToken () + " ";
					while (tokens.hasMoreTokens ()) {
						description += tokens.nextToken () + " ";
					}
				}
			}
			meta = doc.select ("meta[name=description]");
			if (!meta.isEmpty ()) {
				if (description == null) {
					description = meta.get (0).attr ("content");
				} else {
					description += meta.get (0).attr ("content");
				}
				System.out.println ("Meta description : " + description);
			}
			// Infer topic for url
			List<Topic> topics = new ArrayList<Topic> ();
			TopicInferencer inferencer = ModelUtility.getTopicInferencer ();
			ParallelTopicModel model = ModelUtility.getTopicModel ();
			InstanceList instances = new InstanceList (ModelUtility.createPipes ());
			System.out.println (title + description);
			instances.addThruPipe (new Instance (title + " " + description, null, url,
					null));
			System.out.println (instances.get (0).getData ().toString ());
			double[] testProbabilities = inferencer.getSampledDistribution
					(instances.get (0),
							10, 1, 5);
			ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords ();
			Alphabet dataAlphabet = model.getAlphabet ();
			//
			for (int i = 0; i < model.getNumTopics (); i++) {
				int rank = 0;
				if (testProbabilities[i] >= 0.09) {
					Topic t = new Topic ();
					List<RecommendTag> tags = new ArrayList<RecommendTag> ();
					System.out.println (i + " " + testProbabilities[i]);
					t.setTopicID (i);
					t.setTopicProbality (testProbabilities[i]);
					Iterator<IDSorter> iterator = topicSortedWords.get (i).iterator ();
					//
					while (iterator.hasNext () && rank < 5) {
						RecommendTag tag = new RecommendTag ();
						IDSorter idCountPair = iterator.next ();
						tag.setContent ((String) dataAlphabet.lookupObject (idCountPair
								.getID ()));
						System.out.print (idCountPair.getID () + " ");
						System.out.println (tag.getContent ());
						tags.add (tag);
						rank++;
					}
					t.setRecommendTags (tags);
					topics.add (t);
				}
			}
			//
			res.setResult (topics);
		}
		return res;
	}
}
