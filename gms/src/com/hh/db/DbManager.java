package com.hh.db;

import static com.hh.db.ScalarResultBuilder.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.hh.common.utils.XmlUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;



public class DbManager {
	
	private static final Logger logger = LoggerFactory.getLogger(DbManager.class);
	private ComboPooledDataSource ds;
	private String name;
	
	private static Map<String, DbManager> dbs;
	
	
	protected DbManager(String name, DbConnectionInfo connInfo) {
		this.name = name;
		try {
			ds = createDataSource(connInfo);
		} catch (Exception e) {
			logError("PjaDbManager error() error:", e);
		}
	}
	
	public static void init(String confPath) throws Exception{
		InputStream in = new FileInputStream(confPath);
		Document doc = XmlUtil.getDoc(in);
		Node root = XmlUtil.getRootNode(doc);
		List<Node> list = XmlUtil.getChildNodes(root, "db");
		dbs = new HashMap<String, DbManager>();
		for (Node node : list) {
			String name = XmlUtil.getFirstChildNode(node, "name").getTextContent();
			DbConnectionInfo connInfo = new DbConnectionInfo();
			connInfo.driver = XmlUtil.getFirstChildNode(node, "driver").getTextContent();
			connInfo.connectionString = XmlUtil.getFirstChildNode(node, "connection").getTextContent();
			dbs.put(name, new DbManager(name, connInfo));
		}
	}
	
	public static DbManager getDbManager(String name) throws DbException {
		if (dbs == null) {
			throw new DbException("DbManager 未初始化");
		}
		return dbs.get(name);
	}
	
	private static ComboPooledDataSource createDataSource(DbConnectionInfo connInfo) {
		ComboPooledDataSource ds = new ComboPooledDataSource();

		try {
			ds.setDriverClass(connInfo.driver);
		} catch (Exception ex) {
			logError("DataSource set DriverClass error:", ex);
		}

		ds.setJdbcUrl(connInfo.connectionString);
		ds.setMinPoolSize(10);
		ds.setMaxPoolSize(50);
		ds.setCheckoutTimeout(6000);
		ds.setIdleConnectionTestPeriod(2);
		ds.setMaxIdleTime(2);
		ds.setPreferredTestQuery("SELECT 1");
		ds.setMaxStatements(0);//关闭缓存，据说可以防止c3p0某些bug造成的连接池泄露，但貌似默认已经是0了，有机会仔细研究下
		//异步检测连接的有效性
		ds.setTestConnectionOnCheckin(true);
		ds.setTestConnectionOnCheckout(false);
		return ds;
	}
	
	public String getDbName() {
		return name;
	}
	
	public Connection getConnection() throws DbException {
		return getConnection(false);
	}
	
	public Connection getConnection(boolean readonly) throws DbException {
		
		try {
			Connection conn = ds.getConnection();
			conn.setReadOnly(readonly);
			return conn;
		} catch (Exception e) {
			
			throw new DbException("Get connection from pool error:" + e.getMessage(), e);
		}
	}

	/**
	 * 执行查询
	 * 
	 * @param <T>
	 *            类型参数
	 * @param cmd
	 *            具体sql
	 * @param builder
	 *            结果集的包装器，将查询获得的每条记录封装成T类型的对象
	 * @return 类型为T的DataRow数组链表
	 */
	public <T> ArrayList<T> executeQuery_ObjectList(String cmd, ResultObjectBuilder<T> builder)
			throws DbException {
		return executeQuery_ObjectList(cmd, null, builder);
	}

	/**
	 * 执行结果为整数的查询
	 * 
	 * @param cmd
	 *            具体sql
	 * @return 查询结果整数
	 */
	public Integer executeScalarInt(String cmd) throws DbException {
		return this.executeScalarInt(cmd, null);
	}

	/**
	 * 执行结果为整数的查询
	 * 
	 * @param cmd
	 *            具体sql
	 * @param params
	 *            执行sql时的参数
	 * @return 查询结果整数
	 */
	public Integer executeScalarInt(String cmd, Object[] params) throws DbException {
		return executeScalarObject(cmd, params, intResultBuilder);
	}

	/**
	 * 执行结果为字符串的查询
	 * 
	 * @param cmd
	 *            具体sql
	 * @return 查询结果字符串
	 */
	public String executeScalarString(String cmd) throws DbException {
		return executeScalarString(cmd, null);
	}

