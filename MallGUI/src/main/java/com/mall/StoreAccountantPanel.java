package com.mall;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

public class StoreAccountantPanel extends JPanel {

	private final Connection connection;
	private JTable purchasedTable;

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

	private void addPurchaseTable() {
		add(new JScrollPane(purchasedTable));
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
							JOptionPane.showMessageDialog(null, "Item deleted");
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

	private void fillPurchasedWaresTable(Vector<String> purchasedName, Vector<String> purchasedPrice, Vector<String> purchasedQuantity) {
		for (int i = 0; i < purchasedName.size(); i++) {
			((DefaultTableModel) purchasedTable.getModel()).addRow(new Object[]{purchasedName.get(i), purchasedPrice.get(i), purchasedQuantity.get(i)});
		}
	}

	private JTable getCashierTable(Vector<String> firstNames, Vector<String> lastNames, Vector<String> dates) {
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
			Vector<String> cashierFirstNames = new Vector<String>(), cashierLastNames = new Vector<String>(), revenues = new Vector<String>();
			final Vector purchasedName = new Vector(), purchasedPrice = new Vector(), purchasedQuantity = new Vector();
			String date = "", cashierFirstName = null, cashierLastName = null;
			int i = 0, revenue = 0, totalRevenue = 0;
			while (cashiers.next()) {
				int cashierId = getCashierByStoreEmployeeId(cashiers.getInt(1));
				if (cashierId != -1) {
					//System.out.println();
					//System.out.println("Cashier #" + i);
					cashierFirstName = getStoreEmployeeFirstName(cashierId);
					cashierLastName = getStoreEmployeeLastName(cashierId);
					//System.out.println(cashierFirstName + " " + cashierLastName);
					revenue = getTotalRevenue(cashierId);
					cashierFirstNames.add(cashierFirstName);
					cashierLastNames.add(cashierLastName);
					revenues.add(String.valueOf(revenue));
					//System.out.println("Year revenue: $" + revenue);
					totalRevenue += revenue;
					i++;
                                }

				/*ResultSet purchased = getWaresByPurchaseId(cashiers.getInt(3));
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
				*/
			}
			final JTable cashierTable = getCashierTable(cashierFirstNames, cashierLastNames, revenues);
			add(new JScrollPane(cashierTable));
			/*customerTable.addKeyListener(new KeyListener() {

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
								JOptionPane.showMessageDialog(null, "Customer deleted");
							}
							break;
						case 38:
						case 40:
							int selected = customerTable.getSelectedRow();
							DefaultTableModel model = (DefaultTableModel) purchasedTable.getModel();
							int rowCount = model.getRowCount();
							for (int i = rowCount - 1; i >= 0; i--) {
								model.removeRow(i);
							}
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
					DefaultTableModel model = (DefaultTableModel) purchasedTable.getModel();
					int rowCount = model.getRowCount();
					for (int i = rowCount - 1; i >= 0; i--) {
						model.removeRow(i);
					}
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
			*/
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
			/*String columnNames[] = new String[]{"Name", "Price", "Quantity"};
			DefaultTableModel model = new DefaultTableModel(null, columnNames);
			purchasedTable = new JTable(model);
			fillPurchasedWaresTable((Vector) purchasedName.get(0), (Vector) purchasedPrice.get(0), (Vector) purchasedQuantity.get(0));
			addPurchaseTable();
			*/
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
