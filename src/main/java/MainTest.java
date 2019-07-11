import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author hux
 * @datetime 2019-07-11 18:08
 * @description 测试类
 */
public class MainTest {

    /**
     * 测试写入数据
     */
    private static void writeTest(Connection connection) throws SQLException {
        String writeSql = "insert into test values(100);";
        PreparedStatement ps = connection.prepareStatement(writeSql);
        //执行sql
        ps.executeUpdate();
        ps.close();
        connection.close();
    }

    /**
     * 测试读取数据
     */
    private static void readTest(Connection connection) throws SQLException {
        String readSql = "select * from test;";
        PreparedStatement ps = connection.prepareStatement(readSql);
        //查询返回结果集
        ResultSet resultSet = ps.executeQuery();
        //数据查询到的数据
        while (resultSet.next()) {
            System.out.println("ID: " + resultSet.getInt(1));
        }
        ps.close();
        resultSet.close();
        connection.close();
    }

    public static void main(String[] args) throws SQLException {
        /*
        下面2种方式都会获取到Connection，一般都是用读取Yaml文件的方式
         */
        //读取Yaml文件方式获取数据库Connection（常用）
        final Connection yamlConnection = DbKit.getConnectionByYaml();
        //硬编码方式获取数据库Connection（不常用）
        final Connection hardCodeConnection = DbKit.getConnectionByHardCode();
        //测试读操作：所有的读操作都会从slave库中读取，配置多个slave
        readTest(yamlConnection);
        //测试写操作：所有的写操作都会写入master数据库
        writeTest(yamlConnection);
    }
}
