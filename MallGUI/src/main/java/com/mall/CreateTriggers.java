package com.mall;

import java.sql.*;

public class CreateTriggers {

	private static Connection connection;

	public static Connection getConnection() {
                try {
                        Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                }

                Connection connection = null;
                try {
                        connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/mall", "postgres", "postgres");
                } catch (SQLException ex) {
                        ex.printStackTrace();
                }
                return connection;
        }

	private static void createPurchaseTableTrigger() throws SQLException {
		connection.prepareStatement("create or replace function create_purchase_timestamp() returns void as $$"
						+ " begin"
						+ " if exists (select relname from pg_class where relname='purchase_timestamp')"
							+ " then drop table purchase_timestamp;"
						+ " end if;"
						+ " create table purchase_timestamp(timestamp_id serial primary key, stamp timestamp, operation varchar(10), username text, number integer, date date, payment_type_id integer, customer_id integer, cashier_id integer);"
						+ " end"
					+ "$$ language plpgsql;"
					+ "select create_purchase_timestamp();"
					+ "create or replace function process_purchase_timestamp() returns trigger as $purchase_stamp$"
						+ " begin"
							+ " if tg_op = 'INSERT' then"
								+ " insert into purchase_timestamp(stamp, operation, username, number, date, payment_type_id, customer_id, cashier_id) values(now(), tg_op, user, new.number, new.date, new.payment_type, new.customer_id, new.cashier_id);"
							+ " else "
								+ " insert into purchase_timestamp(stamp, operation, username, number, date, payment_type_id, customer_id, cashier_id) values(now(), tg_op, user, old.number, old.date, old.payment_type, old.customer_id, old.cashier_id);"
							+ " end if;"
							+ " return null;"
						+ " end"
					+ "$purchase_stamp$ language plpgsql;"
					+ "create or replace function append_trigger() returns void as $$"
						+ "begin"
							+ " if exists (select tgname from pg_trigger where not tgisinternal and tgname = 'purchase_timestamp')"
								+ " then drop trigger purchase_timestamp on purchase;"
							+ " end if;"
							+ "create trigger purchase_timestamp after insert or update or delete on purchase for each row execute procedure process_purchase_timestamp();"
						+ "end"
					+ "$$ language plpgsql;"
					+ "select append_trigger();").executeUpdate();
	}

	private static void createCustomerTableTrigger() throws SQLException {
		connection.prepareStatement("create or replace function create_customer_timestamp() returns void as $$"
						+ " begin"
						+ " if exists (select relname from pg_class where relname='customer_timestamp')"
							+ " then drop table customer_timestamp;"
						+ " end if;"
						+ " create table customer_timestamp(timestamp_id serial primary key, stamp timestamp, operation varchar(10), username text, first_name varchar(100), last_name varchar(100), last_visit date);"
						+ " end"
					+ "$$ language plpgsql;"
					+ "select create_customer_timestamp();"
					+ "create or replace function process_customer_timestamp() returns trigger as $customer_stamp$"
						+ " begin"
							+ " if tg_op = 'INSERT' then"
								+ " insert into customer_timestamp(stamp, operation, username, first_name, last_name, last_visit) values(now(), tg_op, user, new.first_name, new.last_name, new.last_visit);"
							+ " else "
								+ " insert into customer_timestamp(stamp, operation, username, first_name, last_name, last_visit) values(now(), tg_op, user, old.first_name, old.last_name, old.last_visit);"
							+ " end if;"
							+ " return null;"
						+ " end"
					+ "$customer_stamp$ language plpgsql;"
					+ "create or replace function append_trigger() returns void as $$"
						+ "begin"
							+ " if exists (select tgname from pg_trigger where not tgisinternal and tgname = 'customer_timestamp')"
								+ " then drop trigger customer_timestamp on customer;"
							+ " end if;"
							+ "create trigger customer_timestamp after insert or update or delete on customer for each row execute procedure process_customer_timestamp();"
						+ "end"
					+ "$$ language plpgsql;"
					+ "select append_trigger();").executeUpdate();
	}

	public static void main(String[] args) {
		connection = getConnection();
		try {
			createCustomerTableTrigger();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		try {
			createPurchaseTableTrigger();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
