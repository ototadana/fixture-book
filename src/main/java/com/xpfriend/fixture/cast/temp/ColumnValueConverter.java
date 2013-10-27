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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.xpfriend.junk.Classes;
import com.xpfriend.junk.Formi;
import com.xpfriend.junk.Strings;

/**
 * @author Ototadana
 *
 */
public class ColumnValueConverter {

	private static ColumnValueConverter instance;

	/*
	 * String --> SQLType 変換で使う文字型
	 */
	private static int charType = Types.VARCHAR;
	
	static {
		instance = Classes.newInstance(ColumnValueConverter.class);
	}
	/**
	 * ColumnValueConverter のインスタンスを取得する。
	 * @return ColumnValueConverter のインスタンス。
	 */
	public static ColumnValueConverter getInstance() {
		return instance;
	}

	private static abstract class Type {
		abstract Object get(ResultSet rs, int index) throws SQLException;
		abstract Object get(CallableStatement stmt, int index) throws SQLException;
		abstract void set(PreparedStatement stmt, int index, Object value) throws SQLException;
	}

	private static final Type STRING = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return rs.getString(index);
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return stmt.getString(index);
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setString(index, (String)value);
		}
	};

	private static final Type INT_P = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return Integer.valueOf(rs.getInt(index));
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return Integer.valueOf(stmt.getInt(index));
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setInt(index, ((Integer)value).intValue());
		}
	};

	private static final Type INT_W = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			int i = rs.getInt(index);
			if(rs.wasNull()) {
				return null;
			} else {
				return Integer.valueOf(i);
			}
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			int i = stmt.getInt(index);
			if(stmt.wasNull()) {
				return null;
			} else {
				return Integer.valueOf(i);
			}
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setInt(index, ((Integer)value).intValue());
		}
	};

	private static final Type LONG_P = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return Long.valueOf(rs.getLong(index));
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return Long.valueOf(stmt.getLong(index));
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setLong(index,((Long)value).longValue());
		}
	};

	private static final Type LONG_W = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			long l = rs.getLong(index);
			if(rs.wasNull()) {
				return null;
			} else {
				return Long.valueOf(l);
			}
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			long l = stmt.getLong(index);
			if(stmt.wasNull()) {
				return null;
			} else {
				return Long.valueOf(l);
			}
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setLong(index,((Long)value).longValue());
		}
	};

	private static final Type SHORT_P = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return Short.valueOf(rs.getShort(index));
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return Short.valueOf(stmt.getShort(index));
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setShort(index,((Short)value).shortValue());
		}
	};

	private static final Type SHORT_W = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			short s = rs.getShort(index);
			if(rs.wasNull()) {
				return null;
			} else {
				return Short.valueOf(s);
			}
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			short s = stmt.getShort(index);
			if(stmt.wasNull()) {
				return null;
			} else {
				return Short.valueOf(s);
			}
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setShort(index,((Short)value).shortValue());
		}
	};

	private static final Type FLOAT_P = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return Float.valueOf(rs.getFloat(index));
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return Float.valueOf(stmt.getFloat(index));
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setFloat(index,((Float)value).floatValue());
		}
	};

	private static final Type FLOAT_W = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			float f = rs.getFloat(index);
			if(rs.wasNull()) {
				return null;
			} else {
				return Float.valueOf(f);
			}
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			float f = stmt.getFloat(index);
			if(stmt.wasNull()) {
				return null;
			} else {
				return Float.valueOf(f);
			}
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setFloat(index,((Float)value).floatValue());
		}
	};

	private static final Type DOUBLE_P = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return Double.valueOf(rs.getDouble(index));
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return Double.valueOf(stmt.getDouble(index));
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setDouble(index,((Double)value).doubleValue());
		}
	};

	private static final Type DOUBLE_W = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			double f = rs.getDouble(index);
			if(rs.wasNull()) {
				return null;
			} else {
				return Double.valueOf(f);
			}
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			double f = stmt.getDouble(index);
			if(stmt.wasNull()) {
				return null;
			} else {
				return Double.valueOf(f);
			}
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setDouble(index,((Double)value).doubleValue());
		}
	};

	private static final Type BYTE_P = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return Byte.valueOf(rs.getByte(index));
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return Byte.valueOf(stmt.getByte(index));
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setByte(index,((Byte)value).byteValue());
		}
	};

	private static final Type BYTE_W = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			byte f = rs.getByte(index);
			if(rs.wasNull()) {
				return null;
			} else {
				return Byte.valueOf(f);
			}
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			byte f = stmt.getByte(index);
			if(stmt.wasNull()) {
				return null;
			} else {
				return Byte.valueOf(f);
			}
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setByte(index,((Byte)value).byteValue());
		}
	};

	private static final Type BOOLEAN_P = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return Boolean.valueOf(rs.getBoolean(index));
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return Boolean.valueOf(stmt.getBoolean(index));
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setBoolean(index,((Boolean)value).booleanValue());
		}
	};

	private static final Type BOOLEAN_W = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			boolean f = rs.getBoolean(index);
			if(rs.wasNull()) {
				return null;
			} else {
				return Boolean.valueOf(f);
			}
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			boolean f = stmt.getBoolean(index);
			if(stmt.wasNull()) {
				return null;
			} else {
				return Boolean.valueOf(f);
			}
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setBoolean(index,((Boolean)value).booleanValue());
		}
	};

	private static final Type CHAR_P = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			String s = rs.getString(index);
			return toChar(s, rs.wasNull());
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			String s = stmt.getString(index);
			return toChar(s, stmt.wasNull());
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setString(index, String.valueOf(((Character)value).charValue()));
		}
	};

	private static Character toChar(String s, boolean wasNull) {
		if(wasNull) {
			return Character.valueOf('\0');
		} else {
			if(!Strings.isEmpty(s)) {
				return Character.valueOf(s.charAt(0));
			} else {
				return Character.valueOf('\0');
			}
		}
	}

	private static final Type CHAR_W = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			String s = rs.getString(index);
			return toCharacter(s, rs.wasNull());
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			String s = stmt.getString(index);
			return toCharacter(s, stmt.wasNull());
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setString(index, String.valueOf(((Character)value).charValue()));
		}
	};

	private static Character toCharacter(String s, boolean wasNull) {
		if(wasNull) {
			return null;
		} else {
			if(s.length()>0) {
				return Character.valueOf(s.charAt(0));
			} else {
				return null;
			}
		}
	}

	private static final Type BIGDECIMAL = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return rs.getBigDecimal(index);
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return stmt.getBigDecimal(index);
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setBigDecimal(index, (BigDecimal)value);
		}
	};

	private static final Type TIMESTAMP = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return rs.getTimestamp(index);
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return stmt.getTimestamp(index);
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setTimestamp(index, (Timestamp)value);
		}
	};

	private static final Type TIME = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return rs.getTime(index);
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return stmt.getTime(index);
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setTime(index, (Time)value);
		}
	};

	private static final Type SQL_DATE = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return rs.getDate(index);
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return stmt.getDate(index);
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setDate(index, (java.sql.Date)value);
		}
	};

	private static final Type BYTES = new Type() {
		@Override
		Object get(ResultSet rs, int index) throws SQLException {
			return rs.getBytes(index);
		}

		@Override
		Object get(CallableStatement stmt, int index) throws SQLException {
			return stmt.getBytes(index);
		}

		@Override
		void set(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setBytes(index, (byte[])value);
		}
	};

	private static final Map<Class<?>, Type> TYPE_MAP = new HashMap<Class<?>, Type>(40);

	static {
		TYPE_MAP.put(BigDecimal.class, BIGDECIMAL);
		TYPE_MAP.put(boolean.class, BOOLEAN_P);
		TYPE_MAP.put(Boolean.class, BOOLEAN_W);
		TYPE_MAP.put(byte.class, BYTE_P);
		TYPE_MAP.put(Byte.class, BYTE_W);
		TYPE_MAP.put(char.class, CHAR_P);
		TYPE_MAP.put(Character.class, CHAR_W);
		TYPE_MAP.put(double.class, DOUBLE_P);
		TYPE_MAP.put(Double.class, DOUBLE_W);
		TYPE_MAP.put(float.class, FLOAT_P);
		TYPE_MAP.put(Float.class, FLOAT_W);
		TYPE_MAP.put(int.class, INT_P);
		TYPE_MAP.put(Integer.class, INT_W);
		TYPE_MAP.put(long.class, LONG_P);
		TYPE_MAP.put(Long.class, LONG_W);
		TYPE_MAP.put(short.class, SHORT_P);
		TYPE_MAP.put(Short.class, SHORT_W);
		TYPE_MAP.put(java.sql.Date.class, SQL_DATE);
		TYPE_MAP.put(Time.class, TIME);
		TYPE_MAP.put(Timestamp.class, TIMESTAMP);
		TYPE_MAP.put(byte[].class, BYTES);
	}

	public ColumnValueConverter() {
		// デフォルトインスタンス生成、サブクラスによるオーバーライド用、Javadoc 用
	}

	/**
	 * ResultSet の指定列からデータを取得する。
	 * @param rs ResultSet。
	 * @param type オブジェクトの型。
	 * @param index 列番号。
	 * @return 取得されたデータ。
	 */
	public Object getResult(ResultSet rs, Class<?> type, int index) throws SQLException {
		try {
			return getResultByType(rs, type, index);
		} catch(UnsupportedTypeException e) {
			return rs.getObject(index);
		}
	}

	/**
	 * ResultSet の指定列からデータを取得する。
	 * @param rs ResultSet。
	 * @param type オブジェクトの型。
	 * @param index 列番号。
	 * @return 取得されたデータ。
	 */
	public Object getResultByType(ResultSet rs, Class<?> type, int index) throws SQLException, UnsupportedTypeException {
		if(type.equals(String.class)) {
			return STRING.get(rs, index);
		}

		Type t = TYPE_MAP.get(type);
		if(t != null) {
			return t.get(rs, index);
		} else if(type.equals(java.util.Date.class)) {
			return rs.getTimestamp(index);
		} else {
			throw new UnsupportedTypeException(type);
		}
	}

	/**
	 * パラメタをセットする。
	 * @param stmt パラメタをセットする Statement。
	 * @param index パラメタをセットする位置。
	 * @param type パラメタの型。
	 * @param value パラメタの値。
	 */
	public void setParameter(PreparedStatement stmt, int index, Class<?> type, Object value)
			throws SQLException {
		if(value==null) {
			int sqlType = getSQLType(type);
			stmt.setNull(index, sqlType);
			return;
		}

		if(String.class.equals(type)) {
			STRING.set(stmt, index, value);
			return;
		}

		Type t = TYPE_MAP.get(type);
		if(t != null) {
			t.set(stmt, index, value);
			return;
		}

		if(java.util.Date.class.isAssignableFrom(type)) {
			stmt.setTimestamp(index,Formi.toTimestamp((java.util.Date)value));
		} else if(byte[].class.equals(type)) {
			stmt.setBytes(index, (byte[])value);
		} else {
			stmt.setObject(index, value);
		}
	}

	/**
	 * CallableStatement の指定列からデータを取得する。
	 * @param stmt CallableStatement。
	 * @param type オブジェクトの型。
	 * @param index 列番号。
	 * @return 取得されたデータ。
	 */
	public Object getResult(CallableStatement stmt, Class<?> type, int index) throws SQLException{
		if(type.equals(String.class)) {
			return STRING.get(stmt, index);
		}

		Type t = TYPE_MAP.get(type);
		if(t != null) {
			return t.get(stmt, index);
		} else if(type.equals(java.util.Date.class)) {
			return stmt.getTimestamp(index);
		} else {
			return stmt.getObject(index);
		}
	}

	/**
	 * Java クラスから SQL データ型
	 * を表す定数(java.sql.Types を参照)を取得する。
	 *
	 * @param javatype Java クラス
	 * @return SQL データ型を表す定数
	 * @see java.sql.Types
	 */
	public int getSQLType(Class<?> javatype) {
		if(javatype.equals(String.class)) {
			return getCharType();
		} else if(javatype.equals(int.class) ||
				javatype.equals(Integer.class)) {
			return Types.INTEGER;
		} else if(javatype.equals(long.class) ||
				javatype.equals(Long.class)) {
			return Types.BIGINT;
		} else if(javatype.equals(float.class) ||
				javatype.equals(Float.class)) {
			return Types.REAL;
		} else if(javatype.equals(double.class) ||
				javatype.equals(Double.class)) {
			return Types.DOUBLE;
		} else if(javatype.equals(short.class) ||
				javatype.equals(Short.class)) {
			return Types.SMALLINT;
		} else if(javatype.equals(byte.class) ||
				javatype.equals(Byte.class)) {
			return Types.TINYINT;
		} else if(javatype.equals(boolean.class) ||
				javatype.equals(Boolean.class)) {
			//return Types.BIT;
			return Types.BOOLEAN;
		} else if(javatype.equals(java.math.BigDecimal.class)) {
			return Types.NUMERIC;
		} else if(javatype.equals(java.sql.Date.class)) {
			return Types.DATE;
		} else if(javatype.equals(java.sql.Time.class)) {
			return Types.TIME;
		} else if(javatype.equals(java.sql.Timestamp.class) ||
				java.util.Date.class.isAssignableFrom(javatype)) {
			return Types.TIMESTAMP;
		} else if(javatype.equals(char.class) || javatype.equals(Character.class)) {
			return getCharType();
		} else if(javatype.equals(byte[].class)) {
			return Types.VARBINARY;
		} else {
			return Types.OTHER;
		}
	}

	protected int getCharType() {
		return charType;
	}

}
