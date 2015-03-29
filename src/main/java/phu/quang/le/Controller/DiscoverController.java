package phu.quang.le.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Model.TagWeight;
import phu.quang.le.Utility.UserSQL;

@Controller
public class DiscoverController {
	@RequestMapping(value = "/dashboard/subscription", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTagsSubcription(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		List<TagWeight> mostUsedTags = new ArrayList<TagWeight>();
		mostUsedTags = UserSQL.getMostUsedTags((int) session
				.getAttribute("userID"));
		rs.setStatus("SUCCESS");
		rs.setResult(mostUsedTags);
		//
		return rs;
	}
}
