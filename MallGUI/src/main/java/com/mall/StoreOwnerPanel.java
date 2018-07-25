package com.mall;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

public class StoreOwnerPanel extends JPanel {

	private final Connection connection;
	private DefaultTableModel storeModel = null;
	private DefaultTableModel employeeModel = null;
	private JTable storeTable, employeeTable;
	private Vector employeeIds = new Vector(), employeeFirstNames = new Vector(), employeeLastNames = new Vector(), employeeExperience = new Vector(), employeeJobs = new Vector(), employeeSalary = new Vector();
	private Vector<String> firstNames, lastNames;
	private Vector<Integer> storeIds = new Vector<Integer>();

	private void deleteStoreFromBase() throws SQLException {
		ResultSet result = connection.prepareStatement("select store_employee_id from store_employee where store_id = " + storeIds.get(storeTable.getSelectedRow()) + ";").executeQuery();
		while (result.next()) {
			connection.prepareStatement("delete from cashier_entry where store_employee_id = " + result.getInt(1) + ";").executeUpdate();
			connection.prepareStatement("delete from store_accountant_entry where store_employee_id = " + result.getInt(1) + ";").executeUpdate();
		}
		PreparedStatement statement = connection.prepareStatement("delete from store where store_id = ?;");
		PreparedStatement statement2 = connection.prepareStatement("delete from store_employee where store_id = ?;");
		statement2.setInt(1, storeIds.get(storeTable.getSelectedRow()));
		statement.setInt(1, storeIds.get(storeTable.getSelectedRow()));
		//statement.executeUpdate();
	}

	private Vector<String> getVector(String filePath) {
		Vector<String> v = new Vector<String>();
                try {
                        FileInputStream fileInput = new FileInputStream(filePath);
                        DataInputStream dataInput = new DataInputStream(fileInput);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(dataInput));
                        String inputLine;
                        while ((inputLine = reader.readLine()) != null) {
                                v.add(inputLine);
                        }
                        dataInput.close();
                } catch (IOException ex) {
                        ex.printStackTrace();
                }
		return v;
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

