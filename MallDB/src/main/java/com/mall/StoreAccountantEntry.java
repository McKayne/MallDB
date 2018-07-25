package com.mall;

import java.sql.*;
import java.text.*;
import java.util.*;

public class StoreAccountantEntry {

	private static Connection connection = CreateAndFill.getConnection();

	public static int getTotalRevenue(int accountantId) throws SQLException {
		ResultSet cashiers = getCashiersFromSameStore(accountantId);
		int i = 1, totalRevenue = 0;
		while (cashiers.next()) {
			int cashierId = getCashierByStoreEmployeeId(cashiers.getInt(1));
			if (cashierId != -1) {
				String cashierFirstName = CashierEntry.getStoreEmployeeFirstName(cashierId);
				String cashierLastName = CashierEntry.getStoreEmployeeLastName(cashierId);
				int revenue = CashierEntry.getTotalRevenue(cashierId);
				totalRevenue += revenue;
				i++;
			}
		}

		ResultSet losses = getRentAndTax(accountantId);
		losses.next();
		int levelRent = getMallLevelRent(accountantId);
		int areaRent = getMallAreaRent(accountantId);
		int totalRent = losses.getInt(1) + levelRent * losses.getInt(1) / 100 + areaRent * losses.getInt(1) / 100;

		int tax = totalRevenue / 10;

		return totalRevenue - totalRent - tax;
	}

	public static int getMallAreaRent(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select additional_rent from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?)));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getInt(1);
	}

	public static int getMallLevelRent(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select additional_rent from level where level_id = (select level_id from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?))));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getInt(1);
	}

	private static ResultSet getRentAndTax(int accountantId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select rent_loss, tax_loss from store_accountant_entry where store_accountant_id = ?;");
		statement.setInt(1, accountantId);
		return statement.executeQuery();
	}

	private static int getCashierByStoreEmployeeId(int employeeId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select cashier_id from cashier_entry where store_employee_id = ?;");
		statement.setInt(1, employeeId);
		ResultSet result = statement.executeQuery();
		if (!result.next()) {
			return -1;
		} else {
			return result.getInt(1); 
		}
	}

	private static ResultSet getCashiersFromSameStore(int accountantId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select store_employee_id from store_employee where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from store_accountant_entry where store_accountant_id = ?));");
		statement.setInt(1, accountantId);
		return statement.executeQuery();
	}

	private static ResultSet getRandomStoreAccountant() throws SQLException {
		ResultSet result = connection.prepareStatement("select store_accountant_id, store_employee_id from store_accountant_entry order by random() limit 1;").executeQuery();
		return result;
		//result.next();
		//return result.getInt(1);
	}

	private static void showStoreAccountantInfo(int accountantId, String store) throws SQLException {
		String firstName = CashierEntry.getStoreEmployeeFirstName(accountantId);
		String lastName = CashierEntry.getStoreEmployeeLastName(accountantId);
		int level = CashierEntry.getMallLevel(accountantId);
		String area = CashierEntry.getMallArea(accountantId);

		java.util.Date date = new java.util.Date();
               	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
             	df.setTimeZone(TimeZone.getDefault());

		System.out.println();
		System.out.println("Store accountant financial report " + df.format(date));
		System.out.println("by " + firstName + " " + lastName + "at " + store + ", level " + level + " area " + area);
	}

	public static void main(String[] args) {
		try {
			ResultSet accountant = getRandomStoreAccountant();
			accountant.next();
			int accountantId = accountant.getInt(1);
			String store = CashierEntry.getStoreByEmployeeId(accountantId);
			showStoreAccountantInfo(accountantId, store);

			ResultSet cashiers = getCashiersFromSameStore(accountantId);
			int i = 1, totalRevenue = 0;
			while (cashiers.next()) {
				int cashierId = getCashierByStoreEmployeeId(cashiers.getInt(1));
				if (cashierId != -1) {
					System.out.println();
					System.out.println("Cashier #" + i);
					String cashierFirstName = CashierEntry.getStoreEmployeeFirstName(cashierId);
					String cashierLastName = CashierEntry.getStoreEmployeeLastName(cashierId);
					System.out.println(cashierFirstName + " " + cashierLastName);
					int revenue = CashierEntry.getTotalRevenue(cashierId);
					System.out.println("Year revenue: $" + revenue);
					totalRevenue += revenue;
					i++;
				}
			}

			System.out.println();
			System.out.println("Total year revenue: $" + totalRevenue);

			ResultSet losses = getRentAndTax(accountantId);
			losses.next();
			int levelRent = getMallLevelRent(accountantId);
			int areaRent = getMallAreaRent(accountantId);
			int totalRent = losses.getInt(1) + levelRent * losses.getInt(1) / 100 + areaRent * losses.getInt(1) / 100;
			System.out.println();
			System.err.println("Base rent cost: $" + losses.getInt(1));
			System.out.println("Additional level rent cost: +" + levelRent + "%");
			System.out.println("Additional area rent cost: +" + areaRent + "%");
			System.out.println("Total rent cost: $" + totalRent);

			int tax = totalRevenue / 10;
			System.out.println();
			System.out.println("Revenue tax: $" + totalRevenue / 10);

			System.out.println();
			System.out.println("Total revenue: $" + (totalRevenue - totalRent - tax));
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
