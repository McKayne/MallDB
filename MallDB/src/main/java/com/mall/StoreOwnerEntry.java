package com.mall;

import java.sql.*;
import java.text.*;
import java.util.*;

public class StoreOwnerEntry {

	private static Connection connection = CreateAndFill.getConnection();

	private static ResultSet getStoreEmployeeByStoreId(int storeId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select store_employee_id from store_employee where store_id = ?;");
		statement.setInt(1, storeId); 
		return statement.executeQuery();
	}

	private static ResultSet getStoresByOwner(int ownerId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select store_id, name, mall_area_id from store where store_owner_id = ?;");
		statement.setInt(1, ownerId);
		return statement.executeQuery();
	}

	private static ResultSet getRandomStoreOwner() throws SQLException {
		return connection.prepareStatement("select store_owner_id, first_name, last_name from store_owner_entry order by random() limit 1").executeQuery();
	}

	public static void main(String[] args) {
		try {
			ResultSet owner = getRandomStoreOwner();
			owner.next();
			int ownerId = owner.getInt(1);
			ResultSet store = getStoresByOwner(ownerId);

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

				ResultSet employees = getStoreEmployeeByStoreId(store.getInt(1));
				while (employees.next()) {
					PreparedStatement statement = connection.prepareStatement("select store_accountant_id from store_accountant_entry where store_employee_id = ?");
					statement.setInt(1, employees.getInt(1));
					ResultSet result = statement.executeQuery();
					if (result.next()) {
						int accountantId = result.getInt(1);
						int storeRevenue = StoreAccountantEntry.getTotalRevenue(accountantId);
						System.out.println("Year revenue: $" + storeRevenue);
					}
				}
				i++;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
