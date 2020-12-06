package com.ppwallet.dao;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.MccGroup;
import com.ppwallet.model.Merchant;

public class MerchMccGroupDao extends HandleConnections {
	
	private static final long serialVersionUID = 2L;
	private static String className = MerchMccGroupDao.class.getSimpleName();
	
	public ArrayList<MccGroup>  getMccGroups() throws Exception  {
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;	
		ArrayList<MccGroup> arrMccGroupArraylist = null;
		
		try {
			connection = super.getConnection();	
			
			query = "select mcccategoryid, mcccategoryname from merch_mcc_group";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();
			
			if(rs!=null){
				
				arrMccGroupArraylist = new ArrayList<MccGroup>();
				while(rs.next()){
					MccGroup myMccGroup = new MccGroup();
					myMccGroup.setMccCategoryId( rs.getString(StringUtils.trim("mcccategoryid"))   );
					myMccGroup.setMccCategoryName( rs.getString(StringUtils.trim("mcccategoryname"))   );
					arrMccGroupArraylist.add(myMccGroup);
				}//whileloop
			}//rs
			if(arrMccGroupArraylist!=null)
				  if(arrMccGroupArraylist.size()==0)
					  arrMccGroupArraylist=null;
			
		}catch(Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getMerchDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchantDetails  is  "+e.getMessage());		
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
				//if(myMccGroup!= null) myMccGroup = null;
				
			}

		return arrMccGroupArraylist;
		
	}


}
	
	
