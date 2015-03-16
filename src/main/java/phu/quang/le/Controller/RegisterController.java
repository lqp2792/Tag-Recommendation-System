package phu.quang.le.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import phu.quang.le.Model.User;
import phu.quang.le.Utility.DBUtility;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {

	@RequestMapping(method = RequestMethod.POST)
	public String processRegistration (@Valid @ModelAttribute("user") User user,
			BindingResult result, ModelMap model) throws SQLException {
		if(result.hasErrors ()) {
			model.addAttribute ("validated", true);
			return "index";
		}
		Connection c = DBUtility.getConnection ();
		String sql = "INSERT INTO user VALUES (default, ?, ?, ?, ?)";
		PreparedStatement pst = c.prepareStatement (sql);
		pst.setString (1, user.getEmail ());
		pst.setString (2, user.getPassword ());
		pst.setString (3, user.getFirstName ());
		pst.setString (4, user.getLastName ());
		System.out.println (pst.toString ());
		return "success";
	}
}