	/**
	 * 执行结果为字符串的查询
	 * 
	 * @param cmd
	 *            具体sql
	 * @param params
	 *            执行sql时的参数
	 * @return 查询结果字符串
	 */
	public String executeScalarString(String cmd, Object[] params) throws DbException {
		return executeScalarObject(cmd, params, stringResultBuilder);
	}

	/**
	 * 执行结果为日期的查询
	 * 
	 * @param cmd
	 *            具体sql
	 * @return 查询结果日期
	 */
	public Date executeScalarDate(String cmd) throws DbException {
		return executeScalarDate(cmd, null);
	}

	/**
	 * 执行结果为日期的查询
	 * 
	 * @param cmd
	 *            具体sql
	 * @param params
	 *            执行sql时的参数
	 * @return 查询结果日期
	 */
	public Date executeScalarDate(String cmd, Object[] params) throws DbException {
		return executeScalarObject(cmd, params, dateResultBuilder);
	}

	public Boolean executeScalarBool(String cmd) throws DbException {
		return executeScalarBool(cmd, null);
	}

	public Boolean executeScalarBool(String cmd, Object[] params) throws DbException {
		return executeScalarObject(cmd, params, booleanResultBuilder);
	}

	public Long executeScalarLong(String cmd) throws DbException {
		return executeScalarLong(cmd, null);
	}

	public Long executeScalarLong(String cmd, Object[] params) throws DbException {
		return executeScalarObject(cmd, params, longResultBuilder);
	}

	/**
	 * 执行结果为T类型对象的查询
	 * 
	 * @param <T>
	 *            类型参数
	 * @param cmd
	 *            具体sql
	 * @param builder
	 *            结果集的包装器，将查询获得的记录封装成T类型的对象
	 * @return T类型对象
	 */
	public <T> T executeScalarObject(String cmd, ResultObjectBuilder<T> builder) throws DbException {
		return executeScalarObject(cmd, null, builder);
	}

	/**
	 * 执行结果为T类型对象的查询
	 * 
	 * @param <T>
	 *            类型参数
	 * @param cmd
	 *            具体sql
	 * @param params
	 *            执行sql时的参数
	 * @param builder
	 *            结果集的包装器，将查询获得的记录封装成T类型的对象
	 * @return T类型对象
	 */
	public <T> T executeScalarObject(String cmd, Object[] params, ResultObjectBuilder<T> builder)
			throws DbException {
		ArrayList<T> list = executeQuery_ObjectList(cmd, params, builder);
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}

	/**
	 * 返回该查询是否有结果
	 * 
	 * @param cmd
	 *            具体sql
	 * @return 查询是否有结果
	 */
	public boolean isRecordExist(String cmd) throws DbException {
		return this.isRecordExist(cmd, null);
	}

