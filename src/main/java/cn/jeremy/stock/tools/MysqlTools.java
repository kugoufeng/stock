package cn.jeremy.stock.tools;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlTools
{
    private static final String url = "jdbc:mysql://192.168.109.131:3306/stock?useSSL=false&characterEncoding=utf8";

    private static final String username = "kugoufeng";

    private static final String password = "fjtblt003";

    public static Connection getConnection()
    {
        // 加载驱动类
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            //获取connection对象
            return DriverManager.getConnection(url, username, password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet executeQuery(Connection connection, String sql)
        throws SQLException
    {
        CallableStatement callableStatement = connection.prepareCall(sql);
        return callableStatement.executeQuery();
    }

    public static int executeUpdate(Connection connection, String sql)
        throws SQLException
    {
        CallableStatement callableStatement = connection.prepareCall(sql);
        return callableStatement.executeUpdate();
    }

    public static int executeDelete(Connection connection, String sql)
        throws SQLException
    {
        return executeUpdate(connection, sql);
    }

    public static int executeInsert(Connection connection, String sql)
        throws SQLException
    {
        return executeUpdate(connection, sql);
    }
}
