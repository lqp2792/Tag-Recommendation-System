package phu.quang.le.Controller;

import java.sql.SQLException;

import javacryption.aes.AesCtr;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Utility.UserSQL;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody JsonResponse processRegistration(
			HttpServletRequest req, @RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String encryptedEmail,
			@RequestParam String encryptedPassword) throws SQLException {
		HttpServletRequest request = (HttpServletRequest) req;
		String key = (String) request.getSession().getServletContext()
				.getAttribute("jCryptionKey");
		String decryptedPassword = AesCtr.decrypt(encryptedPassword, key, 256);
		String decryptedEmail = AesCtr.decrypt(encryptedEmail, key, 256);
		JsonResponse rs = new JsonResponse();
		System.out.println("Register New Account");
		if (UserSQL.isAccountExisted(decryptedEmail)) {
			rs.setStatus("FAIL");
			rs.setResult("This account email has been used");
		} else {
			int result = UserSQL.registerAccount(firstName, lastName,
					decryptedEmail, decryptedPassword);
			if (result > 0) {
				rs.setStatus("SUCCESS");
				rs.setResult("Account Registered Successfully");
			} else {
				rs.setStatus("FAIL");
				rs.setResult("Register did not finish Successfully");
			}
		}
		return rs;
	}
}