	/**
	 * 返回该查询是否有结果
	 * 
	 * @param cmd
	 *            具体sql
	 * @param params
	 *            参数
	 * @return 查询是否有结果
	 */
	public boolean isRecordExist(String cmd, Object[] params) throws DbException {
		boolean result = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(true);
			stmt = this.prepareStatement(conn, cmd, params);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			throw new DbException(e.getMessage() + getQueryCmdErrorMsg("isRecordExist", cmd, params), e);
		} finally {
			releaseDbResource(rs, stmt, conn);
		}
		return result;
	}

	/**
	 * 执行更新操作(增、删、改)
	 * 
	 * @param cmd
	 *            具体sql
	 * @return 该操作影响的记录数
	 * @throws DbException
	 */
	public int executeCommand(String cmd) throws DbException {
		return this.executeCommand(cmd, null, null);
	}

	/**
	 * 执行更新操作(增、删、改)
	 * 
	 * @param cmd
	 *            具体sql
	 * @param tranConn
	 *            数据库连接
	 * @return 该操作影响的记录数
	 * @throws DbException
	 */
	public int executeCommand(String cmd, Connection tranConn) throws DbException {
		return this.executeCommand(cmd, null, tranConn);
	}

	/**
	 * 执行更新操作(增、删、改)
	 * 
	 * @param cmd
	 *            具体sql
	 * @param params
	 *            具体参数
	 * @return 该操作影响的记录数
	 * @throws DbException
	 */
	public int executeCommand(String cmd, Object[] params) throws DbException {
		return this.executeCommand(cmd, params, null);
	}

	/**
	 * 执行查询
	 * 
	 * @param <T>
	 *            类型参数
	 * @param cmd
	 *            具体sql
	 * @param params
	 *            具体参数
	 * @param builder
	 *            结果集的包装器，将查询获得的每条记录封装成T类型的对象
	 * @return 类型为T的数组链表
	 * @throws DbException
	 */
	public <T> ArrayList<T> executeQuery_ObjectList(String cmd, Object[] params,
			ResultObjectBuilder<T> builder) throws DbException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<T> objectList = null;
		try {
			conn = getConnection(true);
			stmt = this.prepareStatement(conn, cmd, params);
			rs = stmt.executeQuery();
			objectList = getObjectListFromResultSet(rs, builder);
		} catch (Exception e) {
			throw new DbException(e.getMessage()
					+ getQueryCmdErrorMsg("executeQuery_ObjectList", cmd, params), e);
		} finally {
			releaseDbResource(rs, stmt, conn);
		}
		return objectList;
	}

	/**
	 * 执行更新操作(增、删、改)
	 * 
	 * @param cmd
	 *            具体sql
	 * @param params
	 *            具体参数
	 * @param tranConn
	 *            数据库连接
	 * @return 该操作影响的记录数,没有返回结果则返回0
	 * @throws DbException
	 */
	public int executeCommand(String cmd, Object[] params, Connection tranConn) throws DbException {
		Connection conn = null;
		PreparedStatement stmt = null;
		int rowCount;
		try {
			if (tranConn != null) {
				conn = tranConn;
			} else {
				conn = getConnection();
			}

			stmt = this.prepareStatement(conn, cmd, params);
			rowCount = stmt.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage() + getQueryCmdErrorMsg("executeCommand", cmd, params), e);
		} finally {
			if (tranConn == null) {
				releaseDbResource(null, stmt, conn);
			} else {
				releaseDbResource(null, stmt, null);
			}
		}
		return rowCount;
	}

	/**
	 * 获取最后insert的id
	 * 
	 * @param conn
	 *            数据库连接
	 * @return id
	 * @throws DbException
	 */
	
	private final static String GET_LAST_INSERT_ID = "SELECT last_insert_id() AS Id";
	public int getLastInsertId(Connection conn) throws DbException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int lastInsertId = 0;
		try {
			stmt = conn.prepareStatement(GET_LAST_INSERT_ID);
			rs = stmt.executeQuery();
			if (rs.next()) {
				lastInsertId = rs.getInt("Id");
			}
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		} finally {
			releaseDbResource(rs, stmt, null);
		}
		return lastInsertId;
	}

	/**
	 * 执行Insert
	 * 
	 * @param cmd
	 *            具体的Insert sql
	 * @param params
	 *            具体参数
	 * @return 如果执行成功，返回新记录的自增Id，没有则返回0
	 * @throws DbException
	 */
	public int executeInsertCommand(String cmd, Object[] params) throws DbException {
		Connection conn = null;
		int newId = 0;
		try {
			conn = getConnection();
			int result = this.executeCommand(cmd, params, conn);
			if (result > 0) {
				newId = this.getLastInsertId(conn);
			}
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		} finally {
			releaseDbResource(null, conn);
		}
		return newId;
	}

	/**
	 * 返回一个预处理语句
	 * 
	 * @param conn
	 *            数据库连接
	 * @param cmd
	 *            具体的sql
	 * @param params
	 *            具体参数
	 * @return 预处理语句
	 * @throws SQLException
	 */
	public PreparedStatement prepareStatement(Connection conn, String cmd, Object[] params)
			throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(cmd);
		if (params != null) {
			setParams(stmt, params);
		}
		return stmt;
	}

	public int[] executeBatchCommand(String cmd, Collection<Object[]> paramsList) throws DbException {
		return this.executeBatchCommand(cmd, paramsList, false);
	}

	/**
	 * 
	 * @param tranConn
	 *            事务Connection，需要外部自行commit和release
	 */
	public int[] executeBatchCommand(String cmd, Collection<Object[]> paramsList, Connection tranConn)
			throws DbException {
		if (tranConn == null) {
			throw new NullPointerException("tranConn is null!");
		}
		PreparedStatement stmt = null;
		int[] rowCounts = null;
		try {
			stmt = this.prepareBatchStatement(tranConn, cmd, paramsList);
			rowCounts = stmt.executeBatch();
		} catch (Exception e) {
			try {
				tranConn.rollback();
			} catch (Exception e1) {
			}
			throw new DbException(e.getMessage()
					+ getBatchCmdErrorMsg("executeBatchCommand", cmd, paramsList), e);
		} finally {
			releaseDbResource(null, stmt, null);
		}
		return rowCounts;
	}

	/**
	 * 
	 * @param transacted
	 *            是否起事务
	 */
	public int[] executeBatchCommand(String cmd, Collection<Object[]> paramsList, boolean transacted)
			throws DbException {
		Connection conn = null;
		PreparedStatement stmt = null;
		int[] rowCounts = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(!transacted);

			stmt = this.prepareBatchStatement(conn, cmd, paramsList);
			rowCounts = stmt.executeBatch();
			if (transacted) {
				conn.commit();
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
			throw new DbException(e.getMessage()
					+ getBatchCmdErrorMsg("executeBatchCommand", cmd, paramsList), e);
		} finally {
			releaseDbResource(null, stmt, conn);
		}
		return rowCounts;
	}

	/**
	 * 返回一个批处理命令的预处理语句
	 */
	public PreparedStatement prepareBatchStatement(Connection conn, String cmd,
			Collection<Object[]> paramsList) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(cmd);
		if (paramsList != null && paramsList.size() != 0) {
			for (Object[] params : paramsList) {
				setParams(stmt, params);
				stmt.addBatch();
			}
		}

		return stmt;
	}

	// 为PreparedStatement设置参数
	private void setParams(PreparedStatement stmt, Object[] params) throws SQLException {
		Object o;
		for (int i = 0; i < params.length; i++) {
			o = params[i];
			if (o instanceof Integer) {
				stmt.setInt(i + 1, (Integer) o);
			} else if (o instanceof Short) {
				stmt.setShort(i + 1, (Short) o);
			} else if (o instanceof Long) {
				stmt.setLong(i + 1, (Long) o);
			} else if (o instanceof String) {
				stmt.setString(i + 1, (String) o);
			} else if (o instanceof Date) {
				stmt.setObject(i + 1, o);
			} else if (o instanceof Boolean) {
				stmt.setBoolean(i + 1, (Boolean) o);
			} else if (o instanceof byte[]) {
				stmt.setBytes(i + 1, (byte[]) o);
			} else if (o instanceof Double) {
				stmt.setDouble(i + 1, (Double) o);
			} else if (o instanceof Float) {
				stmt.setFloat(i + 1, (Float) o);
			} else if (o == null) {
				stmt.setNull(i + 1, java.sql.Types.OTHER);
			} else {
				throw new SQLException("Not allowed dataBase data type");
			}
		}
	}

	static <T> ArrayList<T> getObjectListFromResultSet(ResultSet rs, ResultObjectBuilder<T> builder)
			throws DbException {
		if (rs != null) {
			ArrayList<T> objectList = null;
			try {
				objectList = new ArrayList<T>();
				while (rs.next()) {
					objectList.add(builder.build(rs));
				}
			} catch (SQLException e) {
				throw new DbException(e.getMessage(), e);
			}
			return objectList;
		} else {
			return null;
		}
	}

	public static void releaseDbResource(Statement stmt, Connection conn) {
		releaseDbResource(null, stmt, conn);
	}

	/**
	 * 释放结果集、查询语句、数据库连接资源
	 * 
	 * @param rs
	 *            结果集
	 * @param stmt
	 *            语句对象
	 * @param conn
	 *            数据库连接
	 */
	public static void releaseDbResource(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException ex) {
			logError(ex.getMessage(), ex);
		}

		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (SQLException ex) {
			logError(ex.getMessage(), ex);
		}

		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException ex) {
			logError(ex.getMessage(), ex);
		}
	}

	private static void logError(String msg, Throwable t) {
		logger.error(msg, t);	
	}
	
	private static String getQueryCmdErrorMsg(String method, String cmd, Object[] params) {
		return new StringBuilder("!!!<<PjaDbManager>>-").append(method).append(" error [")
				.append(getCmd(cmd, params)).append("]").toString();
	}

	private static String getCmd(String cmd, Object[] params) {
		StringBuilder sb = new StringBuilder();
		sb.append(cmd);
		sb.append("/");
		sb.append(params == null ? "" : Arrays.toString(params));

		return sb.toString();
	}

	private static String getBatchCmdErrorMsg(String method, String cmd, Collection<Object[]> paramsList) {
		return new StringBuilder("!!!<<PjaDbManager>>-").append(method).append(" error [")
				.append(getBatchCmdMsg(cmd, paramsList)).append("]").toString();
	}

	private static String getBatchCmdMsg(String cmd, Collection<Object[]> paramsList) {
		StringBuilder sb = new StringBuilder();
		sb.append(cmd);
		if (paramsList == null) {
			paramsList = new ArrayList<Object[]>();
		}
		for (Object[] params : paramsList) {
			sb.append("/");
			sb.append(params == null ? "" : Arrays.toString(params));
		}
		return sb.toString();
	}

}
