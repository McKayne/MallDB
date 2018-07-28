package com.mall;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;

import java.util.ArrayList;
import java.util.Random;

public class StoreOwnerPanel extends JPanel {

	private final Connection connection;
	private DefaultTableModel storeModel = null;
	private DefaultTableModel employeeModel = null;
	private JTable storeTable, employeeTable;
	private ArrayList employeeIds = new ArrayList(), employeeFirstNames = new ArrayList(), employeeLastNames = new ArrayList(), employeeExperience = new ArrayList(), employeeJobs = new ArrayList(), employeeSalary = new ArrayList();
	private ArrayList<String> firstNames, lastNames;
	private ArrayList<Integer> storeIds = new ArrayList<Integer>();

	protected JTable getStoreTable() {
		return storeTable;
	}

	protected JTable getEmployeeTable() {
		return employeeTable;
	}

	protected ArrayList getEmployeeIds() {
		return employeeIds;
	}

	protected ArrayList getEmployeeFirstNames() {
		return employeeFirstNames;
	}

	protected ArrayList getEmployeeLastNames() {
		return employeeLastNames;
	}

	protected ArrayList getEmployeeExperience() {
		return employeeExperience;
	}

	protected ArrayList getEmployeeJobs() {
		return employeeJobs;
	}

	protected ArrayList getEmployeeSalary() {
		return employeeSalary;
	}

	protected ArrayList<String> getFirstNames() {
		return firstNames;
	}

	protected ArrayList<String> getLastNames() {
		return lastNames;
	}

	protected void deleteStoreFromBase() throws SQLException {
		ResultSet result = connection.prepareStatement("select store_employee_id from store_employee where store_id = " + storeIds.get(storeTable.getSelectedRow()) + ";").executeQuery();
		while (result.next()) {
			connection.prepareStatement("delete from cashier_entry where store_employee_id = " + result.getInt(1) + ";").executeUpdate();
			connection.prepareStatement("delete from store_accountant_entry where store_employee_id = " + result.getInt(1) + ";").executeUpdate();
		}
		PreparedStatement statement = connection.prepareStatement("delete from store where store_id = ?;");
		PreparedStatement statement2 = connection.prepareStatement("delete from store_employee where store_id = ?;");
		statement2.setInt(1, storeIds.get(storeTable.getSelectedRow()));
		statement.setInt(1, storeIds.get(storeTable.getSelectedRow()));
	}

	private void addLabelTextRows(JLabel[] labels, JTextField[] textFields, GridBagLayout gridbag, Container container) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		int numLabels = labels.length;

