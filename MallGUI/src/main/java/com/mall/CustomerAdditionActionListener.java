package com.mall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;

public class CustomerAdditionActionListener implements ActionListener {

	private final Connection connection;
	private final CashierPanel panel;
	private final JFrame insertFrame;
	private final int cashierId;
	private final JTextField firstNameField, secondNameField;
	private final JFormattedTextField ftf;

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into customer(first_name, last_name, last_visit) values(?, ?, ?);");
			statement.setString(1, firstNameField.getText());
			statement.setString(2, secondNameField.getText());
			statement.setDate(3, java.sql.Date.valueOf(ftf.getText()));
			statement.executeUpdate();
			panel.getCustomerFirstNames().add(firstNameField.getText());
			panel.getCustomerLastNames().add(secondNameField.getText());
			panel.getDates().add(ftf.getText());
			((DefaultTableModel) panel.getCustomerTable().getModel()).addRow(new Object[]{firstNameField.getText(), secondNameField.getText(), ftf.getText()});
			ArrayList name = new ArrayList();
			ArrayList price = new ArrayList();
			ArrayList quantity = new ArrayList();
			String wares[] = new String[]{"Women's Fashion",
				"Men's Fashion",
				"Kids & Baby Fashion",
				"Office Supplies & Electronics",
				"Kindle Books",
				"Toys & Games",
				"Electronics & Accessories",
				"Musical Instruments",
				"Sports & Fitness",
				"Books",
				"Beauty & Grooming",
				"Home, Kitchen & Garden",
				"Tools & Home Improvement",
				"Camera, Photo & Video",
				"Computers & Accessories",
				"Health & Personal Care"};
			Random random = new Random();
			PreparedStatement st = connection.prepareStatement("insert into purchase(number, date, payment_type_id, customer_id, cashier_id) values(?, ?, ?, ?, ?);");
			st.setInt(1, random.nextInt(9000000) + 1000000);
			st.setDate(2, java.sql.Date.valueOf(ftf.getText()));
			st.setInt(3, 1);
			st.setInt(4, panel.getCustomerFirstNames().size());
			st.setInt(5, cashierId);
			st.executeUpdate();
			PreparedStatement statement2 = connection.prepareStatement("insert into purchased_wares(purchase_id, number, name, price, quantity) values(?, ?, ?, ?, ?);");
			for (int i = 0; i < random.nextInt(10) + 1; i++) {
				int w = random.nextInt(wares.length);
				int p = random.nextInt(10) * 100 + 100;
				int q = random.nextInt(5) + 1;
				statement2.setInt(1, 1);
				statement2.setInt(2, 0);
				statement2.setString(3, wares[w]);
				statement2.setInt(4, p);
				statement2.setInt(5, q);
				name.add(wares[w]);
				price.add(p);
				quantity.add(q);
				statement.executeUpdate();
			}
			panel.getPurchasedName().add(name);
			panel.getPurchasedPrice().add(price);
			panel.getPurchasedQuantity().add(quantity);
			panel.getPurchaseNumber().add("12345");
			panel.getPurchaseDate().add("2016-01-01");
			insertFrame.setVisible(false);
			JOptionPane.showMessageDialog(null, "Customer added successfully");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public CustomerAdditionActionListener(Connection connection, CashierPanel panel, int cashierId, JTextField firstNameField, JTextField secondNameField, JFormattedTextField ftf, JFrame insertFrame) {
		this.connection = connection;
		this.panel = panel;
		this.cashierId = cashierId;
		this.firstNameField = firstNameField;
		this.secondNameField = secondNameField;
		this.ftf = ftf;
		this.insertFrame =  insertFrame;
	}
}