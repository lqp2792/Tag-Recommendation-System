package phu.quang.le.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.Link;

@Controller
public class PanelController {
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public ModelAndView getDashBoard () {
		ModelAndView dashboard = new ModelAndView ("dashboard");
		Link newLink = new Link ();
		dashboard.addObject ("newLink", newLink);
		//
		return dashboard;
	}
}
