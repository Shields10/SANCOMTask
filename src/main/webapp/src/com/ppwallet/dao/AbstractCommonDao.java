package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.SQLException;

public abstract interface AbstractCommonDao {
	public Connection getConnection() throws SQLException;
	public void commit() throws SQLException;
	public void close() throws SQLException;
	public void rollback() throws SQLException;
}
