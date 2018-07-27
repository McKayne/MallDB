package com.mall;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Random;

public class CreateAndFill { 

	private static int storeOwnerQuantity, storeQuantity, storeEmployeeQuantity, customerQuantity, purchaseQuantity, cashierQuantity;
	private static Random random = new Random();
	private static ArrayList<String> firstNames, lastNames;

	private static void fillPurchasedWaresTable(Connection connection) throws SQLException {
		ArrayList<String> wares = new ArrayList<>();
		try (FileInputStream fileInput = new FileInputStream("wares.txt");
			DataInputStream dataInput = new DataInputStream(fileInput);
			BufferedReader reader = new BufferedReader(new InputStreamReader(dataInput))) {
				String inputLine;
				while ((inputLine = reader.readLine()) != null) {
					wares.add(inputLine);
				}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		PreparedStatement statement = connection.prepareStatement("insert into purchased_wares(purchase_id, number, name, price, quantity) values(?, ?, ?, ?, ?);");
		for (int i = 0; i < purchaseQuantity; i++) {
			for (int j = 0; j < random.nextInt(5) + 1; j++) {
				statement.setInt(1, i + 1);
				statement.setInt(2, j);
				statement.setString(3, wares.get(random.nextInt(wares.size())));
				statement.setInt(4, 100 + random.nextInt(10) * 100);
				statement.setInt(5, random.nextInt(5) + 1);
				statement.executeUpdate();
			}
		}
	}

	private static void fillPurchaseTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into purchase(number, date, payment_type_id, customer_id, cashier_id) values(?, ?, ?, ?, ?);");
		purchaseQuantity = (random.nextInt(3) + 1) * customerQuantity;
		for (int i = 0; i < purchaseQuantity; i++) {
			int month = random.nextInt(12) + 1;
			int day = random.nextInt(25) + 1;
			statement.setInt(1, random.nextInt(9000000) + 1000000);
			statement.setDate(2, Date.valueOf("2015-" + month + "-" + day));
			statement.setInt(3, random.nextInt(4) + 1);
			statement.setInt(4, random.nextInt(customerQuantity) + 1);
			statement.setInt(5, random.nextInt(cashierQuantity) + 1);
			statement.executeUpdate();
		}
	}

	private static void fillPaymentTypeTable(Connection connection) throws SQLException {
		String data[] = new String[]{"Cash", "VISA", "MasterCard", "Maestro"};
		PreparedStatement statement = connection.prepareStatement("insert into payment_type(type) values(?);");
		for (int i = 0; i < data.length; i++) {
			statement.setString(1, data[i]);
			statement.executeUpdate();
		} 
	}

