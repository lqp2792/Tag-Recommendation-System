package phu.quang.le.Model;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class User {

	@NotEmpty
	@Email
	private String email;
	@Size(min = 2, max = 30)
	@NotEmpty
	private String firstName;
	@Size(min = 2, max = 30)
	@NotEmpty
	private String lastName;
	@Size(min = 6, max = 30)
	@NotEmpty
	private String password;

	public String getEmail () {
		return email;
	}

	public String getFirstName () {
		return firstName;
	}

	public String getLastName () {
		return lastName;
	}

	public String getPassword () {
		return password;
	}

	public void setEmail (String email) {
		this.email = email;
	}

	public void setFirstName (String firstName) {
		this.firstName = firstName;
	}

	public void setLastName (String lastName) {
		this.lastName = lastName;
	}

	public void setPassword (String password) {
		this.password = password;
	}
}