	private void showInsertFrame() {
		final JFrame insertFrame = new JFrame();
		//insertFrame.setSize(400, 300);
		insertFrame.setTitle("New store");

		//.setLayout(new GridLayout(2, 0));
		insertFrame.setLayout(new BorderLayout());

		//Create a regular text field.
		final JTextField storeName = new JTextField(20);
		storeName.setActionCommand("Store name:");
		//textField.addActionListener(this);

		//Create a password field.
		final JTextField lastName = new JTextField(20);
		lastName.setActionCommand("Mall level: ");
		//passwordField.addActionListener(this);

		//Create a formatted text field.
		final JFormattedTextField ftf = new JFormattedTextField(
			"");
		ftf.setActionCommand("test");
		//ftf.addActionListener(this);

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
			next.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String job[] = new String[]{"Accountant", "Analytics", "Big Data", "Cashier", "Cloud Computing", "Database Programming", "Internet Security", "Network Administration"};
					storeModel.addRow(new Object[]{storeName.getText(), 0});
					//private Vector employeeIds = new Vector(), employeeFirstNames = new Vector(), employeeLastNames = new Vector(), employeeExperience = new Vector(), employeeJobs = new Vector(), employeeSalary = new Vector();
					Vector ids = new Vector();
					Vector fNames = new Vector();
					Vector lNames = new Vector();
					Vector experience = new Vector();
					Vector jobs = new Vector();
					Vector salary = new Vector();
					Random random = new Random();
					for (int i = 0; i < random.nextInt(10) + 10; i++) {
						ids.add("123");
						fNames.add(firstNames.get(random.nextInt(firstNames.size())));
						lNames.add(lastNames.get(random.nextInt(lastNames.size())));
						experience.add(String.valueOf(random.nextInt(10)));
						jobs.add(job[random.nextInt(job.length)]);
						salary.add(String.valueOf(random.nextInt(10) * 250 + 1000));
					}
					employeeIds.add(ids);
					employeeFirstNames.add(fNames);
					employeeLastNames.add(lNames);
					employeeExperience.add(experience);
					employeeJobs.add(jobs);
					employeeSalary.add(salary);
					//((DefaultTableModel) customerTable.getModel()).addRow(new Object[]{firstNameField.getText(), secondNameField.getText(), ftf.getText()});
					insertFrame.setVisible(false);
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
			BorderFactory.createTitledBorder("Info"),
			BorderFactory.createEmptyBorder(5,5,5,5)));

		insertFrame.add(textControlsPane);
		insertFrame.pack();
		insertFrame.setVisible(true);
	}

	private ResultSet getJobById(int jobId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name, salary from job where job_id = ?;");
		statement.setInt(1, jobId);
		return statement.executeQuery(); 
	}

	private void deleteRows(JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowCount = model.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}

	private void fillEmployeeTable(Vector employeeFirstNames, Vector employeeLastNames, Vector employeeExperience, Vector employeeJobs, Vector employeeSalary) {
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
			firstNames = getVector("/home/dbprojects/Documents/MallGUI/firstNames");
			lastNames = getVector("/home/dbprojects/Documents/MallGUI/lastNames");
			ResultSet owner = getRandomStoreOwner();
                        owner.next();
                        int ownerId = owner.getInt(1);
                        ResultSet store = getStoresByOwner(ownerId);

			String columnNames[] = new String[]{"Store", "Year revenue"};
			storeModel = new DefaultTableModel(null, columnNames);
			JPanel storeTablePanel = new JPanel(new GridLayout(1, 0));
			storeTable = new JTable(storeModel);
			storeTable.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
					switch (e.getKeyCode()) {
						case 127:
							int n = JOptionPane.showConfirmDialog(
								null,
								"Are you sure you want to delete this store?",
								"Confirm delete",
								JOptionPane.YES_NO_OPTION);
							if (n == 0) {
								int selected = storeTable.getSelectedRow();
								try {
									deleteStoreFromBase();
								} catch (SQLException ex) {
									ex.printStackTrace();
								}
								//private Vector employeeIds = new Vector(), employeeFirstNames = new Vector(), employeeLastNames = new Vector(), employeeExperience = new Vector(), employeeJobs = new Vector(), employeeSalary = new Vector();
								employeeIds.remove(selected);
								employeeFirstNames.remove(selected);
								employeeLastNames.remove(selected);
								employeeExperience.remove(selected);
								employeeJobs.remove(selected);
								employeeSalary.remove(selected);
								((DefaultTableModel) storeTable.getModel()).removeRow(storeTable.getSelectedRow());
								JOptionPane.showMessageDialog(null, "Store deleted");
								deleteRows(employeeTable);
								fillEmployeeTable((Vector) employeeFirstNames.get(0), (Vector) employeeLastNames.get(0), (Vector) employeeExperience.get(0), (Vector) employeeJobs.get(0), (Vector) employeeSalary.get(0));
							}
						case 40:
						case 38:
							deleteRows(employeeTable);
							//updatePurchaseInfo(purchaseNumber, purchaseDate);
							fillEmployeeTable((Vector) employeeFirstNames.get(0), (Vector) employeeLastNames.get(0), (Vector) employeeExperience.get(0), (Vector) employeeJobs.get(0), (Vector) employeeSalary.get(0));
							//fillPurchasedWaresTable((Vector) purchasedName.get(selected), (Vector) purchasedPrice.get(selected), (Vector) purchasedQuantity.get(selected));
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyTyped(KeyEvent e) {
				}
			});
			storeTable.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					int selected = storeTable.getSelectedRow();
					if (selected == -1) {
						selected = 0;
					}
					DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
					int rowCount = model.getRowCount();
					for (int i = rowCount - 1; i >= 0; i--) {
						model.removeRow(i);
					}
					//System.out.println("tet");
					fillEmployeeTable((Vector) employeeFirstNames.get(storeTable.getSelectedRow()), (Vector) employeeLastNames.get(storeTable.getSelectedRow()), (Vector) employeeExperience.get(storeTable.getSelectedRow()), (Vector) employeeJobs.get(storeTable.getSelectedRow()), (Vector) employeeSalary.get(storeTable.getSelectedRow()));
					//updatePurchaseInfo(purchaseNumber, purchaseDate);
					//fillPurchasedWaresTable((Vector) purchasedName.get(selected), (Vector) purchasedPrice.get(selected), (Vector) purchasedQuantity.get(selected));
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
			storeTablePanel.add(new JScrollPane(storeTable));
			storeTablePanel.setBorder(BorderFactory.createTitledBorder(
                                "Store owner " + owner.getString(2) + " " + owner.getString(3)));
			add(storeTablePanel);

			int i = 0, totalRevenue = 0;
                        while (store.next()) {
				storeIds.add(store.getInt(1));
                                ResultSet employees = getStoreEmployeeByStoreId(store.getInt(1));
				employeeIds.add(new Vector());
				employeeFirstNames.add(new Vector());
				employeeLastNames.add(new Vector());
				employeeExperience.add(new Vector());
				employeeJobs.add(new Vector());
				employeeSalary.add(new Vector());
                                while (employees.next()) {
					Vector v1 = (Vector) employeeIds.get(i);
					Vector v2 = (Vector) employeeFirstNames.get(i);
					Vector v3 = (Vector) employeeLastNames.get(i);
					Vector v4 = (Vector) employeeExperience.get(i);
					Vector v5 = (Vector) employeeJobs.get(i);
					Vector v6 = (Vector) employeeSalary.get(i);
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
			bcc.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showInsertFrame();
				}
			});
			panel.add(bcc);
			add(panel);

			JPanel revenuePanel = new JPanel(new GridLayout(1, 0));
			revenuePanel.add(new JLabel("Total revenue: $" + totalRevenue));
			add(revenuePanel);

			JPanel employeeTablePanel = new JPanel(new GridLayout(1, 0));
			employeeTablePanel.setBorder(BorderFactory.createTitledBorder(
                                "Store employees"));
			employeeModel = new DefaultTableModel(null, new String[]{"First name", "Last name", "Experience", "Job", "Salary"});
			employeeTable = new JTable(employeeModel);
			employeeTable.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
					switch (e.getKeyCode()) {
						case 127:
							int n = JOptionPane.showConfirmDialog(
								null,
								"Are you sure you want to delete this employee from database?",
								"Confirm delete",
								JOptionPane.YES_NO_OPTION);
							if (n == 0) {
								//private Vector employeeIds = new Vector(), employeeFirstNames = new Vector(), employeeLastNames = new Vector(), employeeExperience = new Vector(), employeeJobs = new Vector(), employeeSalary = new Vector();
								((Vector) employeeIds.get(storeTable.getSelectedRow())).remove(employeeTable.getSelectedRow());
								((Vector) employeeFirstNames.get(storeTable.getSelectedRow())).remove(employeeTable.getSelectedRow());
								((Vector) employeeLastNames.get(storeTable.getSelectedRow())).remove(employeeTable.getSelectedRow());
								((Vector) employeeExperience.get(storeTable.getSelectedRow())).remove(employeeTable.getSelectedRow());
								((Vector) employeeJobs.get(storeTable.getSelectedRow())).remove(employeeTable.getSelectedRow());
								((Vector) employeeSalary.get(storeTable.getSelectedRow())).remove(employeeTable.getSelectedRow());
								((DefaultTableModel) employeeTable.getModel()).removeRow(employeeTable.getSelectedRow());
								JOptionPane.showMessageDialog(null, "Employee deleted");
							}
							break;
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyTyped(KeyEvent e) {
				}
			});
			fillEmployeeTable((Vector) employeeFirstNames.get(0), (Vector) employeeLastNames.get(0), (Vector) employeeExperience.get(0), (Vector) employeeJobs.get(0), (Vector) employeeSalary.get(0));
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
