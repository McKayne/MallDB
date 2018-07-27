package com.mall;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MallGUI {

	public static void main(String[] args) {
                try {
                        Class.forName("org.postgresql.Driver");

                        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/mall", "postgres", "postgres")) {
                                new MallFrame(connection);
                        }
                } catch (ClassNotFoundException ex1) {
                        ex1.printStackTrace();
                } catch (SQLException ex2) {
                        ex2.printStackTrace();
                }
	}
}