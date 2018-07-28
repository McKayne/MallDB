package com.mall;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class CashierPanel extends JPanel {

	private final Connection connection;
	private int cashierId;
	private JTable customerTable, purchasedTable;
	private JLabel numberLabel, dateLabel;
	private TitledBorder purchaseBorder = BorderFactory.createTitledBorder("");
	private ArrayList<Integer> customerIds = new ArrayList<Integer>(), purchaseIds = new ArrayList<Integer>();
	private ArrayList<String> customerFirstNames = new ArrayList<>(), customerLastNames = new ArrayList<>(), dates = new ArrayList<>();
	private ArrayList purchasedName = new ArrayList(), purchasedPrice = new ArrayList(), purchasedQuantity = new ArrayList(), purchaseNumber = new ArrayList(), purchaseDate = new ArrayList();

	protected JTable getCustomerTable() {
		return customerTable;
	}

	protected JTable getPurchasedTable() {
		return purchasedTable;
	}

	protected ArrayList<String> getCustomerFirstNames() {
		return customerFirstNames;
	}

	protected ArrayList<String> getCustomerLastNames() {
		return customerLastNames;
	}

	protected ArrayList<String> getDates() {
		return dates;
	}

	protected ArrayList getPurchasedName() {
		return purchasedName;
	}

	protected ArrayList getPurchasedPrice() {
		return purchasedPrice;
	}

	protected ArrayList getPurchasedQuantity() {
		return purchasedQuantity;
	}

	protected ArrayList getPurchaseNumber() {
		return purchaseNumber;
	}

	protected ArrayList getPurchaseDate() {
		return purchaseDate;
	}

	protected void deleteCustomerFromBase() throws SQLException {
		ResultSet result = connection.prepareStatement("select purchase_id from purchase where customer_id = " + customerIds.get(customerTable.getSelectedRow()) + ";").executeQuery();
		while (result.next()) {
			connection.prepareStatement("delete from purchased_wares where purchase_id = " + result.getInt(1) + ";").executeUpdate();
		}
		PreparedStatement statement2 = connection.prepareStatement("delete from purchase where customer_id = ?;");
		statement2.setInt(1, customerIds.get(customerTable.getSelectedRow()));
		statement2.executeUpdate();
		PreparedStatement statement3 = connection.prepareStatement("delete from customer cascade where customer_id = ?;");
		statement3.setInt(1, customerIds.get(customerTable.getSelectedRow()));
		statement3.executeUpdate();
	}

	protected void showInsertFrame() {
		final JFrame insertFrame = new JFrame();
		
		insertFrame.setTitle("New customer");

		insertFrame.setLayout(new BorderLayout());

		//Create a regular text field.
		final JTextField firstNameField = new JTextField(20);
		firstNameField.setActionCommand("First name:");
		
		final JTextField secondNameField = new JTextField(20);
		secondNameField.setActionCommand("Second name: ");
		
		//Create a formatted text field.
		final JFormattedTextField ftf = new JFormattedTextField("2016-01-01");
		ftf.setActionCommand("test");
		
		//Create some labels for the fields.
		JLabel textFieldLabel = new JLabel("First name: ");
		textFieldLabel.setLabelFor(firstNameField);
		JLabel secondNameFieldLabel = new JLabel("Second name: ");
		secondNameFieldLabel.setLabelFor(secondNameField);
		JLabel ftfLabel = new JLabel("Last visit: ");
		ftfLabel.setLabelFor(ftf);

		//Create a label to put messages during an action event.
		JLabel actionLabel = new JLabel("Type text in a field and press Enter.");
		actionLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

		//Lay out the text controls and the labels.
		JPanel textControlsPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		textControlsPane.setLayout(gridbag);

		JLabel[] labels = {textFieldLabel, secondNameFieldLabel, ftfLabel};
		JTextField[] textFields = {firstNameField, secondNameField, ftf};
		addLabelTextRows(labels, textFields, gridbag, textControlsPane);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		JButton next = new JButton("Next");
		textControlsPane.add(next);
		next.addActionListener(new CustomerAdditionActionListener(connection, this, cashierId, firstNameField, secondNameField, ftf, insertFrame));
		JButton cancel = new JButton("Cancel");
		textControlsPane.add(cancel);
		cancel.addActionListener(new CancelButtonActionListener(insertFrame));
		textControlsPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Personal"), BorderFactory.createEmptyBorder(5,5,5,5)));

		insertFrame.add(textControlsPane);
		insertFrame.pack();
		insertFrame.setVisible(true);
	}

	protected void deleteRows(JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowCount = model.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}

	protected void deleteItem() {
		((ArrayList) (purchasedName.get(customerTable.getSelectedRow()))).remove(purchasedTable.getSelectedRow());
		((ArrayList) (purchasedPrice.get(customerTable.getSelectedRow()))).remove(purchasedTable.getSelectedRow());
		((ArrayList) (purchasedQuantity.get(customerTable.getSelectedRow()))).remove(purchasedTable.getSelectedRow());
		purchaseNumber.remove(purchasedTable.getSelectedRow());
		purchaseDate.remove(purchasedTable.getSelectedRow());
		((DefaultTableModel) purchasedTable.getModel()).removeRow(purchasedTable.getSelectedRow());
		JOptionPane.showMessageDialog(null, "Item deleted");
	}

	protected void updatePurchaseInfo(ArrayList purchaseNumber, ArrayList purchaseDate) {
		int selected = customerTable.getSelectedRow();
		if (selected == -1) {
			selected = 0;
		}
		purchaseBorder.setTitle("Purchase number " + purchaseNumber.get(selected) + "        " + purchaseDate.get(selected));
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

	private String getCustomerDateById(int customerId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select last_visit from customer where customer_id = ?;");
		statement.setInt(1, customerId);
		ResultSet result = statement.executeQuery();
		result.next();
		return String.valueOf(result.getDate(1));
	}

	private String getCustomerLastNameById(int customerId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select last_name from customer where customer_id = ?;");
		statement.setInt(1, customerId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	private String getCustomerFirstNameById(int customerId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select first_name from customer where customer_id = ?;");
		statement.setInt(1, customerId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	private ResultSet getCustomersByCashierId(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select customer_id, number, purchase_id, date from purchase where cashier_id = ?;");
		statement.setInt(1, cashierId);
		return statement.executeQuery();
	}

	public int getMallLevel(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select number from level where level_id = (select level_id from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?))));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getInt(1);
	}

	public String getMallArea(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select code from mall_area where mall_area_id = (select mall_area_id from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?)));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	public String getStoreByEmployeeId(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name from store where store_id = (select store_id from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?));");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	public String getStoreEmployeeLastName(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select last_name from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?);");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	public String getStoreEmployeeFirstName(int cashierId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select first_name from store_employee where store_employee_id = (select store_employee_id from cashier_entry where cashier_id = ?);");
		statement.setInt(1, cashierId);
		ResultSet result = statement.executeQuery();
		result.next();
		return result.getString(1);
	}

	private int getRandomCashier() throws SQLException {
		ResultSet result = connection.prepareStatement("select cashier_id from purchase order by random() limit 1;").executeQuery();
		result.next();
		return result.getInt(1);
	}

	protected void fillPurchasedWaresTable(ArrayList<String> purchasedName, ArrayList<String> purchasedPrice, ArrayList<String> purchasedQuantity) {
		for (int i = 0; i < purchasedName.size(); i++) {
			((DefaultTableModel) purchasedTable.getModel()).addRow(new Object[]{purchasedName.get(i), purchasedPrice.get(i), purchasedQuantity.get(i)});
		}
	}

	private JTable getCustomerTable(ArrayList<String> firstNames, ArrayList<String> lastNames, ArrayList<String> dates) {
		String columnNames[] = new String[]{"First name", "Last name", "Last visit"};
		Object[][] data = new Object[firstNames.size()][columnNames.length];
		for (int i = 0; i < firstNames.size(); i++) {
			data[i][0] = firstNames.get(i);
			data[i][1] = lastNames.get(i);
			data[i][2] = dates.get(i);
		}
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		return new JTable(model);
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

	public CashierPanel(Connection connection) {
		super(new GridLayout(4, 0));
		this.connection = connection;
		try {
			cashierId = getRandomCashier();
			String cashierFirstName = getStoreEmployeeFirstName(cashierId);
			String cashierLastName = getStoreEmployeeLastName(cashierId);
			String store = getStoreByEmployeeId(cashierId);
			int level = getMallLevel(cashierId);
	                String area = getMallArea(cashierId);

			add(getCashierInfo(cashierFirstName, cashierLastName, store, level, area));
			ResultSet customers = getCustomersByCashierId(cashierId);

			int i = 0;
			while (customers.next()) {
				customerIds.add(customers.getInt(1));
				customerFirstNames.add(getCustomerFirstNameById(customers.getInt(1)));
				customerLastNames.add(getCustomerLastNameById(customers.getInt(1)));
				dates.add(getCustomerDateById(customers.getInt(1)));
				purchaseNumber.add(customers.getInt(2));
				purchaseIds.add(customers.getInt(3));
				purchaseDate.add(String.valueOf(customers.getDate(4)));

				ResultSet purchased = getWaresByPurchaseId(customers.getInt(3));
				purchasedName.add(new ArrayList());
				purchasedPrice.add(new ArrayList());
				purchasedQuantity.add(new ArrayList());
				while (purchased.next()) {
					((ArrayList) purchasedName.get(i)).add(purchased.getString(1));
					((ArrayList) purchasedPrice.get(i)).add(purchased.getString(2));
					((ArrayList) purchasedQuantity.get(i)).add(String.valueOf(purchased.getInt(3)));
				}
				i++;
			}
			customerTable = getCustomerTable(customerFirstNames, customerLastNames, dates);
			JPanel panel1 = new JPanel(new GridLayout(1, 0));
			panel1.setBorder(BorderFactory.createTitledBorder("Customers"));
			panel1.add(new JScrollPane(customerTable));
			add(panel1);
			customerTable.addKeyListener(new CustomerTableKeyListener(this));
			customerTable.addMouseListener(new CustomerTableMouseListener(this));
			JPanel panel = new JPanel();
			JButton bcc = new JButton("Add new customer...");
			bcc.addActionListener(new AddButtonActionListener(this));
			panel.add(bcc);
			add(panel);

			JPanel purchaseInfo = new JPanel(new GridLayout(2, 0));
			numberLabel = new JLabel();
			dateLabel = new JLabel();
			purchaseInfo.add(numberLabel);
			purchaseInfo.add(dateLabel);

			String columnNames[] = new String[]{"Name", "Price", "Quantity"};
			DefaultTableModel model = new DefaultTableModel(null, columnNames);
			purchasedTable = new JTable(model);
			JPanel purchasePanel = new JPanel(new GridLayout(1, 0));
			updatePurchaseInfo(purchaseNumber, purchaseDate);
			fillPurchasedWaresTable((ArrayList) purchasedName.get(0), (ArrayList) purchasedPrice.get(0), (ArrayList) purchasedQuantity.get(0));
			purchasePanel.add(new JScrollPane(purchasedTable));
			purchasedTable.addKeyListener(new PurchaseTableKeyListener(this));
			
			purchasePanel.setBorder(purchaseBorder);
			add(purchasePanel);

			JPanel panel2 = new JPanel();
			JButton bcc2 = new JButton("Add new purchase...");
			panel2.add(bcc2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}