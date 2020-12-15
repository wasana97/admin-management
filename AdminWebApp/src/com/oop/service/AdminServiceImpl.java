/**
 * OOP 2018
 * 
 * @author Udara Samaratunge  Department of Software Engineering, SLIIT 
 * 
 * @version 1.0
 * Copyright: SLIIT, All rights reserved
 * 
 */
package com.oop.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.oop.model.Admin;
import com.oop.util.CommonConstants;
import com.oop.util.CommonUtil;
import com.oop.util.DBConnectionUtil;
import com.oop.util.QueryUtil;

/**
 * Contract for the implementation of Admin Service .
 * 
 * @author Udara Samaratunge, SLIIT
 * @version 1.0
 */
public class AdminServiceImpl implements IAdmintService {
	

	/** Initialize logger */
	public static final Logger log = Logger.getLogger(AdminServiceImpl.class.getName());

	private static Connection connection;

	private static Statement statement;

	static{
		//create table or drop if exist
		createAdminTable();
	}

	private PreparedStatement preparedStatement;

	/**
	 * This method initially drop existing Admins table in the database and
	 * recreate table structure to insert admin entries
	 * 
	 * @throws SQLException
	 *             - Thrown when database access error occurs or this method is
	 *             called on a closed connection
	 * @throws ClassNotFoundException
	 *             - Thrown when an application tries to load in a class through
	 *             its string name using
	 * @throws SAXException
	 *             - Encapsulate a general SAX error or warning
	 * @throws IOException
	 *             - Exception produced by failed or interrupted I/O operations.
	 * @throws ParserConfigurationException
	 *             - Indicates a serious configuration error
	 * @throws NullPointerException
	 *             - Service is not available
	 * 
	 */
	public static void createAdminTable() {

		try {
			connection = DBConnectionUtil.getDBConnection();
			statement = connection.createStatement();
			// Drop table if already exists and as per SQL query available in
			// Query.xml
			statement.executeUpdate(QueryUtil.queryByID(CommonConstants.QUERY_ID_DROP_TABLE));
			// Create new admins table as per SQL query available in
			// Query.xml
			statement.executeUpdate(QueryUtil.queryByID(CommonConstants.QUERY_ID_CREATE_TABLE));

		} catch (SQLException | SAXException | IOException | ParserConfigurationException | ClassNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Add set of admins for as a batch from the selected admin List
	 * 
	 * @throws SQLException
	 *             - Thrown when database access error occurs or this method is
	 *             called on a closed connection
	 * @throws SAXException
	 *             - Encapsulate a general SAX error or warning
	 * @throws IOException
	 *             - Exception produced by failed or interrupted I/O operations.
	 * @throws ParserConfigurationException
	 *             - Indicates a serious configuration error.
	 * 
	 */
	@Override
	public void addAdmin(Admin admin) {

		String indexID = CommonUtil.generateIDs(getIndexIDs());
		
		try {
			connection = DBConnectionUtil.getDBConnection();
			/*
			 * Query is available in AdminQuery.xml file and use
			 * insert_admin key to extract value of it
			 */
			preparedStatement = connection
					.prepareStatement(QueryUtil.queryByID(CommonConstants.QUERY_ID_INSERT_ADMINS));
			connection.setAutoCommit(false);
			
			//Generate admin IDs
			admin.setIndexID(indexID);
			preparedStatement.setString(CommonConstants.COLUMN_INDEX_ONE, admin.getIndexID());
			preparedStatement.setString(CommonConstants.COLUMN_INDEX_TWO, admin.getCustermorID());
			preparedStatement.setString(CommonConstants.COLUMN_INDEX_THREE, admin.getCustemorName());
			preparedStatement.setString(CommonConstants.COLUMN_INDEX_FOUR, admin.getDriverName());
			preparedStatement.setString(CommonConstants.COLUMN_INDEX_FIVE, admin.getVehicleNumber());
			preparedStatement.setString(CommonConstants.COLUMN_INDEX_SIX, admin.getAmount());
			// Add admin
			preparedStatement.execute();
			connection.commit();

		} catch (SQLException | SAXException | IOException | ParserConfigurationException | ClassNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage());
		} finally {
			/*
			 * Close prepared statement and database connectivity at the end of
			 * transaction
			 */
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, e.getMessage());
			}
		}
	}

	/**
	 * Admin details can be retrieved based on the provided admin ID
	 * 
	 * @param indexID
	 *            - Admin details are filtered based on the ID
	 * 
	 * @see #actionOnAdmin()
	 */
	@Override
	public Admin getAdminByID(String indexID) {

		return actionOnAdmin(indexID).get(0);
	}
	
	/**
	 * Get all list of admins
	 * 
	 * @return ArrayList<Admin> 
	 * 						- Array of admin list will be return
	 * 
	 * @see #actionOnAdmin()
	 */
	@Override
	public ArrayList<Admin> getAdmins() {
		
		return actionOnAdmin(null);
	}

	/**
	 * This method delete an admin based on the provided ID
	 * 
	 * @param indexID
	 *            - Delete admin according to the filtered admin details
	 * @throws SQLException
	 *             - Thrown when database access error occurs or this method is
	 *             called on a closed connection
	 * @throws ClassNotFoundException
	 *             - Thrown when an application tries to load in a class through
	 *             its string name using
	 * @throws SAXException
	 *             - Encapsulate a general SAX error or warning
	 * @throws IOException
	 *             - Exception produced by failed or interrupted I/O operations.
	 * @throws ParserConfigurationException
	 *             - Indicates a serious configuration error.
	 * @throws NullPointerException
	 *             - Service is not available
	 */
	@Override
	public void removeAdmin(String indexID) {

		// Before deleting check whether admin ID is available
		if (indexID != null && !indexID.isEmpty()) {
			/*
			 * Remove admin query will be retrieved from AdminQuery.xml
			 */
			try {
				connection = DBConnectionUtil.getDBConnection();
				preparedStatement = connection
						.prepareStatement(QueryUtil.queryByID(CommonConstants.QUERY_ID_REMOVE_ADMIN));
				preparedStatement.setString(CommonConstants.COLUMN_INDEX_ONE, indexID);
				preparedStatement.executeUpdate();
			} catch (SQLException | SAXException | IOException | ParserConfigurationException
					| ClassNotFoundException e) {
				log.log(Level.SEVERE, e.getMessage());
			} finally {
				/*
				 * Close prepared statement and database connectivity at the end
				 * of transaction
				 */
				try {
					if (preparedStatement != null) {
						preparedStatement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					log.log(Level.SEVERE, e.getMessage());
				}
			}
		}
	}

	/**
	 * This performs GET admin by ID and Display all admins
	 * 
	 * @param indexID
	 *            ID of the admin to remove or select from the list

	 * @return ArrayList<Admin> Array of admin list will be return
	 * 
	 * @throws SQLException
	 *             - Thrown when database access error occurs or this method is
	 *             called on a closed connection
	 * @throws ClassNotFoundException
	 *             - Thrown when an application tries to load in a class through
	 *             its string name using
	 * @throws SAXException
	 *             - Encapsulate a general SAX error or warning
	 * @throws IOException
	 *             - Exception produced by failed or interrupted I/O operations.
	 * @throws ParserConfigurationException
	 *             - Indicates a serious configuration error.
	 * @throws NullPointerException
	 *             - Service is not available
	 * 
	 * @see #getAdmins()
	 * @see #getIndexByID(String)
	 */
	private ArrayList<Admin> actionOnAdmin(String indexID) {

		ArrayList<Admin> adminList = new ArrayList<Admin>();
		try {
			connection = DBConnectionUtil.getDBConnection();
			/*
			 * Before fetching admin it checks whether admin ID is
			 * available
			 */
			if (indexID != null && !indexID.isEmpty()) {
				/*
				 * Get admin by ID query will be retrieved from
				 * AdminQuery.xml
				 */
				preparedStatement = connection
						.prepareStatement(QueryUtil.queryByID(CommonConstants.QUERY_ID_GET_ADMIN));
				preparedStatement.setString(CommonConstants.COLUMN_INDEX_ONE, indexID);
			}
			/*
			 * If admin ID is not provided for get admin option it display
			 * all admins
			 */
			else {
				preparedStatement = connection
						.prepareStatement(QueryUtil.queryByID(CommonConstants.QUERY_ID_ALL_ADMINS));
			}
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Admin admin = new Admin();
				admin.setIndexID(resultSet.getString(CommonConstants.COLUMN_INDEX_ONE));
				admin.setCustermorID(resultSet.getString(CommonConstants.COLUMN_INDEX_TWO));
				admin.setCustemorName(resultSet.getString(CommonConstants.COLUMN_INDEX_THREE));
				admin.setDriverName(resultSet.getString(CommonConstants.COLUMN_INDEX_FOUR));
				admin.setVehicleNumber(resultSet.getString(CommonConstants.COLUMN_INDEX_FIVE));
				admin.setAmount(resultSet.getString(CommonConstants.COLUMN_INDEX_SIX));
				adminList.add(admin);
			}

		} catch (SQLException | SAXException | IOException | ParserConfigurationException | ClassNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage());
		} finally {
			/*
			 * Close prepared statement and database connectivity at the end of
			 * transaction
			 */
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, e.getMessage());
			}
		}
		return adminList;
	}

	/**
	 * Get the updated admin
	 * 
	 * @param indexID
	 *            ID of the admin to remove or select from the list
	 * 
	 * @return return the Admin object
	 * 
	 */
	@Override
	public Admin updateAdmin(String indexID, Admin admin) {

		/*
		 * Before fetching admin it checks whether admin ID is available
		 */
		if (indexID != null && !indexID.isEmpty()) {
			/*
			 * Update admin query will be retrieved from AdminQuery.xml
			 */
			try {
				connection = DBConnectionUtil.getDBConnection();
				preparedStatement = connection
						.prepareStatement(QueryUtil.queryByID(CommonConstants.QUERY_ID_UPDATE_ADMIN));
				preparedStatement.setString(CommonConstants.COLUMN_INDEX_ONE, admin.getCustermorID());
				preparedStatement.setString(CommonConstants.COLUMN_INDEX_TWO, admin.getCustemorName());
				preparedStatement.setString(CommonConstants.COLUMN_INDEX_THREE, admin.getDriverName());
				preparedStatement.setString(CommonConstants.COLUMN_INDEX_FOUR, admin.getVehicleNumber());
				preparedStatement.setString(CommonConstants.COLUMN_INDEX_FIVE, admin.getAmount());
				preparedStatement.setString(CommonConstants.COLUMN_INDEX_SIX, admin.getIndexID());
			
				preparedStatement.executeUpdate();

			} catch (SQLException | SAXException | IOException | ParserConfigurationException
					| ClassNotFoundException e) {
				log.log(Level.SEVERE, e.getMessage());
			} finally {
				/*
				 * Close prepared statement and database connectivity at the end
				 * of transaction
				 */
				try {
					if (preparedStatement != null) {
						preparedStatement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					log.log(Level.SEVERE, e.getMessage());
				}
			}
		}
		// Get the updated admin
		return getAdminByID(indexID);
	}
	
	/**
	 *
	 * @return ArrayList<String> Array of admin id list will be return
	 * 
	 * @throws SQLException
	 *             - Thrown when database access error occurs or this method is
	 *             called on a closed connection
	 * @throws ClassNotFoundException
	 *             - Thrown when an application tries to load in a class through
	 *             its string name using
	 * @throws SAXException
	 *             - Encapsulate a general SAX error or warning
	 * @throws IOException
	 *             - Exception produced by failed or interrupted I/O operations.
	 * @throws ParserConfigurationException
	 *             - Indicates a serious configuration error.
	 * @throws NullPointerException
	 *             - Service is not available
	 */
	private ArrayList<String> getIndexIDs(){
		
		ArrayList<String> arrayList = new ArrayList<String>();
		/*
		 * List of admin IDs will be retrieved from AdminQuery.xml
		 */
		try {
			connection = DBConnectionUtil.getDBConnection();
			preparedStatement = connection
					.prepareStatement(QueryUtil.queryByID(CommonConstants.QUERY_ID_GET_INDEX_IDS));
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				arrayList.add(resultSet.getString(CommonConstants.COLUMN_INDEX_ONE));
			}
		} catch (SQLException | SAXException | IOException | ParserConfigurationException
				| ClassNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage());
		} finally {
			/*
			 * Close prepared statement and database connectivity at the end of
			 * transaction
			 */
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, e.getMessage());
			}
		}
		return arrayList;
	}


}
