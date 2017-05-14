import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBInterface {
	private static String url = null;
	private static String user = null;
	private static String password = null;
	private static Properties props = new Properties();

	static {
		try {
			props.load(DBInterface.class.getResourceAsStream("db.config"));
		} catch (IOException e) {
			System.out.println("未找到配置文件。");
			e.printStackTrace();
		}
		url = props.getProperty("url");
		user = props.getProperty("user");
		password = props.getProperty("password");

		// TODO How to replace it?
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("驱动加载错误。");
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("数据库连接错误。");
			e.printStackTrace();
		}
		return conn;
	}

	public static ResultSet executeQuery(Connection conn, String sqlCommand) throws SQLException {
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlCommand);
		} catch (SQLException e) {
			System.out.println("数据库操作错误。");
			e.printStackTrace();
			throw new SQLException();    // ???
		} finally {
			stmt.close();
		}
		return rs;
	}

	public static int executeUpdate(Connection conn, String sqlCommand) throws SQLException {
		int res = 0;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			res = stmt.executeUpdate(sqlCommand);
		} catch (SQLException e) {
			System.out.println("数据库操作错误。");
			e.printStackTrace();
			throw new SQLException();
		} finally {
			stmt.close();
		}
		return res;
	}

	public static void closeConnection(Connection conn) {
		if(conn == null)
			return;
		try {
			if(!conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			System.out.println("关闭连接失败。");
			e.printStackTrace();
		}
	}
	
}