		for (int i = 0; i < numLabels; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
			c.fill = GridBagConstraints.NONE;      //reset to default
			c.weightx = 0.0;                       //reset to default
			container.add(labels[i], c);

			c.gridwidth = GridBagConstraints.REMAINDER;     //end row
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			container.add(textFields[i], c);
		}
	}

	protected void showInsertFrame() {
		final JFrame insertFrame = new JFrame();
		insertFrame.setTitle("New store");

		insertFrame.setLayout(new BorderLayout());

		//Create a regular text field.
		final JTextField storeName = new JTextField(20);
		storeName.setActionCommand("Store name:");

		final JTextField lastName = new JTextField(20);
		lastName.setActionCommand("Mall level: ");

		//Create a formatted text field.
		final JFormattedTextField ftf = new JFormattedTextField("");
		ftf.setActionCommand("test");

		//Create some labels for the fields.
		JLabel textFieldLabel = new JLabel("Store name: ");
		textFieldLabel.setLabelFor(storeName);
		JLabel passwordFieldLabel = new JLabel("Mall level: ");
		passwordFieldLabel.setLabelFor(lastName);
		JLabel ftfLabel = new JLabel("Mall area: ");
		ftfLabel.setLabelFor(ftf);

		//Create a label to put messages during an action event.
		JLabel actionLabel = new JLabel("Type text in a field and press Enter.");
		actionLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

		//Lay out the text controls and the labels.
		JPanel textControlsPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		textControlsPane.setLayout(gridbag);

		JLabel[] labels = {textFieldLabel, passwordFieldLabel, ftfLabel};
		JTextField[] textFields = {storeName, lastName, ftf};
		addLabelTextRows(labels, textFields, gridbag, textControlsPane);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		JButton next = new JButton("Next");
		textControlsPane.add(next);
		next.addActionListener(new StoreOwnerNextButtonActionListener(this, insertFrame, storeName, storeModel));
		JButton cancel = new JButton("Cancel");
		textControlsPane.add(cancel);
		cancel.addActionListener(new CancelButtonActionListener(insertFrame));
		textControlsPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Info"), BorderFactory.createEmptyBorder(5,5,5,5)));
		insertFrame.add(textControlsPane);
		insertFrame.pack();
		insertFrame.setVisible(true);
	}

	private ResultSet getJobById(int jobId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name, salary from job where job_id = ?;");
		statement.setInt(1, jobId);
		return statement.executeQuery(); 
	}

	protected void deleteRows(JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowCount = model.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}

	protected void fillEmployeeTable(ArrayList employeeFirstNames, ArrayList employeeLastNames, ArrayList employeeExperience, ArrayList employeeJobs, ArrayList employeeSalary) {
		deleteRows(employeeTable);
		for (int i = 0; i < employeeFirstNames.size(); i++) {
			employeeModel.addRow(new Object[]{employeeFirstNames.get(i), employeeLastNames.get(i), employeeExperience.get(i), employeeJobs.get(i), employeeSalary.get(i)});
		}
	}

	private ResultSet getWaresByPurchaseId(int purchaseId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name, price, quantity from purchased_wares where purchase_id = ?;");
		statement.setInt(1, purchaseId);
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

	private ResultSet getStoreEmployeeByStoreId(int storeId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select store_employee_id, first_name, last_name, experience, job_id from store_employee where store_id = ?;");
		statement.setInt(1, storeId);
		return statement.executeQuery();
	}

	private ResultSet getStoresByOwner(int ownerId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select store_id, name, mall_area_id from store where store_owner_id = ?;");
		statement.setInt(1, ownerId);
		return statement.executeQuery();
	}

	private ResultSet getRandomStoreOwner() throws SQLException {
		return connection.prepareStatement("select store_owner_id, first_name, last_name from store_owner_entry order by random() limit 1").executeQuery();
	}

	public StoreOwnerPanel(Connection connection) {
		super(new GridLayout(5, 0));
		this.connection = connection;
		try {
			ResultSet owner = getRandomStoreOwner();
			owner.next();
			int ownerId = owner.getInt(1);
			ResultSet store = getStoresByOwner(ownerId);

			String columnNames[] = new String[]{"Store", "Year revenue"};
			storeModel = new DefaultTableModel(null, columnNames);
			JPanel storeTablePanel = new JPanel(new GridLayout(1, 0));
			storeTable = new JTable(storeModel);
			storeTable.addKeyListener(new StoreTableKeyListener(this));
			storeTable.addMouseListener(new StoreTableMouseListener(this));
			storeTablePanel.add(new JScrollPane(storeTable));
			storeTablePanel.setBorder(BorderFactory.createTitledBorder("Store owner " + owner.getString(2) + " " + owner.getString(3)));
			add(storeTablePanel);

			int i = 0, totalRevenue = 0;
			while (store.next()) {
				storeIds.add(store.getInt(1));
				ResultSet employees = getStoreEmployeeByStoreId(store.getInt(1));
				employeeIds.add(new ArrayList());
				employeeFirstNames.add(new ArrayList());
				employeeLastNames.add(new ArrayList());
				employeeExperience.add(new ArrayList());
				employeeJobs.add(new ArrayList());
				employeeSalary.add(new ArrayList());
				while (employees.next()) {
					ArrayList v1 = (ArrayList) employeeIds.get(i);
					ArrayList v2 = (ArrayList) employeeFirstNames.get(i);
					ArrayList v3 = (ArrayList) employeeLastNames.get(i);
					ArrayList v4 = (ArrayList) employeeExperience.get(i);
					ArrayList v5 = (ArrayList) employeeJobs.get(i);
					ArrayList v6 = (ArrayList) employeeSalary.get(i);
					v1.add(String.valueOf(employees.getInt(1)));
					v2.add(String.valueOf(employees.getString(2)));
					v3.add(String.valueOf(employees.getString(3)));
					v4.add(String.valueOf(employees.getInt(4)));
					ResultSet job = getJobById(employees.getInt(5));
					job.next();
					v5.add(job.getString(1));
					v6.add(String.valueOf(job.getInt(2)));
					PreparedStatement statement = connection.prepareStatement("select store_accountant_id from store_accountant_entry where store_employee_id = ?");
					statement.setInt(1, employees.getInt(1));
					ResultSet result = statement.executeQuery();
					if (result.next()) {
						int accountantId = result.getInt(1);
						int storeRevenue = getTotalRevenue(accountantId);
						totalRevenue += storeRevenue;
						storeModel.addRow(new Object[]{store.getString(2), String.valueOf(storeRevenue)});
					}
				}
				i++;
			}
			JPanel panel = new JPanel();
			JButton bcc = new JButton("Add new store...");
			bcc.addActionListener(new AddButtonActionListener(this));
			panel.add(bcc);
			add(panel);

			JPanel revenuePanel = new JPanel(new GridLayout(1, 0));
			revenuePanel.add(new JLabel("Total revenue: $" + totalRevenue));
			add(revenuePanel);

			JPanel employeeTablePanel = new JPanel(new GridLayout(1, 0));
			employeeTablePanel.setBorder(BorderFactory.createTitledBorder("Store employees"));
			employeeModel = new DefaultTableModel(null, new String[]{"First name", "Last name", "Experience", "Job", "Salary"});
			employeeTable = new JTable(employeeModel);
			employeeTable.addKeyListener(new EmployeeTableKeyListener(this));
			fillEmployeeTable((ArrayList) employeeFirstNames.get(0), (ArrayList) employeeLastNames.get(0), (ArrayList) employeeExperience.get(0), (ArrayList) employeeJobs.get(0), (ArrayList) employeeSalary.get(0));
			employeeTablePanel.add(new JScrollPane(employeeTable));
			add(employeeTablePanel);

			JPanel panel2 = new JPanel();
			JButton button2 = new JButton("Add new employee...");
			panel2.add(button2);
			add(panel2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}