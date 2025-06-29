package chapter6.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import chapter6.exception.SQLRuntimeException;

/**
 * DB(コネクション関係)のユーティリティー
 */
public class DBUtil {

	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost/simple_twitter";
	private static final String USER = "root";
	private static final String PASSWORD = "root";

	static {

		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * コネクションを取得します。
	 *
	 * @return
	 */
	public static Connection getConnection() {

		try {
			Connection connection = DriverManager.getConnection(URL, USER,
					PASSWORD);

			connection.setAutoCommit(false);

			return connection;
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}

	/**
	 * コミットします。
	 *
	 * @param connection
	 */
	public static void commit(Connection connection) {

		try {
			connection.commit();
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}

	/**
	 * ロールバックします。
	 *
	 * @param connection
	 */
	public static void rollback(Connection connection) {

		if (connection == null) {
			return;
		}

		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}

    /**
     * コネクションをクローズします。
     * @param con クローズするConnectionオブジェクト
     */
    public static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace(); // エラーログ出力
            }
        }
    }

    /**
     * ResultSet, PreparedStatement, Connectionをクローズします。
     * @param rs クローズするResultSetオブジェクト (null許容)
     * @param ps クローズするPreparedStatementオブジェクト (null許容)
     * @param con クローズするConnectionオブジェクト (null許容)
     */
    public static void close(ResultSet rs, PreparedStatement ps, Connection con) {
        // ResultSetをクローズ
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // PreparedStatementをクローズ
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Connectionをクローズ
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
