package com.mall;

import java.awt.GridLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.util.ArrayList;
import java.util.Random;

public class StoreAccountantPanel extends JPanel {

	private final Connection connection;

	public int getMallAreaRent(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select additional_rent from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?)));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getInt(1);
	}

	public int getMallLevelRent(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select additional_rent from level where level_id = (select level_id from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?))));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getInt(1);
	}

	private ResultSet getRentAndTax(int accountantId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select rent_loss, tax_loss from store_accountant_entry where store_accountant_id = ?;");
		statement.setInt(1, accountantId);
		return statement.executeQuery();
	}

	private ResultSet getCustomersByCashierId(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select customer_id, number, purchase_id, date from purchase where cashier_id = ?;");
		statement.setInt(1, cashierId);
		return statement.executeQuery();
	}

	public int getTotalRevenue(int cashierId) throws SQLException {
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

	private int getCashierByStoreEmployeeId(int employeeId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select cashier_id from cashier_entry where store_employee_id = ?;");
		statement.setInt(1, employeeId);
		ResultSet result = statement.executeQuery();
		if (!result.next()) {
			return -1;
		} else {
			return result.getInt(1);
		}
	}

	private ResultSet getCashiersFromSameStore(int accountantId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select store_employee_id from store_employee where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from store_accountant_entry where store_accountant_id = ?));");
		statement.setInt(1, accountantId);
		return statement.executeQuery();
	}

	private ResultSet getWaresByPurchaseId(int purchaseId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name, price, quantity from purchased_wares where purchase_id = ?;");
		statement.setInt(1, purchaseId);
		return statement.executeQuery();
	}

	private JPanel getCashierInfo(String firstName, String lastName, String store, int level, String area) {
		JPanel info = new JPanel(new GridLayout(4, 0));
		info.add(new JLabel("Employee: " + firstName + " " + lastName));
		info.add(new JLabel("Store: " + store));
		info.add(new JLabel("Mall level: " + level));
		info.add(new JLabel("Mall area: " + area));
		return info;
	}

	public int getMallLevel(int accountantId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select number from level where level_id = (select level_id from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?))));");
		statement.setInt(1, accountantId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getInt(1);
	}

	public String getMallArea(int accountantId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select code from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?)));");
		statement.setInt(1, accountantId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	public String getStoreByEmployeeId(int accountantId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?));");
		statement.setInt(1, accountantId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	public String getStoreEmployeeLastName(int accountantId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select last_name from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?);");
		statement.setInt(1, accountantId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	public String getStoreEmployeeFirstName(int accountantId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select first_name from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?);");
		statement.setInt(1, accountantId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	private int getRandomStoreAccountant() throws SQLException {
		ResultSet result = connection.prepareStatement("select store_accountant_id from store_accountant_entry order by random() limit 1;").executeQuery();
		result.next();
		return result.getInt(1);
	}

	private JTable getCashierTable(ArrayList<String> firstNames, ArrayList<String> lastNames, ArrayList<String> dates) {
		String columnNames[] = new String[]{"First name", "Last name", "Year revenue"};
		Object[][] data = new Object[firstNames.size()][columnNames.length];
		for (int i = 0; i < firstNames.size(); i++) {
			data[i][0] = firstNames.get(i);
			data[i][1] = lastNames.get(i);
			data[i][2] = dates.get(i);
		}
		return new JTable(data, columnNames);
	}

	public StoreAccountantPanel(Connection connection) {
		super(new GridLayout(4, 0));
		this.connection = connection;
		try {
			int accountantId = getRandomStoreAccountant();
			String accountantFirstName = getStoreEmployeeFirstName(accountantId);
			String accountantLastName = getStoreEmployeeLastName(accountantId);
			String store = getStoreByEmployeeId(accountantId);
			int level = getMallLevel(accountantId);
			String area = getMallArea(accountantId);

			add(getCashierInfo(accountantFirstName, accountantLastName, store, level, area));
			ResultSet cashiers = getCashiersFromSameStore(accountantId);
			ArrayList<String> cashierFirstNames = new ArrayList<String>(), cashierLastNames = new ArrayList<String>(), revenues = new ArrayList<String>();
			final ArrayList purchasedName = new ArrayList(), purchasedPrice = new ArrayList(), purchasedQuantity = new ArrayList();
			String date = "", cashierFirstName = null, cashierLastName = null;
			int i = 0, revenue = 0, totalRevenue = 0;
			while (cashiers.next()) {
				int cashierId = getCashierByStoreEmployeeId(cashiers.getInt(1));
				if (cashierId != -1) {
					cashierFirstName = getStoreEmployeeFirstName(cashierId);
					cashierLastName = getStoreEmployeeLastName(cashierId);
					revenue = getTotalRevenue(cashierId);
					cashierFirstNames.add(cashierFirstName);
					cashierLastNames.add(cashierLastName);
					revenues.add(String.valueOf(revenue));
					totalRevenue += revenue;
					i++;
				}
			}

			final JTable cashierTable = getCashierTable(cashierFirstNames, cashierLastNames, revenues);
			add(new JScrollPane(cashierTable));

			JPanel accountantSummary = new JPanel(new GridLayout(7, 0));
			accountantSummary.add(new JLabel("Total year revenue: $" + totalRevenue));

			ResultSet losses = getRentAndTax(accountantId);
			losses.next();
			int levelRent = getMallLevelRent(accountantId);
			int areaRent = getMallAreaRent(accountantId);
			int totalRent = losses.getInt(1) + levelRent * losses.getInt(1) / 100 + areaRent * losses.getInt(1) / 100;
			accountantSummary.add(new JLabel("Base rent cost: $" + losses.getInt(1)));
			accountantSummary.add(new JLabel("Additional level rent cost: +" + levelRent + "%"));
			accountantSummary.add(new JLabel("Additional area rent cost: +" + areaRent + "%"));
			accountantSummary.add(new JLabel("Total rent cost: $" + totalRent));

			int tax = totalRevenue / 10;
			accountantSummary.add(new JLabel("Revenue tax: $" + totalRevenue / 10));
			accountantSummary.add(new JLabel("Total revenue: $" + (totalRevenue - totalRent - tax)));

			add(accountantSummary);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}