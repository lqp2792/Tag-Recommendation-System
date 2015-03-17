package phu.quang.le.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/dashboard")
public class PanelController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getDashBoard () {
		System.out.println ("what the actual fuck");
		return new ModelAndView ("dashboard");
	}
}
