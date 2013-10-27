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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.ExceptionHandler;
import com.xpfriend.junk.Loggi;
import com.xpfriend.junk.Strings;

/**
 * データベース。
 * 
 * @author Ototadana
 */
class Database {
    DatabaseConnection connection = new DatabaseConnection();
    private Map<String, MetaData> metaDatas = new HashMap<String, MetaData>();
    
    private class DbResult {
		DynaClass dynaClass;
    	List<DynaBean> list;
    	
    	public DbResult(DynaClass dynaClass, List<DynaBean> list) {
    		this.dynaClass = dynaClass;
    		this.list = list;
		}
    }
    
    private class DbParameter {
    	Class<?> type;
    	Object value;
    	
    	public DbParameter(Class<?> type, Object value) {
    		this.type = type;
    		this.value = value;
		}
    	
		@Override
		public String toString() {
			if(value == null) {
				return "null";
			}
			if(value instanceof String) {
				return "\"" + value + "\"";
			}
			if(value instanceof Character) {
				return "'" + value + "'";
			}
			if(value instanceof BigDecimal) {
				return ((BigDecimal) value).toPlainString();
			}
			if(value instanceof Number) {
				return value.toString();
			}
			return value.toString() + "(" + type + ")";
		}
    }
    
    private class DbCommand {
    	Statement statement;
    	ResultSet resultSet;
    	
    	String sql;
    	List<DbParameter> parameters = new ArrayList<DbParameter>();

    	public DbCommand() {
    	}

    	public DbCommand(String sql) {
    		setSQL(sql, true);
    	}
    	
    	public DbCommand(String sql, boolean usePreparedStatement) {
    		setSQL(sql, usePreparedStatement);
    	}

    	public void setSQL(String sql, boolean usePreparedStatement) {
    		this.sql = sql;
    		if(usePreparedStatement) {
        		this.statement = connection.prepareStatement(sql);
    		} else {
    			this.statement = connection.createStatement();
    		}
    	}
    	
    	public void clearParameters() throws SQLException {
    		parameters.clear();
    		if(statement instanceof PreparedStatement) {
        		((PreparedStatement)statement).clearParameters();
    		}
    	}
    	
    	public void executeNonQuery() throws SQLException {
    		addParameters(parameters);
    		if(statement instanceof PreparedStatement) {
    			((PreparedStatement)statement).executeUpdate();
    		} else {
    			statement.execute(sql);
    		}
    	}
    	
		public ResultSet executeQuery() throws SQLException {
    		addParameters(parameters);
			return ((PreparedStatement)statement).executeQuery();
		}
    	
    	public void close() {
    		close(resultSet);
    		close(statement);
    		parameters.clear();
    	}
    	
    	private void close(Statement statement) {
    		if(statement != null) {
    			try {
    				statement.close();
    			} catch (SQLException e) {
    				ExceptionHandler.ignore(e);
    			}
    			statement = null;
    		}
    	}
    	
    	private void close(ResultSet resultSet) {
    		if(resultSet != null) {
    			try {
    				resultSet.close();
    			} catch (SQLException e) {
    				ExceptionHandler.ignore(e);
    			}
    			resultSet = null;
    		}
    	}

		public void printSQL() {
			if(!Loggi.isDebugEnabled()) {
				return;
			}
			
			StringBuilder sqlText = new StringBuilder();
			for(DbParameter parameter : parameters) {
				if(sqlText.length() == 0) {
					sqlText.append(sql).append(" - ");
				} else {
					sqlText.append(", ");
				}
				sqlText.append(parameter);
			}
			if(sqlText.length() == 0) {
				sqlText.append(sql);
			}
            Loggi.debug("SQL: " + sqlText);
		}

		public void addParameter(Class<?> columnType, Object columnValue) {
			parameters.add(new DbParameter(columnType, columnValue));
		}
		
		private void addParameters(List<DbParameter> parameters) throws SQLException {
			if(parameters.isEmpty()) {
				return;
			}
			
			ColumnValueConverter converter = ColumnValueConverter.getInstance();
			for(int i = 0; i < parameters.size(); i++) {
				DbParameter parameter = parameters.get(i);
				converter.setParameter((PreparedStatement)statement, i + 1, parameter.type, parameter.value);
			}
		}
    }
    
    private class MetaData {
    	DynaClass metaDataTable;
    	Boolean hasIdentityColumn;
    }

    public void use(String databaseName) {
    	connection.use(databaseName);
    }
    
    public void commit() {
    	connection.commit();
    }
    
    public void dispose() {
    	connection.dispose();
    }

	public void insert(List<TempDynaSet> dynaSet, Section tableInfos) {
		for(TempDynaSet table : dynaSet) {
			Table tableInfo = tableInfos.getTable(table.getName());
			use(table.getDatabaseName());
			insert(table, tableInfo);
		}
	}

