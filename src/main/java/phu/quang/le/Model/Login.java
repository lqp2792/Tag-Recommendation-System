package phu.quang.le.Model;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class Login {

	private String email;
	private String password;

	@Email
	@NotEmpty
	public String getEmail () {
		return email;
	}

	@NotEmpty
	@Size(min = 2, max = 30)
	public String getPassword () {
		return password;
	}

	public void setEmail (String email) {
		this.email = email;
	}

	public void setPassword (String password) {
		this.password = password;
	}
}
