package phu.quang.le.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Utility.DBUtility;

@Controller
@RequestMapping(value = "/settings")
public class SettingController {
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getSettingsPage(HttpSession session) {
		if (session.getAttribute("firstName") == null) {
			System.out.println("Session does not exist -> index");
			return new ModelAndView("redirect:/");
		} else {
			ModelAndView dashboard = new ModelAndView("dashboard");
			dashboard.addObject("firstName", session.getAttribute("firstName"));
			dashboard.addObject("lastName", session.getAttribute("lastName"));
			return dashboard;
		}
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public @ResponseBody JsonResponse changePassword(HttpSession session,
			@RequestParam String oldPassword, @RequestParam String newPassword) {
		JsonResponse rs = new JsonResponse();
		Connection c = DBUtility.getConnection();
		int userID = (int) session.getAttribute("userID");
		String sql = "SELECT * FROM users WHERE id = ? AND password = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setString(2, oldPassword);
			ResultSet result = pst.executeQuery();
			if (result.next()) {
				sql = "UPDATE users SET password = ? WHERE id = ?";
				pst = c.prepareStatement(sql);
				pst.setString(1, newPassword);
				pst.setInt(2, userID);
				int check = pst.executeUpdate();
				if (check > 0) {
					rs.setStatus("SUCCESS");
					rs.setResult("Changed password successfully");
				} else {
					rs.setStatus("FAIL");
					rs.setResult("Something wrong has happend!");
				}
			} else {
				rs.setStatus("FAIL");
				rs.setResult("Old password is wrong. Please check again!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}
		return rs;
	}
}
