package com.mall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;

public class StoreOwnerNextButtonActionListener implements ActionListener {

	private final StoreOwnerPanel panel;
	private final JFrame insertFrame;
	private final JTextField storeName;
	private final DefaultTableModel storeModel;

	@Override
	public void actionPerformed(ActionEvent e) {
		String job[] = new String[]{"Accountant", "Analytics", "Big Data", "Cashier", "Cloud Computing", "Database Programming", "Internet Security", "Network Administration"};
		storeModel.addRow(new Object[]{storeName.getText(), 0});

		ArrayList ids = new ArrayList();
		ArrayList fNames = new ArrayList();
		ArrayList lNames = new ArrayList();
		ArrayList experience = new ArrayList();
		ArrayList jobs = new ArrayList();
		ArrayList salary = new ArrayList();
		Random random = new Random();
		for (int i = 0; i < random.nextInt(10) + 10; i++) {
			ids.add("123");
			fNames.add(panel.getFirstNames().get(random.nextInt(panel.getFirstNames().size())));
			lNames.add(panel.getLastNames().get(random.nextInt(panel.getLastNames().size())));
			experience.add(String.valueOf(random.nextInt(10)));
			jobs.add(job[random.nextInt(job.length)]);
			salary.add(String.valueOf(random.nextInt(10) * 250 + 1000));
		}
		panel.getEmployeeIds().add(ids);
		panel.getEmployeeFirstNames().add(fNames);
		panel.getEmployeeLastNames().add(lNames);
		panel.getEmployeeExperience().add(experience);
		panel.getEmployeeJobs().add(jobs);
		panel.getEmployeeSalary().add(salary);
		insertFrame.setVisible(false);
	}

	public StoreOwnerNextButtonActionListener(StoreOwnerPanel panel, JFrame insertFrame, JTextField storeName, DefaultTableModel storeModel) {
		this.panel = panel;
		this.insertFrame = insertFrame;
		this.storeName = storeName;
		this.storeModel = storeModel;
	}
}