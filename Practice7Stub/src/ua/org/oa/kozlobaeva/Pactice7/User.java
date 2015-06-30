package ua.org.oa.kozlobaeva.Pactice7;


public class User {

	private int id;

	private String login;

	private String password;

	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", login=" + login + ", name=" + name + "]";
	}
	
	public static User buildUser(int id, String login, String password, String name) {
		User user = new User();
		user.setId(id);
		user.setLogin(login);
		user.setPassword(password);
		user.setName(name);
		return user;
	}

}