	private static void fillCustomerTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into customer(first_name, last_name, last_visit) values(?, ?, ?);");
		customerQuantity = random.nextInt(1000) + 1200; 
		for (int i = 0; i < customerQuantity; i++) {
			int month = random.nextInt(12) + 1;
			int day = random.nextInt(25) + 1;
			statement.setString(1, firstNames.get(random.nextInt(firstNames.size())));
			statement.setString(2, lastNames.get(random.nextInt(lastNames.size())));
			statement.setDate(3, Date.valueOf("2015-" + month + "-" + day));
			statement.executeUpdate();
		}
	}

	private static void fillStoreAccountantEntryTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into store_accountant_entry(revenue, rent_loss, tax_loss, store_employee_id) values(?, ?, ?, ?);");
		for (int i = 0; i < storeQuantity; i++) {
			statement.setInt(1, 100);
			statement.setInt(2, random.nextInt(11) * 10000 + 50000);
			statement.setInt(3, 20);
			statement.setInt(4, random.nextInt(storeEmployeeQuantity) + 1);
			statement.executeUpdate();
		}
	}

	private static void fillCashierEntryTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into cashier_entry(store_employee_id) values(?);");
		for (int i = 0; i < storeQuantity; i++) {
			statement.setInt(1, random.nextInt(storeEmployeeQuantity) + 1);
			statement.executeUpdate();
		}
	}

	private static void fillStoreEmployeeTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into store_employee(first_name, last_name, experience, job_id, store_id) values(?, ?, ?, ?, ?);");
		PreparedStatement cashier = connection.prepareStatement("insert into cashier_entry(store_employee_id) values(?);"); 
		PreparedStatement accountant = connection.prepareStatement("insert into store_accountant_entry(revenue, rent_loss, tax_loss, store_employee_id) values(?, ?, ?, ?);");
		storeEmployeeQuantity = 0;//(random.nextInt(5) + 2) * storeQuantity;
		int lastEmployee = 0;
		cashierQuantity = 0;
		for (int i = 0; i < storeQuantity; i++) {
			int c = random.nextInt(2) + 3;
			int k = random.nextInt(10) + 5;
			storeEmployeeQuantity += k;
			for (int j = 0; j < k; j++) {
				statement.setString(1, firstNames.get(random.nextInt(firstNames.size())));
				statement.setString(2, lastNames.get(random.nextInt(lastNames.size())));
				statement.setInt(3, random.nextInt(10));
				statement.setInt(4, random.nextInt(8) + 1);
				statement.setInt(5, i + 1);
				statement.executeUpdate();
				if (j < c) {
					cashier.setInt(1, lastEmployee + 1);
					cashier.executeUpdate();
					cashierQuantity++;
				}
				if (j == c) {
					accountant.setInt(1, 100);
					accountant.setInt(2, random.nextInt(11) * 10000 + 50000);
					accountant.setInt(3, 20);
					accountant.setInt(4, lastEmployee + 1);
					accountant.executeUpdate();
				}
				lastEmployee++;
			}
		}
	}

	private static void fillJobTable(Connection connection) throws SQLException {
		String job[] = new String[]{"Accountant", "Analytics", "Big Data", "Cashier", "Cloud Computing", "Database Programming", "Internet Security", "Network Administration"};
		PreparedStatement statement = connection.prepareStatement("insert into job(name, salary) values(?, ?);");
		for (int i = 0; i < job.length; i++) {
			statement.setString(1, job[i]); 
			statement.setInt(2, random.nextInt(10) * 250 + 1000);
			statement.executeUpdate();
		}
	}

	private static void fillStoreTable(Connection connection) throws SQLException {
		ArrayList<String> stores = new ArrayList<>();
		try (FileInputStream fileInput = new FileInputStream("stores.txt");
			DataInputStream dataInput = new DataInputStream(fileInput);
			BufferedReader reader = new BufferedReader(new InputStreamReader(dataInput))) {
				String inputLine;
				while ((inputLine = reader.readLine()) != null) {
					stores.add(inputLine);
				}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		PreparedStatement statement = connection.prepareStatement("insert into store(name, mall_area_id, store_owner_id) values(?, ?, ?);");
		storeQuantity = random.nextInt(100) + 50;
		for (int i = 0; i < storeQuantity; i++) {
			statement.setString(1, stores.get(random.nextInt(stores.size())));
			statement.setInt(2, random.nextInt(25) + 1);
			statement.setInt(3, random.nextInt(storeOwnerQuantity) + 1);
			statement.executeUpdate();
		}
	}

	private static void fillStoreOwnerEntryTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into store_owner_entry(first_name, last_name) values(?, ?);");
		storeOwnerQuantity = random.nextInt(30) + 20;
		for (int i = 0; i < storeOwnerQuantity; i++) {
			statement.setString(1, firstNames.get(random.nextInt(firstNames.size())));
			statement.setString(2, lastNames.get(random.nextInt(lastNames.size())));
			statement.executeUpdate();
		}
	}

	private static void fillMallAreaTable(Connection connection) throws SQLException {
		String code[] = new String[25];
		for (int i = 0; i < code.length; i++) {
			code[i] = "";
			code[i] += (char) ((int) 'A' + i);
		}
		int additionalRent[] = new int[]{0, 5, 3, 0, 0,
						10, 25, 15, 5, 8,
						8, 20, 10, 3, 4,
						5, 10, 8, 2, 3,
						4, 8, 6, 1, 2};
		PreparedStatement statement = connection.prepareStatement("insert into mall_area(code, additional_rent, level_id) values(?, ?, ?);");
		for (int i = 0; i < code.length; i++) {
			statement.setString(1, code[i]);
			statement.setInt(2, additionalRent[i]);
			statement.setInt(3, i / 5 + 1);
			statement.executeUpdate();
		}
	}

	private static void fillLevelTable(Connection connection) throws SQLException {
		int number[] = new int[]{-1, 0, 1, 2, 3};
		int additionalRent[] = new int[]{0, 25, 10, 8, 5};
		PreparedStatement statement = connection.prepareStatement("insert into level(number, additional_rent) values(?, ?);");
		for (int i = 0; i < number.length; i++) {
			statement.setInt(1, number[i]);
			statement.setInt(2, additionalRent[i]);
			statement.executeUpdate();
		}
	}

	private static void fillLastNames() {
		lastNames = new ArrayList<>();
		try (FileInputStream fileInput = new FileInputStream("lastNames.txt");
			DataInputStream dataInput = new DataInputStream(fileInput);
			BufferedReader reader = new BufferedReader(new InputStreamReader(dataInput))) {
				String inputLine;
				while ((inputLine = reader.readLine()) != null) {
					lastNames.add(inputLine);
				}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void fillFirstNames() {
		firstNames = new ArrayList<>();
		try (FileInputStream fileInput = new FileInputStream("firstNames.txt");
			DataInputStream dataInput = new DataInputStream(fileInput);
			BufferedReader reader = new BufferedReader(new InputStreamReader(dataInput))) {
				String inputLine;
				while ((inputLine = reader.readLine()) != null) {
					firstNames.add(inputLine);
				}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void createPurchasedWaresTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table purchased_wares("
		+ "purchased_wares_id serial primary key,"
		+ "number integer,"
		+ "name varchar(100),"
		+ "price integer,"
		+ "quantity integer,"
		+ "purchase_id serial references purchase(purchase_id));");
		statement.executeUpdate();
	}

	private static void createPurchaseTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table purchase("
		+ "purchase_id serial primary key,"
		+ "number integer,"
		+ "date date,"
		+ "payment_type_id serial references payment_type(payment_type_id),"
		+ "customer_id serial references customer(customer_id),"
		+ "cashier_id serial references cashier_entry(cashier_id));");
		statement.executeUpdate();
	}

	private static void createPaymentTypeTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table payment_type("
		+ "payment_type_id serial primary key,"
		+ "type varchar(20));");
		statement.executeUpdate();
	}

	private static void createCustomerTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table customer("
		+ "customer_id serial primary key,"
		+ "first_name varchar(100),"
		+ "last_name varchar(100),"
		+ "last_visit date);");
		statement.executeUpdate();
	}

	private static void createStoreAccountantEntryTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table store_accountant_entry("
		+ "store_accountant_id serial primary key,"
		+ "revenue integer,"
		+ "rent_loss integer,"
		+ "tax_loss integer,"
		+ "store_employee_id serial references store_employee(store_employee_id));");
		statement.executeUpdate();
	}

	private static void createCashierEntryTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table cashier_entry("
		+ "cashier_id serial primary key,"
		+ "store_employee_id serial references store_employee(store_employee_id));");
		statement.executeUpdate();
	}

	private static void createStoreEmployeeTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table store_employee("
		+ "store_employee_id serial primary key,"
		+ "first_name varchar(100),"
		+ "last_name varchar(100),"
		+ "experience integer,"
		+ "job_id serial references job(job_id),"
		+ "store_id serial references store(store_id));");
		statement.executeUpdate();
	}

	private static void createJobTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table job("
		+ "job_id serial primary key,"
		+ "name varchar(50),"
		+ "salary integer);");
		statement.executeUpdate();
	}

	private static void createStoreTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table store("
		+ "store_id serial primary key,"
		+ "name varchar(50),"
		+ "mall_area_id serial references mall_area(mall_area_id),"
		+ "store_owner_id serial references store_owner_entry(store_owner_id));");
		statement.executeUpdate();
	}

	private static void createStoreOwnerEntryTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table store_owner_entry("
		+ "store_owner_id serial primary key,"
		+ "first_name varchar(100),"
		+ "last_name varchar(100));");
		statement.executeUpdate();
	}

	private static void createMallAreaTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table mall_area("
		+ "mall_area_id serial primary key,"
		+ "code varchar(1),"
		+ "additional_rent integer,"
		+ "level_id serial references level(level_id));");
		statement.executeUpdate();
	} 

	private static void createLevelTable(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("create table level("
		+ "level_id serial primary key,"
		+ "number integer,"
		+ "additional_rent integer);");
		statement.executeUpdate(); 
	}

	private static void fillAllTables(Connection connection) throws SQLException {
		fillLevelTable(connection);
		fillMallAreaTable(connection);
		fillFirstNames();
		fillLastNames();
		fillStoreOwnerEntryTable(connection);
		fillStoreTable(connection);
		fillJobTable(connection);
		fillStoreEmployeeTable(connection);
		fillCustomerTable(connection);
		fillPaymentTypeTable(connection);
		fillPurchaseTable(connection);
		fillPurchasedWaresTable(connection);
	}

	private static void createAllTables(Connection connection) throws SQLException {
		createLevelTable(connection);
		createMallAreaTable(connection);
		createStoreOwnerEntryTable(connection);
		createStoreTable(connection);
		createJobTable(connection);
		createStoreEmployeeTable(connection);
		createCashierEntryTable(connection);
		createStoreAccountantEntryTable(connection);
		createCustomerTable(connection);
		createPaymentTypeTable(connection);
		createPurchaseTable(connection);
		createPurchasedWaresTable(connection);
	}

	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver");

			try (Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/mall", "postgres", "postgres")) {
				createAllTables(connection);
				fillAllTables(connection);
			}
		} catch (ClassNotFoundException ex1) {
			ex1.printStackTrace();
		} catch (SQLException ex2) {
			ex2.printStackTrace();
		}
	}
}