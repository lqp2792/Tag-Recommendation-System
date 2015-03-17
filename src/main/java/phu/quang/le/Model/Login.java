package phu.quang.le.Model;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class Login {

	private String loginEmail;
	private String loginPassword;

	@Email
	@NotEmpty
	public String getLoginEmail () {
		return loginEmail;
	}

	@NotEmpty
	@Size(min = 2, max = 30)
	public String getLoginPassword () {
		return loginPassword;
	}

	public void setLoginEmail (String loginEmail) {
		this.loginEmail = loginEmail;
	}

	public void setLoginPassword (String loginPassword) {
		this.loginPassword = loginPassword;
	}
}
