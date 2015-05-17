package phu.quang.le.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javacryption.aes.AesCtr;

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
	public @ResponseBody JsonResponse processLogin(HttpServletRequest req,
			@RequestParam String encryptedEmail,
			@RequestParam String encryptedPassword) {
		HttpServletRequest request = (HttpServletRequest) req;
		String key = (String) request.getSession().getServletContext()
				.getAttribute("jCryptionKey");
		String decryptedPassword = AesCtr.decrypt(encryptedPassword, key, 256);
		String decryptedEmail = AesCtr.decrypt(encryptedEmail, key, 256);
		JsonResponse rs = new JsonResponse();
		HttpSession session = request.getSession();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setString(1, decryptedEmail);
			pst.setString(2, decryptedPassword);
			ResultSet result = pst.executeQuery();
			if (!result.next()) {
				rs.setStatus("FAIL");
				rs.setResult("Password or email is wrong!");
			} else {
				rs.setStatus("SUCCESS");
				String firstName = result.getString(4);
				String lastName = result.getString(5);
				session.setAttribute("firstName", firstName);
				session.setAttribute("lastName", lastName);
				int userID = UserSQL.getUserID(firstName, lastName);
				session.setAttribute("userID", userID);

				sql = "UPDATE users SET online = 1 WHERE id = ?";
				pst = c.prepareStatement(sql);
				pst.setInt(1, userID);
				pst.executeUpdate();
				rs.setResult("Login Successful");
			}
		} catch (SQLException e) {
			System.err.println("Login Exception: " + e);
		} finally {
			DBUtility.closeConnection(c);
		}

		return rs;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public @ResponseBody JsonResponse processLogout(HttpSession session)
			throws SQLException {
		JsonResponse rs = new JsonResponse();
		Integer userID = new Integer((int) session.getAttribute("userID"));
		UserSQL.updateLoginHistory(userID);
		session.invalidate();
		System.out.println("Log out");
		return rs;
	}
}
