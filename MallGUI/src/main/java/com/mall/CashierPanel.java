package com.mall;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.*;

public class CashierPanel extends JPanel {

	private final Connection connection;
	private int cashierId;
	private JTable customerTable, purchasedTable;
	private JLabel numberLabel, dateLabel;
	private TitledBorder purchaseBorder = BorderFactory.createTitledBorder("123");
	private Vector<Integer> customerIds = new Vector<Integer>(), purchaseIds = new Vector<Integer>();
	private Vector<String> customerFirstNames = new Vector<String>(), customerLastNames = new Vector<String>(), dates = new Vector<String>();
	private Vector purchasedName = new Vector(), purchasedPrice = new Vector(), purchasedQuantity = new Vector(), purchaseNumber = new Vector(), purchaseDate = new Vector();

	private void deleteCustomerFromBase() throws SQLException {
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

	private void showInsertFrame() {
		final JFrame insertFrame = new JFrame();
		//insertFrame.setSize(400, 300);
		insertFrame.setTitle("New customer");

		//.setLayout(new GridLayout(2, 0));
		insertFrame.setLayout(new BorderLayout());

		//Create a regular text field.
		final JTextField firstNameField = new JTextField(20);
		firstNameField.setActionCommand("First name:");
		//textField.addActionListener(this);

		//Create a password field.
		final JTextField secondNameField = new JTextField(20);
		secondNameField.setActionCommand("Second name: ");
		//secondNameField.addActionListener(this);

		//Create a formatted text field.
		final JFormattedTextField ftf = new JFormattedTextField(
			"2016-01-01");
		ftf.setActionCommand("test");
		//ftf.addActionListener(this);

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
			next.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						PreparedStatement statement = connection.prepareStatement("insert into customer(first_name, last_name, last_visit) values(?, ?, ?);");
						statement.setString(1, firstNameField.getText());
						statement.setString(2, secondNameField.getText());
						statement.setDate(3, java.sql.Date.valueOf(ftf.getText()));
						statement.executeUpdate();
						customerFirstNames.add(firstNameField.getText());
						customerLastNames.add(secondNameField.getText());
						dates.add(ftf.getText());
						((DefaultTableModel) customerTable.getModel()).addRow(new Object[]{firstNameField.getText(), secondNameField.getText(), ftf.getText()});
						Vector name = new Vector();
						Vector price = new Vector();
						Vector quantity = new Vector();
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
						st.setInt(4, customerFirstNames.size());
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
						purchasedName.add(name);
						purchasedPrice.add(price);
						purchasedQuantity.add(quantity);
						purchaseNumber.add("12345");
						purchaseDate.add("2016-01-01");
						insertFrame.setVisible(false);
						JOptionPane.showMessageDialog(null, "Customer added successfully");
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			});
		JButton cancel = new JButton("Cancel");
		textControlsPane.add(cancel);
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					insertFrame.setVisible(false);
				}
			});
		textControlsPane.setBorder(
			BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Personal"),
			BorderFactory.createEmptyBorder(5,5,5,5)));

		insertFrame.add(textControlsPane);
		insertFrame.pack();
		insertFrame.setVisible(true);
	}

	private void deleteRows(JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowCount = model.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}

	private void deleteItem() {
		//connection.preparedStatement("delete from purchased_wares where purchased_wares_id = " + customerTable.get 
		((Vector) (purchasedName.get(customerTable.getSelectedRow()))).remove(purchasedTable.getSelectedRow());
		((Vector) (purchasedPrice.get(customerTable.getSelectedRow()))).remove(purchasedTable.getSelectedRow());
		((Vector) (purchasedQuantity.get(customerTable.getSelectedRow()))).remove(purchasedTable.getSelectedRow());
		purchaseNumber.remove(purchasedTable.getSelectedRow());
		purchaseDate.remove(purchasedTable.getSelectedRow());
		((DefaultTableModel) purchasedTable.getModel()).removeRow(purchasedTable.getSelectedRow());
		JOptionPane.showMessageDialog(null, "Item deleted");
	}

	private void updatePurchaseInfo(Vector purchaseNumber, Vector purchaseDate) {
		int selected = customerTable.getSelectedRow();
		if (selected == -1) {
			selected = 0;
		}
		purchaseBorder.setTitle("Purchase number " + purchaseNumber.get(selected) + "        " + purchaseDate.get(selected));
	}

	private void addPurchaseTable() {
		
	}

	private void addMouseListener(JTable table, MouseListener listener) {
		table.addMouseListener(listener);
	}

	private void addKeyListener(JTable table, KeyListener listener) {
		table.addKeyListener(listener);
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

	private void fillPurchasedWaresTable(Vector<String> purchasedName, Vector<String> purchasedPrice, Vector<String> purchasedQuantity) {
		for (int i = 0; i < purchasedName.size(); i++) {
			((DefaultTableModel) purchasedTable.getModel()).addRow(new Object[]{purchasedName.get(i), purchasedPrice.get(i), purchasedQuantity.get(i)});
		}
	}

	private JTable getCustomerTable(Vector<String> firstNames, Vector<String> lastNames, Vector<String> dates) {
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

	private void addLabelTextRows(JLabel[] labels,
                                  JTextField[] textFields,
                                  GridBagLayout gridbag,
                                  Container container) {
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
                	//"select customer_id, number, purchase_id, date from purchase where cashier_id = ?;"
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
				purchasedName.add(new Vector());
				purchasedPrice.add(new Vector());
				purchasedQuantity.add(new Vector());
				//String str = (String) ((Vector) purchasedName.get(0)).get(0);
				//System.out.println(str);
				while (purchased.next()) {
					((Vector) purchasedName.get(i)).add(purchased.getString(1));
					((Vector) purchasedPrice.get(i)).add(purchased.getString(2));
					((Vector) purchasedQuantity.get(i)).add(String.valueOf(purchased.getInt(3)));
				}
				i++;
			}
			customerTable = getCustomerTable(customerFirstNames, customerLastNames, dates);
			JPanel panel1 = new JPanel(new GridLayout(1, 0));
			panel1.setBorder(BorderFactory.createTitledBorder("Customers"));
			panel1.add(new JScrollPane(customerTable));
			add(panel1);
			customerTable.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
					switch (e.getKeyCode()) {
						case 127:
							int n = JOptionPane.showConfirmDialog(
								null,
								"Are you sure you want to delete this customer?",
								"Confirm delete",
								JOptionPane.YES_NO_OPTION);
							if (n == 0) {
								try {
									deleteCustomerFromBase();
								} catch (SQLException ex) {
									ex.printStackTrace();
								}
								customerFirstNames.remove(customerTable.getSelectedRow());
								customerLastNames.remove(customerTable.getSelectedRow());
								dates.remove(customerTable.getSelectedRow());
								purchasedName.remove(customerTable.getSelectedRow());
								purchasedPrice.remove(customerTable.getSelectedRow());
								purchasedQuantity.remove(customerTable.getSelectedRow());
								purchaseNumber.remove(customerTable.getSelectedRow());
								purchaseDate.remove(customerTable.getSelectedRow());
								((DefaultTableModel) customerTable.getModel()).removeRow(customerTable.getSelectedRow());
								JOptionPane.showMessageDialog(null, "Customer deleted");
								deleteRows(purchasedTable);
								fillPurchasedWaresTable((Vector) purchasedName.get(0), (Vector) purchasedPrice.get(0), (Vector) purchasedQuantity.get(0));
							}
							break;
						case 38:
						case 40:
							int selected = customerTable.getSelectedRow();
							deleteRows(purchasedTable);
							updatePurchaseInfo(purchaseNumber, purchaseDate);
							fillPurchasedWaresTable((Vector) purchasedName.get(selected), (Vector) purchasedPrice.get(selected), (Vector) purchasedQuantity.get(selected));
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyTyped(KeyEvent e) {
				}
			});
			customerTable.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					int selected = customerTable.getSelectedRow();
					if (selected == -1) {
						selected = 0;
					}
					DefaultTableModel model = (DefaultTableModel) purchasedTable.getModel();
					int rowCount = model.getRowCount();
					for (int i = rowCount - 1; i >= 0; i--) {
						model.removeRow(i);
					}
					updatePurchaseInfo(purchaseNumber, purchaseDate);
					fillPurchasedWaresTable((Vector) purchasedName.get(selected), (Vector) purchasedPrice.get(selected), (Vector) purchasedQuantity.get(selected));
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}
			});
			JPanel panel = new JPanel();
			JButton bcc = new JButton("Add new customer...");
			bcc.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showInsertFrame();
				}
			});
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
			fillPurchasedWaresTable((Vector) purchasedName.get(0), (Vector) purchasedPrice.get(0), (Vector) purchasedQuantity.get(0));
			purchasePanel.add(new JScrollPane(purchasedTable));
			addKeyListener(purchasedTable, new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case 127:
						int n = JOptionPane.showConfirmDialog(
							null,
							"Are you sure you want to delete this tem from purchase?",
							"Confirm delete",
							JOptionPane.YES_NO_OPTION);
						if (n == 0) {
							deleteItem();
						}
						break;
					case 40:
						break;
					case 38:
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
			
			purchasePanel.setBorder(purchaseBorder);
			add(purchasePanel);
			//addPurchaseTable();

			JPanel panel2 = new JPanel();
			JButton bcc2 = new JButton("Add new purchase...");
			panel2.add(bcc2);
			//add(panel2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
