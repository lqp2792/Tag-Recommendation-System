package phu.quang.le.Controller;

import java.sql.SQLException;

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
			@RequestParam String firstName, @RequestParam String lastName,
			@RequestParam String email, @RequestParam String password)
			throws SQLException {
		JsonResponse rs = new JsonResponse();
		System.out.println("Register New Account");
		if (UserSQL.isAccountExisted(email)) {
			rs.setStatus("FAIL");
			rs.setResult("This account email has been used");
		} else {
			int result = UserSQL.registerAccount(firstName, lastName, email,
					password);
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
