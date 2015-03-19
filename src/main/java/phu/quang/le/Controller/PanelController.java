package phu.quang.le.Controller;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.BookmarkLink;

@Controller
@RequestMapping(value = "/dashboard")
public class PanelController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getDashBoard () {
		ModelAndView dashboard = new ModelAndView ("dashboard");
		BookmarkLink newLink = new BookmarkLink ();
		dashboard.addObject ("newLink", newLink);
		//
		return dashboard;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST, headers = "Accept:*/*")
	public @ResponseBody String bookmarkLink (
			@ModelAttribute("newLink") BookmarkLink newLink) {
		String url = newLink.getUrl ();
		String message = null;
		System.out.println (url);
		UrlValidator urlValidator = new UrlValidator ();
		if (url.isEmpty ()) {
			message = "Url can not be blank";
			return message;
		} else {
			if (urlValidator.isValid (url)) {
				return "success";
			} else {
				message = "Url is not valid.";
				return "fail";
			}
		}
	}
}
