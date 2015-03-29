package phu.quang.le.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getIndexView (HttpServletRequest request) {
		HttpSession session = request.getSession (false);
		if (session == null) {
			session = request.getSession ();
			return "index";
		} else {
			System.out.println ("Redirect");
			return "redirect:/dashboard";
		}
	}
}