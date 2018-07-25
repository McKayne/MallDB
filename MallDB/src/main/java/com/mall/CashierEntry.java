package com.mall;

import java.sql.*;
import java.text.*;
import java.util.Random;
import java.util.TimeZone;

public class CashierEntry {

	private static Connection connection = CreateAndFill.getConnection();

	public static int getTotalRevenue(int cashierId) throws SQLException {
		ResultSet customers = getCustomersByCashierId(cashierId);

		int i = 1, totalRevenue = 0;
		while (customers.next()) {
			ResultSet purchased = getWaresByPurchaseId(customers.getInt(3));
			int j = 1, totalCustomerRevenue = 0;
			while (purchased.next()) {
				int price = purchased.getInt(2);
				int quantity = purchased.getInt(3);
				int revenue = price * quantity;
				totalCustomerRevenue += revenue;

				j++;
			}

			i++;
			totalRevenue += totalCustomerRevenue;
		}

		return totalRevenue;
	}

	private static ResultSet getWaresByPurchaseId(int purchaseId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name, price, quantity from purchased_wares where purchase_id = ?;");
		statement.setInt(1, purchaseId);
		return statement.executeQuery();
	}

	private static String getCustomerLastNameById(int customerId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select last_name from customer where customer_id = ?;");
		statement.setInt(1, customerId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	private static String getCustomerFirstNameById(int customerId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select first_name from customer where customer_id = ?;");
		statement.setInt(1, customerId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	private static ResultSet getCustomersByCashierId(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select customer_id, number, purchase_id, date from purchase where cashier_id = ?;");
		statement.setInt(1, cashierId);
		return statement.executeQuery();
	}

	public static int getMallLevel(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select number from level where level_id = (select level_id from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?))));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getInt(1);
	}

	public static String getMallArea(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select code from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?)));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	public static String getStoreByEmployeeId(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	public static String getStoreEmployeeLastName(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select last_name from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?);");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	public static String getStoreEmployeeFirstName(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select first_name from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?);");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	private static int getRandomCashier() throws SQLException {
		ResultSet result = connection.prepareStatement("select cashier_id from purchase order by random() limit 1;").executeQuery();
		result.next();
		return result.getInt(1);
	}

	private static void showCashierInfo(int cashierId) throws SQLException {
		String cashierFirstName = getStoreEmployeeFirstName(cashierId);
		String cashierLastName = getStoreEmployeeLastName(cashierId);
		String store = getStoreByEmployeeId(cashierId);
		int level = getMallLevel(cashierId);
		String area = getMallArea(cashierId);

		java.util.Date date = new java.util.Date();
               	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
             	df.setTimeZone(TimeZone.getDefault());

		System.out.println();
		System.out.println("Cashier financial report " + df.format(date));
		System.out.println("by " + cashierFirstName + " " + cashierLastName + "at " + store + ", level " + level + " area " + area);
	}

	public static void main(String args[]) {
		try {
			int cashierId = getRandomCashier();
			showCashierInfo(cashierId);

			ResultSet customers = getCustomersByCashierId(cashierId);

			int i = 1, totalRevenue = 0;
			while (customers.next()) {
				System.out.println();
				System.out.println("Customer #" + i);
				System.out.println(getCustomerFirstNameById(customers.getInt(1)) + " " + getCustomerLastNameById(customers.getInt(1)) + "\t" + customers.getDate(4));
				System.out.println("Purchase ID: " + customers.getInt(2));
				ResultSet purchased = getWaresByPurchaseId(customers.getInt(3));
				int j = 1, totalCustomerRevenue = 0;
				while (purchased.next()) {
					int price = purchased.getInt(2);
					int quantity = purchased.getInt(3);
					int revenue = price * quantity;
					totalCustomerRevenue += revenue;

					System.out.println(j + ". " + purchased.getString(1) + "\t$" + price + " x " + quantity + "\t$" + revenue);
					j++;
				}

				System.out.println();
				System.out.println("Total customer revenue: $" + totalCustomerRevenue);
				System.out.println();

				i++;
				totalRevenue += totalCustomerRevenue;
			}

			System.out.println();
			System.out.println("Total revenue: $" + totalRevenue);
			System.out.println();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
