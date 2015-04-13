package phu.quang.le.Utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtility {

	public static Connection getConnection() {
		Connection connection = null;
		try {
			Properties prop = new Properties();
			InputStream inputStream = DBUtility.class.getClassLoader()
					.getResourceAsStream("config.properties");
			prop.load(inputStream);
			String driver = prop.getProperty("driver");
			String url = prop.getProperty("url");
			String user = prop.getProperty("user");
			String password = prop.getProperty("password");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return connection;
	}

	public static void main(String[] args) {
		Connection conn = getConnection();
		if (conn != null) {
			System.out.println("ok");
		} else {
			System.out.println("not ok");
		}
	}
}
