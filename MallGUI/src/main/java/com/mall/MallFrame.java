package com.mall;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class MallFrame extends JFrame {

	private final Connection connection;
	private final JPanel contentPane = new JPanel(new GridLayout(1, 0));
	private final CashierPanel cashier;
	private final StoreAccountantPanel storeAccountant;
	private final StoreOwnerPanel storeOwner;

	private JTabbedPane initTaskPane() {
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
		this.connection = connection;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cashier = new CashierPanel(connection);
		storeAccountant = new StoreAccountantPanel(connection);
		storeOwner = new StoreOwnerPanel(connection);
		contentPane.add(initTaskPane());
		contentPane.setOpaque(true);
		setContentPane(contentPane);
		setSize(800, 600);
		setVisible(true);
	}
}
