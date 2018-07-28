package com.mall;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import javax.swing.table.DefaultTableModel;

public class EmployeeTableKeyListener implements KeyListener {

	private static StoreOwnerPanel panel;

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 127:
				int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this employee from database?", "Confirm delete", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					((ArrayList) panel.getEmployeeIds().get(panel.getStoreTable().getSelectedRow())).remove(panel.getEmployeeTable().getSelectedRow());
					((ArrayList) panel.getEmployeeFirstNames().get(panel.getStoreTable().getSelectedRow())).remove(panel.getEmployeeTable().getSelectedRow());
					((ArrayList) panel.getEmployeeLastNames().get(panel.getStoreTable().getSelectedRow())).remove(panel.getEmployeeTable().getSelectedRow());
					((ArrayList) panel.getEmployeeExperience().get(panel.getStoreTable().getSelectedRow())).remove(panel.getEmployeeTable().getSelectedRow());
					((ArrayList) panel.getEmployeeJobs().get(panel.getStoreTable().getSelectedRow())).remove(panel.getEmployeeTable().getSelectedRow());
					((ArrayList) panel.getEmployeeSalary().get(panel.getStoreTable().getSelectedRow())).remove(panel.getEmployeeTable().getSelectedRow());
					((DefaultTableModel) panel.getEmployeeTable().getModel()).removeRow(panel.getEmployeeTable().getSelectedRow());
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

	public EmployeeTableKeyListener(StoreOwnerPanel panel) {
		this.panel = panel;
	}
}