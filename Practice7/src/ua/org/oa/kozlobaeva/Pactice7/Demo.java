package ua.org.oa.kozlobaeva.Pactice7;

import java.sql.SQLException;

public class Demo {
	
	public static void main(String[] args) throws SQLException {
		DBManager manager = DBManager.getInstance();
		
		// clear the users table
		manager.clearUsers();
		
		// test create user 
		System.out.println("Insert two users");
		manager.insertUser(User.buildUser(0, "admin", "admin", "Ivanov"));
		manager.insertUser(User.buildUser(0, "client", "client", "Petrov"));
		manager.insertUser(User.buildUser(0, "client2", "client2", "Fedorov"));
		for (User user : manager.findAllUsers()) {
			System.out.println(user);
		}
		System.out.println("~~~~~~~~~~~~~~");
		
		// test update user
		User admin = manager.findUserByLogin("admin");
		System.out.println(admin);

		if (admin != null) {
			System.out.println("Update user admin");
			admin.setName("Ivanov4");
			manager.updateUser(admin);
		}

		User client = manager.findUserByLogin("client");
		if(client != null){
			manager.deleteUser(client);
			System.out.println("client was delete successful");
		}
		for (User user : manager.findAllUsers()) {
			System.out.println(user);
		}
	}

}
