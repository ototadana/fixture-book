/*
 * Copyright 2013 XPFriend Community.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xpfriend.fixture.cast.temp;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.Loggi;

/**
 * データベースコネクション。
 * 
 * @author Ototadana
 */
class DatabaseConnection {
	private static final String DEFAULT = "db";
	
	private static Map<String, BasicDataSource> dataSourceMap = new HashMap<String, BasicDataSource>();
	private Map<String, InternalDatabaseConnection> connections = new HashMap<String, InternalDatabaseConnection>();
	private InternalDatabaseConnection currentConnection;

	private InternalDatabaseConnection getInstance() {
		if(currentConnection == null) {
			currentConnection = getConnection(DEFAULT);
		}
		return currentConnection;
	}
	
	public void use(String databaseName) {
		if(databaseName == null) {
			databaseName = DEFAULT;
		}
		currentConnection = getConnection(databaseName);
	}

	private InternalDatabaseConnection getConnection(String databaseName) {
		InternalDatabaseConnection connection = connections.get(databaseName);
		if(connection != null) {
			return connection;
		}
		connection = new InternalDatabaseConnection(databaseName);
		connections.put(databaseName, connection);
		return connection;
	}
	
	public boolean isSQLServer() {
		return getInstance().isSQLServer;
	}
	
	public void commit() {
		for(InternalDatabaseConnection connection : connections.values()) {
			connection.commit();
		}
	}
	
	public void dispose() {
		for(InternalDatabaseConnection connection : connections.values()) {
			connection.dispose();
		}
	}
	
	public PreparedStatement prepareStatement(String sql) {
		return getInstance().prepareStatement(sql);
	}
	
	public Statement createStatement() {
		return getInstance().createStatement();
	}

	private class InternalDatabaseConnection {
		private Connection connection;
		
		boolean isSQLServer;
		
		InternalDatabaseConnection(String databaseName) {
			BasicDataSource dataSource = getDataSource(databaseName);
			isSQLServer = dataSource.getUrl().startsWith("jdbc:sqlserver://");
			connect(dataSource);
		}
		
		private BasicDataSource getDataSource(String databaseName) {
			BasicDataSource dataSource = dataSourceMap.get(databaseName);
			if(dataSource == null) {
				dataSource = createDataSource(databaseName);
				dataSourceMap.put(databaseName, dataSource);
			}
			return dataSource;
		}

		private BasicDataSource createDataSource(String databaseName) {
			Properties properties = new Properties();
			InputStream is = ClassLoader.getSystemResourceAsStream(databaseName + ".properties");
			if(is == null) {
				throw new ConfigException("M_Fixture_Temp_DatabaseConnection_NoSuchName", databaseName);
			}

			try {
				try {
					properties.load(is);
				} finally {
					is.close();
				}

				String url = properties.getProperty("url");
				String user = properties.getProperty("username");
				String password = properties.getProperty("password");
				Loggi.debug("DatabaseName: " + databaseName +
						", driverClassName: " + properties.getProperty("driverClassName") + 
						", url=" + url + 
						", username=" + user + 
						", password=" + password);

				return (BasicDataSource)BasicDataSourceFactory.createDataSource(properties);
			} catch(Exception e) {
				throw new ConfigException(e);
			}
		}

		private void connect(DataSource dataSource) {
			try {
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
			} catch(Exception e) {
				throw new ConfigException(e);
			}
		}
		
		void commit() {
			try {
				connection.commit();
				Loggi.debug("commit");
			} catch(SQLException e) {
				throw new ConfigException(e);
			}
		}
		
		void dispose() {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					Loggi.warn(e);
				} finally {
					connection = null;
				}
			}
		}
		
		PreparedStatement prepareStatement(String sql) {
			try {
				return connection.prepareStatement(sql);
			} catch(SQLException e) {
				throw new ConfigException(e);
			}
		}

		Statement createStatement() {
			try {
				return connection.createStatement();
			} catch(SQLException e) {
				throw new ConfigException(e);
			}
		}
	}
}
