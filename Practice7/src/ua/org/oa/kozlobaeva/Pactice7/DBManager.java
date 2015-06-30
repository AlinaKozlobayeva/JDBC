package ua.org.oa.kozlobaeva.Pactice7;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class manages DB.<br/>
 * WARNING! You must have mysql driver on the classpath of your application.<br/>
 * Also you have to define a connection url in db.properties file (must be saved
 * in root of classpath).<br/>
 * 
 * @author Dmitry Kolesnikov
 * 
 */
public class DBManager {

	private final String URL;

	// SQL queries
	private static final String SQL_INSERT_USER = "INSERT INTO users VALUES(DEFAULT, ?, ?, ?)";

	private static final String SQL_DELETE_USER = "DELETE FROM users WHERE id=?";

	private static final String SQL_SELECT_ALL_USERS = "SELECT * FROM users";

	private static final String SQL_SELECT_USER_BY_LOGIN = "SELECT * FROM users WHERE login=?";

	private static final String SQL_UPDATE_USER = "UPDATE users SET login=?, password=?, name=? WHERE id=?";

	private static final String SQL_DELETE_ALL_USERS = "DELETE FROM users";

	// SINGLETON start ------------------------------------

	private static DBManager instance;

	public synchronized static DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}

	private DBManager() {
		Properties props = new Properties();

		// this way we obtain db.properties file from the CLASSPATH
		InputStream in = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream(
					"db.properties");
			if (in == null) {
				throw new IllegalStateException(
						"File must be resides in classpath: db.properties");
			}
			props.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// obtain the connection url
		URL = props.getProperty("connection.url");
		if (URL == null) {
			throw new IllegalStateException(
					"You have to define connection.url property in db.properties");
		}
	}

	// SINGLETON finish -----------------------------------

	// CRUD methods start ---------------------------------

	// (1) CREATE
	/**
	 * WARNING! Server side (MySQL server) generates identifier (id) when you
	 * will insert a user to the table. You must set up this returned value for
	 * user instance. To do this you can use Statement.RETURN_GENERATED_KEYS
	 * constant during PreparedStatement object creation.
	 * 
	 * @param user
	 *            user to be added
	 * @return true if user was successfully inserted, false otherwise.
	 */
	public boolean insertUser(User user) {
		boolean result = false;
		Connection con = null;
		try {
			con = getConnection();
			// create a prepared statement
			PreparedStatement pstmt = con.prepareStatement(SQL_INSERT_USER,
					Statement.RETURN_GENERATED_KEYS);
			// adjust a prepared statement
			int k = 1;
			pstmt.setString(k++, user.getLogin());
			pstmt.setString(k++, user.getPassword());
			pstmt.setString(k++, user.getName());

			// execute a query (query updates the users table)
			int count = pstmt.executeUpdate();

			// obtain identifier if the user have been inserted
			if (count > 0) {
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					user.setId(rs.getInt(1));
					result = true;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Cannot insert the user: " + user);
			ex.printStackTrace();
		} finally {
			close(con);
		}
		return result;
	}

	// (2) READ
	public User findUserByLogin(String login) {
		Connection con = null;
		User result = null;
		try {
			con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_USER_BY_LOGIN);
			pstmt.setString(1, login);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				result = extractUser(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Can't close connection!");
				}
			}
		}
		return result;
	}

	// (3) UPDATE
	public boolean updateUser(User user) {
		boolean result = false;
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(SQL_UPDATE_USER);

			int k =1;
			pstmt.setString(k++,user.getName());
			pstmt.setString(k++, user.getPassword());
			pstmt.setString(k++, user.getName());
			pstmt.setInt(k++, user.getId());
			int row = pstmt.executeUpdate();
			if(row > 0){
				result = true;
				System.out.println("Update successful");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(con);
		}
		return result;
	}

	// (4) DELETE
	public boolean deleteUser(User user) {
		boolean result = false;
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(SQL_DELETE_USER);
			// adjust a prepared statement
			int k = 1;
			pstmt.setInt(k++, user.getId());
			int row = pstmt.executeUpdate();
			if(row > 0){
				result = true;
				System.out.println("Update successful");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(con);
		}

		return false;
	}

	// CRUD methods finish --------------------------------

	// other methods

	/**
	 * Returns all the users from the users table.
	 */
	public List<User> findAllUsers() {
		List<User> users = new ArrayList<>();
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL_USERS);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				users.add(extractUser(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	public void clearUsers() {
		Connection con = null;
		try {
			con = getConnection();
			Statement stmt = con.createStatement();
			stmt.executeUpdate(SQL_DELETE_ALL_USERS);
		} catch (SQLException ex) {
			System.out.println("Cannot clear the users table");
			ex.printStackTrace();
		} finally {
			close(con);
		}
	}

	// util methods

	private Connection getConnection() throws SQLException {
		Connection con = DriverManager.getConnection(URL);
		return con;
	}

	private void close(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException ex) {
			System.out.println("Cannot close a connection");
			ex.printStackTrace();
		}
	}

	/**
	 * Extracts a user object from the result set object.
	 */
	@SuppressWarnings("unused")
	private User extractUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getInt(Fields.USERS_ID));
		user.setLogin(rs.getString(Fields.USERS_LOGIN));
		user.setPassword(rs.getString(Fields.USERS_PASSWORD));
		user.setName(rs.getString(Fields.USERS_NAME));
		return user;
	}

	// test method
	public static void main(String[] args) throws SQLException {
		// test connection
		DBManager manager = DBManager.getInstance();
		System.out.println("connection ==> " + manager.getConnection());
	}

}