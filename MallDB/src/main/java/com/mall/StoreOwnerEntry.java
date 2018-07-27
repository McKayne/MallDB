package com.mall;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.TimeZone;

public class StoreOwnerEntry {

	private static ResultSet getStoreEmployeeByStoreId(int storeId, Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select store_employee_id from store_employee where store_id = ?;");
		statement.setInt(1, storeId); 
		return statement.executeQuery();
	}

	private static ResultSet getStoresByOwner(int ownerId, Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select store_id, name, mall_area_id from store where store_owner_id = ?;");
		statement.setInt(1, ownerId);
		return statement.executeQuery();
	}

	private static ResultSet getRandomStoreOwner(Connection connection) throws SQLException {
		return connection.prepareStatement("select store_owner_id, first_name, last_name from store_owner_entry order by random() limit 1").executeQuery();
	}

	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver");

			try (Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/mall", "postgres", "postgres")) {
				ResultSet owner = getRandomStoreOwner(connection);
				owner.next();
				int ownerId = owner.getInt(1);
				ResultSet store = getStoresByOwner(ownerId, connection);

				java.util.Date date = new java.util.Date();
       	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
       		    df.setTimeZone(TimeZone.getDefault());

				System.out.println();
				System.out.println("Store owner financial report " + df.format(date));
				System.out.println("by " + owner.getString(2) + " " + owner.getString(3));

				int i = 1;
				while (store.next()) {
					System.out.println();
					System.out.println("Store #" + i);
					System.out.println(store.getString(2));

					ResultSet employees = getStoreEmployeeByStoreId(store.getInt(1), connection);
					while (employees.next()) {
						PreparedStatement statement = connection.prepareStatement("select store_accountant_id from store_accountant_entry where store_employee_id = ?");
						statement.setInt(1, employees.getInt(1));
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							int accountantId = result.getInt(1);
							int storeRevenue = StoreAccountantEntry.getTotalRevenue(accountantId, connection);
							System.out.println("Year revenue: $" + storeRevenue);
						}
					}
					i++;
				}
			}
		} catch (ClassNotFoundException ex1) {
			ex1.printStackTrace();
		} catch (SQLException ex2) {
			ex2.printStackTrace();
		}
	}
}