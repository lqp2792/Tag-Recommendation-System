package phu.quang.le.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.AdvanceBookmark;
import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Model.TagWeight;
import phu.quang.le.Utility.BookmarkSQL;
import phu.quang.le.Utility.TagSQL;

@Controller
@RequestMapping(value = "/trending")
public class TrendingController {
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getTrendingView(HttpSession session, ModelMap map)
			throws FileNotFoundException, ClassNotFoundException,
			URISyntaxException, IOException {
		if (session.getAttribute("userID") == null) {
			return new ModelAndView("index");
		} else {
			if (session.getAttribute("sortBy") == null) {
				session.setAttribute("sortBy", 4);
			}
			ModelAndView trending = new ModelAndView("dashboard");
			List<TagWeight> tagWeights = TagSQL.getTopMostUsedTags();
			trending.addObject("firstName", session.getAttribute("firstName"));
			trending.addObject("lastName", session.getAttribute("lastName"));
			trending.addObject("tagWeights", tagWeights);
			return trending;
		}
	}

	@RequestMapping(value = "/bookmarks", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getTrendingBookmarks(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		rs.setStatus("SUCCESS");
		List<AdvanceBookmark> trendingBookmarks = BookmarkSQL
				.getTrendingBookmarks();
		rs.setResult(trendingBookmarks);
		return rs;
	}
}
