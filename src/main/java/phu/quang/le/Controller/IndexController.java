package phu.quang.le.Controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getIndexView(HttpSession session) {
		if (session.getAttribute("firstName") == null) {
			System.out.println("Session does not exist -> index");
			return "index";
		} else {
			System.out.println("Session exists -> dashboard");
			return "redirect:/dashboard";
		}
	}
}