package phu.quang.le.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import phu.quang.le.Model.Login;
import phu.quang.le.Model.User;
import phu.quang.le.Utility.DBUtility;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

	@RequestMapping(method = RequestMethod.POST)
	public String processRegistration (@Valid @ModelAttribute("login") Login login,
			BindingResult result, ModelMap model) throws SQLException {
		if (result.hasErrors ()) {
			User user = new User ();
			model.addAttribute ("user", user);
			return "index";
		} else {
			Connection c = DBUtility.getConnection ();
			String sql = "SELECT email, password FROM users WHERE email = ? AND password = ?";
			PreparedStatement pst = c.prepareStatement (sql);
			pst.setString (1, login.getLoginEmail ());
			pst.setString (2, login.getLoginPassword ());
			ResultSet rs = pst.executeQuery ();
			if (!rs.next ()) {
				//
				System.out.println ("Cant Login");
				return "redirect:/";
			} else {
				System.out.println ("Login Successful");
				return "redirect:/dashboard";
			}
		}
	}
}
