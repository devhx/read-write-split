import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.yaml.YamlMasterSlaveDataSourceFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author hux
 * @datetime 2019-07-11 19:54
 * @description 获取读写分离的数据库Connection。
 */
class DbKit {

    /***
     * 获取读写分离的Connection，通过yaml文件
     * @return Connection
     */
    static Connection getConnectionByYaml() {
        Connection connection = null;
        try {
            //sharding-jdbc yaml文件对象
            final File file = new File((System.getProperty("user.dir") + "\\src\\main\\resources\\sharding-jdbc.yml"));
            //创建数据源
            final DataSource dataSource = YamlMasterSlaveDataSourceFactory.createDataSource(file);
            //从数据源中获取Connection
            connection = dataSource.getConnection();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 获取读写分离的Connection，硬编码方式（不推荐使用）
     *
     * @return Connection
     */
    static Connection getConnectionByHardCode() {
        //存放数据源的Map
        Map<String, DataSource> dataSourceMap = new HashMap<>(3);
        //主数据源（写）
        HikariConfig hikariMaster = new HikariConfig();
        hikariMaster.setJdbcUrl("jdbc:mysql://127.0.0.1/t_master?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false");
        hikariMaster.setUsername("root");
        hikariMaster.setPassword("tiger123");
        hikariMaster.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //从数据源（读）
        HikariConfig hikariSlave = new HikariConfig();
        hikariMaster.setJdbcUrl("jdbc:mysql://127.0.0.1/t_master?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false");
        hikariSlave.setUsername("root");
        hikariSlave.setPassword("tiger123");
        hikariSlave.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //将读写的数据源都添加到map中
        dataSourceMap.put("ds_slave", new HikariDataSource(hikariSlave));
        dataSourceMap.put("ds_master", new HikariDataSource(hikariMaster));
        //配置读写分离规则
        MasterSlaveRuleConfiguration msrc = new MasterSlaveRuleConfiguration("ds_read_write", "ds_master", Collections.singletonList("ds_slave"));
        Connection connection = null;
        try {
            //创建数据源
            DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, msrc, new Properties());
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("创建数据源失败");
        }
        return connection;
    }

}
