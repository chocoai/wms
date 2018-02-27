package com.xyy.bill.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.log.Log;

/**
 * 数据库访问 辅助计算类
 * 
 * @author evan
 *
 */
public class TableSchemaUtil {
	private static final Log log = Log.getLog(TableSchemaUtil.class);
	private Connection connection;

	public TableSchemaUtil(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * 处理参数
	 * 
	 * @param ps
	 *            :
	 * @param args
	 *            :参数
	 */
	private void setParameters(PreparedStatement ps, Object... args) {
		try {
			if (ps != null && args != null) {
				int size = args.length;
				for (int i = 0; i < size; i++) {
					ps.setObject(i + 1, args[i]);
		
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行更新
	 * 
	 * @param sql
	 *            :sql语句
	 * @param args
	 *            :参数
	 * @return:更新的记录数
	 */
	public int executeUpdate(String sql, Object... args) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = this.getConnection();
			ps = con.prepareStatement(sql);
			this.setParameters(ps, args);
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 执行批量更新
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public int[] executeBatchUpdate(String sql, List<Object[]> args) {
		Connection con = null;
		PreparedStatement ps = null;
		int[] result = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(sql);
			ps.clearBatch();
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					this.setParameters(ps, args.get(i));
					ps.addBatch();
				}
			}
			result = ps.executeBatch();
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				ps.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	// 查询表的schema
	public TableSchema querySchema(String table) {
		Connection con = null;
		PreparedStatement ps = null;
		TableSchema result = null;
		try {
			con = this.getConnection();
			ps = con.prepareStatement("select * from " + table + "  where 1!=1");
			ResultSet rs = ps.executeQuery();
			result = new TableSchema(table);
			ResultSetMetaData metaData = rs.getMetaData();
			rs = ps.executeQuery("show full columns from " + table);  
			int columnTotal = metaData.getColumnCount();
			for (int i = 1; i <= columnTotal; i++) {
				ColumnSchema column=new ColumnSchema();
				column.setColumnName(metaData.getColumnName(i));
				column.setColumnType(metaData.getColumnType(i));
				column.setColumnTypeName(metaData.getColumnTypeName(i));
				column.setPrecision(metaData.getPrecision(i));
				column.setScale(metaData.getScale(i));
				if(rs.next()){
					column.setComment(rs.getString("Comment"));
				}
				result.getColumnSchemas().put(column.getColumnName(), column);
			}
	
		} catch (Exception e) {			
			log.info(e.getMessage());
		} finally {
			try {
				ps.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;

	}

	/**
	 * 查询数据
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public ResultSet executeQuery(String sql, Object... args) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = this.getConnection();
			ps = con.prepareStatement(sql);
			this.setParameters(ps, args);
			ResultSet rs = ps.executeQuery();

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static class TableSchema {
		private String tableName;
		private Map<String, ColumnSchema> columnSchemas = new HashMap<>();

		public TableSchema(String tableName) {
			super();
			this.tableName = tableName;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public Map<String, ColumnSchema> getColumnSchemas() {
			return columnSchemas;
		}

		public void setColumnSchemas(Map<String, ColumnSchema> columnSchemas) {
			this.columnSchemas = columnSchemas;
		}

	}

	/**
	 * 数据表shcema: columnName，列名称 columnType：列类型，java.sql.Types
	 * columnTypeName:数据库类型名称，支持名称包含： VARCHAR,INT,BIGINT,DECIMAL,DATE,TIMESTAMP,
	 * 【注意】VARCHAR(21845)==>text 其他类型引擎不支持
	 * precision：对于数字类性，就是int(20),decimal(20,scale),varchar(3000)
	 * scale:用于decimal小数部分
	 * 
	 * 
	 * @author evan
	 *
	 */
	public static class ColumnSchema {
		private String columnName;
		private int columnType;
		private String columnTypeName;
		private int precision;
		private int scale;
		private String comment;

		public ColumnSchema() {
			super();
		}

		public ColumnSchema(String columnName, int columnType, String columnTypeName, int precision, int scale, String comment) {
			super();
			this.columnName = columnName;
			this.columnType = columnType;
			this.columnTypeName = columnTypeName;
			this.precision = precision;
			this.scale = scale;
			this.comment = comment;
		}

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public int getColumnType() {
			return columnType;
		}

		public void setColumnType(int columnType) {
			this.columnType = columnType;
		}

		public String getColumnTypeName() {
			return columnTypeName;
		}

		public void setColumnTypeName(String columnTypeName) {
			this.columnTypeName = columnTypeName;
		}

		public int getPrecision() {
			return precision;
		}

		public void setPrecision(int precision) {
			this.precision = precision;
		}

		public int getScale() {
			return scale;
		}

		public void setScale(int scale) {
			this.scale = scale;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}
		
	}

}
