package com.mall;

import java.awt.GridLayout;

import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MallFrame extends JFrame {

	private JTabbedPane initTaskPane(Connection connection) {
		CashierPanel cashier = new CashierPanel(connection);
		StoreAccountantPanel storeAccountant = new StoreAccountantPanel(connection);
		StoreOwnerPanel storeOwner = new StoreOwnerPanel(connection);

		JTabbedPane taskPane = new JTabbedPane();
		taskPane.addTab("Cashier", cashier);
		taskPane.addTab("Store accountant", storeAccountant);
		taskPane.addTab("Store owner", storeOwner);
		taskPane.addTab("Mall accountant", null);
		taskPane.addTab("Mall owner", null);
		taskPane.addTab("Tax agency", null);
		return taskPane;
	}

	public MallFrame(Connection connection) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel(new GridLayout(1, 0));
		contentPane.add(initTaskPane(connection));
		contentPane.setOpaque(true);
		setContentPane(contentPane);

		setSize(800, 600);
		setVisible(true);
	}
}