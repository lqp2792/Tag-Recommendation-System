package phu.quang.le.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Utility.DBUtility;
import phu.quang.le.Utility.UserSQL;

@Controller
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody JsonResponse processLogin(@RequestParam String email,
			@RequestParam String password, HttpServletRequest request)
			throws SQLException {
		JsonResponse rs = new JsonResponse();
		HttpSession session = request.getSession();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
		PreparedStatement pst = c.prepareStatement(sql);
		pst.setString(1, email);
		pst.setString(2, password);
		ResultSet result = pst.executeQuery();
		if (!result.next()) {
			rs.setStatus("FAIL");
			rs.setResult("Password or email is wrong!");
		} else {
			rs.setStatus("SUCCESS");
			String firstName = result.getString(4);
			String lastName = result.getString(5);
			session.setAttribute("email", email);
			session.setAttribute("firstName", firstName);
			session.setAttribute("lastName", lastName);
			session.setAttribute("userID",
					UserSQL.getUserID(firstName, lastName));
			rs.setResult("Login Successful");
		}
		return rs;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public @ResponseBody JsonResponse processLogout(HttpServletRequest request)
			throws SQLException {
		JsonResponse rs = new JsonResponse();
		HttpSession session = request.getSession();
		session.invalidate();
		System.out.println("Log out");
		return rs;
	}
}
