package phu.quang.le.Controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import phu.quang.le.Model.AdvanceBookmark;
import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Utility.TagSQL;

@Controller
@RequestMapping(value = "/search")
public class SearchController {
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody JsonResponse searchBookmarksByTag(HttpSession session,
			@RequestParam int sortBy, @RequestParam String tag, @RequestParam int offset) {
		JsonResponse rs = new JsonResponse();
		System.out.println("Search tag: " + tag + " - Offset : "  + offset);
		if (session.getAttribute("userID") == null) {
			
		} else {
			int userID = (int) session.getAttribute("userID");
			if (sortBy == 0 || tag == null) {
				rs.setStatus("FAIL");
			} else {
				List<AdvanceBookmark> search = TagSQL.searchBookmarksByTag(
						userID, tag, sortBy, offset);
				if (search.size() == 0) {
					rs.setStatus("EMPTY");
				} else {
					rs.setStatus("SUCCESS");
				}
				rs.setResult(search);
			}
		}

		return rs;
	}
}
