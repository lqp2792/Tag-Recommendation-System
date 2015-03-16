package phu.quang.le.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.Login;
import phu.quang.le.Model.User;

@Controller
public class IndexController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView getIndexView () {
		ModelAndView model = new ModelAndView ("index");
		User user = new User ();
		Login login = new Login ();
		model.addObject ("user", user);
		model.addObject ("login", login);
		//
		return model;
	}
}