package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.web.HttpServletJXGB;


public abstract class HandleConnections extends HttpServletJXGB implements AbstractCommonDao {
	private static final long serialVersionUID = 1L;
	private Connection conn=null;
	private String className = HandleConnections.class.getSimpleName();

	@Override
	public synchronized Connection getConnection() throws SQLException {
		   try{
			   this.conn = MySqlPoolableObjectFactory.getConnection();			  
		   }catch(Exception e){
			   PPWalletEnvironment.setComment(1,className," Exception in  getConnection() -->" +e.getMessage());
		   	}
		return this.conn;	}

	@Override
	public void commit() throws SQLException {
		try{
			this.conn.commit();
		}catch(Exception e){
			throw new SQLException("HandleConnections :commit" +e.getMessage());
		}
	}

	@Override
	public void close() throws SQLException {
		try{
			this.conn.close();
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className," error in close()  -->" +e.getMessage());
				throw new SQLException(" Class handleDataBase:Method close:"+e.getMessage());	
			}
		}
	
	@Override
	public void rollback() throws SQLException {
		try{
			this.conn.rollback();
		}catch(Exception e){
			throw new SQLException("handleDataBase :rollback"+ e.getMessage());
		}
		}

}
