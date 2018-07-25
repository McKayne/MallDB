package com.mall;

import java.sql.*;

/**
 * Hello world!
 *
 */
public class MallGUI {

	private static Connection connection;

	public static Connection getConnection() {
                try {
                        Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                }

                Connection connection = null;
                try {
                        connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/mall", "postgres", "postgres");
                } catch (SQLException ex) {
                        ex.printStackTrace();
                }
                return connection;
        }

	public static void main(String[] args) {
		connection = getConnection();
		new MallFrame(connection);
	}
}