    public void insert(TempDynaSet table, Table tableInfo) {
		setEnabledIdentityInsert(true, table.getTableName());
		DbCommand command = createInsertCommand(table, tableInfo);
    	try {
    		List<Row> rowInfo = tableInfo.getRows();
    		int rowIndex = 0;
    		for(DynaBean row : table.getRows()) {
    			insertRow(table, command, row, tableInfo, rowInfo.get(rowIndex++));
    		}
    	} finally {
    		setEnabledIdentityInsert(false, table.getTableName());
    		command.close();
    	}
    }

	private void setEnabledIdentityInsert(boolean enabled, String tableName) {
		if(connection.isSQLServer() && hasIdentityColumn(tableName)) {
            String onOff = enabled ? " ON" : " OFF";
			DbCommand command = new DbCommand("SET IDENTITY_INSERT " + tableName + onOff, false);
			try {
                executeNonQuery(command);
			} catch(Exception e) {
				Loggi.warn(e);
			} finally {
	    		command.close();
			}
		}
	}

	private boolean hasIdentityColumn(String tableName) {
		MetaData metaData = getMetaData(tableName);
		if(metaData.hasIdentityColumn == null) {
			metaData.hasIdentityColumn = hasIdentityColumnInternal(tableName);
		}
		return metaData.hasIdentityColumn;
	}

	private boolean hasIdentityColumnInternal(String tableName) {
		DbCommand command = createCommand("select * from " + tableName);
		try {
			ResultSet resultSet = command.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			for(int i = 1; i <= columnCount; i++) {
				if(metaData.isAutoIncrement(i)) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			throw new ConfigException(e);
		} finally {
			command.close();
		}
	}
	
	private void insertRow(TempDynaSet table, DbCommand command,
			DynaBean row, Table tableInfo, Row rowInfo) {
		try {
			command.clearParameters();
			for(DynaProperty property : table.getColumns()) {
				addParameter(command, property.getType(), row.get(property.getName()));
			}
			executeNonQuery(command);
		} catch(Exception e) {
			throw new ConfigException(e, "M_Fixture_Temp_Database_InsertRow", 
					table.getName(), tableInfo, rowInfo);
		}
	}

	private DbCommand createInsertCommand(TempDynaSet table, Table tableInfo) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into ").append(table.getTableName());
			sql.append(getColumnList(table.getColumns()));
			sql.append(getValueList(table.getColumns()));
			return createCommand(sql.toString());
		} catch(Exception e) {
			throw new ConfigException("M_Fixture_Temp_Database_CreateInsertCommand", 
					table.getName(), tableInfo);
		}
	}

	private StringBuilder getValueList(DynaProperty[] properties) {
        StringBuilder sql = new StringBuilder();
		for(int i = 0; i < properties.length; i++) {
			if(i == 0) {
				sql.append("values(");
			} else {
				sql.append(",");
			}
			sql.append("?");
		}
		sql.append(")");
		return sql;
	}

	private StringBuilder getColumnList(DynaProperty[] properties) {
        StringBuilder sql = new StringBuilder();
		for(int i = 0; i < properties.length; i++) {
			if(i == 0) {
				sql.append("(");
			} else {
				sql.append(",");
			}
			sql.append(properties[i].getName());
		}
		sql.append(")");
		return sql;
	}

	public void delete(List<TempDynaSet> dynaSet, Section tableInfos) {
		for(TempDynaSet table : dynaSet) {
	    	use(table.getDatabaseName());
			Table tableInfo = tableInfos.getTable(table.getName());
			delete(table, tableInfo);
		}
	}

	private void delete(TempDynaSet table, Table tableInfo) {
        String queryPrefix = "delete from " + table.getTableName() + " where ";
        List<String> columnNames = new ArrayList<String>();
        for(DynaProperty column : table.getColumns()) {
        	columnNames.add(column.getName());
        }

        List<Row> rowInfo = tableInfo.getRows();
		int rowIndex = 0;
		for(DynaBean row : table.getRows()) {
			delete(table, row, queryPrefix, columnNames, tableInfo, rowInfo.get(rowIndex++));
		}
	}
	
	private void delete(TempDynaSet table, DynaBean row, 
			String queryPrefix, List<String> columnNames, Table tableInfo, Row rowInfo) {
		
		DbCommand command = new DbCommand();
		try {
			setQueryString(table, row, command, queryPrefix, columnNames);
			executeNonQuery(command);
		} catch(Exception e) {
			throw new ConfigException(e, "M_Fixture_Temp_Database_DeleteRow", table.getName(), tableInfo, rowInfo);
		} finally {
			command.close();
		}
	}

	private void executeNonQuery(DbCommand command) throws SQLException {
		command.printSQL();
		command.executeNonQuery();
	}
	
	private void setQueryString(TempDynaSet table, DynaBean row,
			DbCommand command, String queryPrefix, List<String> columnNames) throws SQLException {
		DynaClass metaData = getMetaDataTable(table.getTableName());
		StringBuilder query = new StringBuilder();
		for(String columnName : columnNames) {
			Class<?> columnType = getColumnType(columnName, metaData, table);
			Object columnValue = convertType(columnType, row.get(columnName));
			addParameter(command, queryPrefix, query, columnName, columnType, columnValue);
		}
		command.setSQL(query.toString(), true);
	}

	private Object convertType(Class<?> columnType, Object columnValue) {
		if(columnValue == null || !(columnValue instanceof String) ||
				columnType.isAssignableFrom(columnValue.getClass())) {
			return columnValue;
		}

		String valueAsText = (String)columnValue;
		if("*".equals(valueAsText) || valueAsText.indexOf("%") > -1) {
			return valueAsText;
		}
		
		try {
			return TypeConverter.changeType(valueAsText, columnType);
		} catch(Exception e) {
			return columnValue;
		}
	}

	private Class<?> getColumnType(String columnName, DynaClass metaData,
			TempDynaSet table) {
		DynaProperty column = metaData.getDynaProperty(columnName);
		if(column != null) {
			return column.getType();
		}
		return table.getColumn(columnName).getType();
	}

	private void addParameter(DbCommand command, String queryPrefix,
			StringBuilder query, String columnName, Class<?> columnType,
			Object columnValue) {		
		addParameterPrefix(queryPrefix, query);
		query.append(columnName);
		if(columnValue == null) {
			query.append(" is null");
		} else if("*".equals(columnValue)) {
			query.append(" is not null");
		} else {
			if(columnValue instanceof String &&
				(((String)columnValue).startsWith("%") || ((String)columnValue).endsWith("%"))) {
				query.append(" like ");
			} else {
				query.append(" = ");
			}
			query.append("?");
			addParameter(command, columnType, columnValue);
		}
	}

	private void addParameterPrefix(String queryPrefix, StringBuilder query) {
		if(query.length() == 0) {
			query.append(queryPrefix);
		} else {
			query.append(" and ");
		}
	}

	private void addParameter(DbCommand command, Class<?> columnType, Object columnValue) {
		command.addParameter(columnType, columnValue);
	}
	
	public List<DynaBean> select(List<String> keyColumns, TempDynaSet keyTable, Table tableInfo) {
    	use(keyTable.getDatabaseName());
		String queryPrefix = getQueryPrefixForSelect(keyTable);
		List<DynaBean> resultTable = new ArrayList<DynaBean>();
		List<Row> rowInfo = tableInfo.getRows();
		int rowIndex = 0;
		for(DynaBean keyRow : keyTable.getRows()) {
			select(keyColumns, keyTable, keyRow, resultTable, tableInfo, rowInfo.get(rowIndex++), queryPrefix);
		}
		return resultTable;
	}

	private String getQueryPrefixForSelect(TempDynaSet table) {
		StringBuilder queryString = new StringBuilder();
		for(DynaProperty property : table.getColumns()) {
			if (queryString.length() == 0) {
				queryString.append("select ");
			} else {
				queryString.append(",");
			}
			queryString.append(property.getName());
		}
		queryString.append(" from ").append(table.getTableName()).append(" where ");
		return queryString.toString();
	}

	private void select(List<String> keyColumns,
			TempDynaSet keyTable, DynaBean keyRow,
			List<DynaBean> resultTable, Table tableInfo, Row rowInfo,
			String queryPrefix) {
		DbCommand command = new DbCommand();
		try {
			setQueryString(keyTable, keyRow, command, queryPrefix, keyColumns);
			List<DynaBean> result = executeQuery(command).list;
			DynaBean resultRow = selectResultRow(keyTable.getName(), 
					result, keyRow, tableInfo, rowInfo, keyColumns);
			resultTable.add(resultRow);
		} catch(Exception e) {
			throw new ConfigException(e, "M_Fixture_Temp_Database_SelectRow", 
					keyTable.getName(), tableInfo, rowInfo);
		} finally {
			command.close();
		}
	}

	private DynaBean selectResultRow(String tableName, List<DynaBean> result, DynaBean keyRow,
			Table tableInfo, Row rowInfo, List<String> keyColumns) {
		int rowCount = result.size();
		if(rowInfo.isDeleted()) {
			if(rowCount == 0) {
				return null;
			} else {
				Assertie.fail("M_Fixture_Temp_DatabaseValidator_UnexpectedData", 
						tableName, tableInfo, rowInfo, toString(keyColumns, keyRow));
			}
		}
		
		if(rowCount == 0) {
			String message = tableInfo.hasKeyColumn() ?
					"M_Fixture_Temp_DatabaseValidator_NotFound" :
					"M_Fixture_Temp_DatabaseValidator_NotFound_With_Comment";
				Assertie.fail(message, tableName, tableInfo, rowInfo, toString(keyColumns, keyRow));
		}
		
		if(rowCount > 1) {
			Assertie.fail("M_Fixture_Temp_DatabaseValidator_OneMoreData", tableName, tableInfo, rowInfo, toString(keyColumns, keyRow));
		}
		
		return result.get(0);
	}

	private String toString(List<String> keyColumns, DynaBean keyRow) {
		StringBuilder text = new StringBuilder();
		for(String columnName : keyColumns) {
			if(text.length() == 0) {
				text.append("{");
			} else {
				text.append(", ");
			}
			text.append(columnName).append("=").append(keyRow.get(columnName));
		}
		text.append("}");
		return text.toString();
	}

	private DbResult executeQuery(DbCommand command) throws SQLException {
		command.printSQL();
		ResultSet resultSet = command.executeQuery();
		DynaClass dynaClass = getDynaClass(resultSet);
		
		List<DynaBean> list = new ArrayList<DynaBean>();
		ColumnValueConverter converter = ColumnValueConverter.getInstance();
		DynaProperty[] properties = dynaClass.getDynaProperties();
		while(resultSet.next()) {
			DynaBean bean;
			try {
				bean = dynaClass.newInstance();
			} catch (Exception e) {
				throw new ConfigException(e);
			}
			for(int i = 0; i < properties.length; i++) {
				Object value = converter.getResult(resultSet, properties[i].getType(), i + 1);
				bean.set(properties[i].getName(), value);
			}
			list.add(bean);
		}
		return new DbResult(dynaClass, list);
	}
	
	private DynaClass getDynaClass(ResultSet resultSet) throws SQLException {
		ResultSetMetaData md = resultSet.getMetaData();
		int count = md.getColumnCount();
		DynaProperty[] properties = new DynaProperty[count];
		for(int i = 0; i < properties.length; i++) {
			int column = i + 1;
			Class<?> type = TypeConverter.getJavaType(
					md.getColumnType(column), md.getColumnTypeName(column), 
					md.getPrecision(column), md.getScale(column));
			String name = getColumnLabel(md, column);
			properties[i] = new DynaProperty(name, type);
		}
		return new BasicDynaClass(null, null, properties);
	}

	private String getColumnLabel(ResultSetMetaData md, int column) throws SQLException {
		try {
			String label = md.getColumnLabel(column);
			if(!Strings.isEmpty(label)) {
				return label;
			}
		} catch(Exception e) {
			ExceptionHandler.ignore(e);
		}
		return md.getColumnName(column);
	}

	public TempDynaClass getMetaData(Table table) {
		TempDynaClass tempClass = new TempDynaClass(table.getName());
		use(tempClass.getDatabaseName());
		try {
			DynaClass metaData = getMetaDataTable(tempClass.getTableName());
			tempClass.update(metaData);
			return tempClass;
		} catch(Exception e) {
			throw new ConfigException(e, "M_Fixture_Temp_Database_GetMetaData", tempClass.getTableName(), table);
		}
	}
	
	private DynaClass getMetaDataTable(String tableName) throws SQLException {
		MetaData metaData = getMetaData(tableName);
		if(metaData.metaDataTable == null) {
			metaData.metaDataTable = 
					executeQueryInternal("select * from " + tableName + " where 1=2").dynaClass;
		}
		return metaData.metaDataTable;
	}

	private MetaData getMetaData(String tableName) {
		MetaData metaData = metaDatas.get(tableName);
		if(metaData == null) {
			metaData = new MetaData();
			metaDatas.put(tableName, metaData);
		}
		return metaData;
	}

	public DbResult executeQueryInternal(String queryString) throws SQLException {
		DbCommand command = createCommand(queryString);
		try {
			return executeQuery(command);
		} finally {
			command.close();
		}
	}

	public static List<DynaBean> executeQuery(String databaseName, String queryString) throws SQLException {
		Database database = new Database();
		if(databaseName != null) {
			database.use(databaseName);
		}
		try {
			return database.executeQueryInternal(queryString).list;
		} finally {
			database.dispose();
		}
	}
	
	public static void executeNonQuery(String databaseName, String queryString) throws SQLException {
		Database database = new Database();
		if(databaseName != null) {
			database.use(databaseName);
		}
		try {
			database.executeNonQuery(queryString);
			database.commit();
		} finally {
			database.dispose();
		}
	}
	
	private void executeNonQuery(String queryString) throws SQLException {
		DbCommand command = createCommand(queryString);
		try {
			executeNonQuery(command);
		} finally {
			command.close();
		}
	}
	
	private DbCommand createCommand(String sql) {
		return new DbCommand(sql);
	}

}
