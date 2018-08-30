package com.sxb.lin.transactions.dubbo.test.demo5.config;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.SystemException;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;


@MapperScan(
	    basePackages = "com.sxb.lin.transactions.dubbo.test.demo3.a.dao",
	    sqlSessionFactoryRef = "sqlSessionFactory"
	)
@Configuration
public class Demo5Config {
	
	public final static String DB_DEMO5_A = "DB-demo5-a";
	
//	private Properties getConnectProperties(){
//		Properties connectProperties = new Properties();
//		connectProperties.put("characterEncoding", "utf-8");
//		connectProperties.put("allowMultiQueries", "true");
//		connectProperties.put("tinyInt1isBit", "false");
//		connectProperties.put("zeroDateTimeBehavior", "convertToNull");
//		connectProperties.put("useSSL", "false");
//		connectProperties.put("pinGlobalTxToPhysicalConnection", "true");
//		return connectProperties;
//	}
//	
//	@Bean(initMethod = "init",destroyMethod = "close")
//	public DataSource dataSource() throws SQLException{
//		
//		DruidXADataSource druidXADataSource = new DruidXADataSource();
//        druidXADataSource.setInitialSize(5);
//        druidXADataSource.setMaxActive(10);
//        druidXADataSource.setMinIdle(3);
//        druidXADataSource.setMaxWait(60000);
//        druidXADataSource.setValidationQuery("SELECT 1");
//        druidXADataSource.setTestOnBorrow(false);
//        druidXADataSource.setTestOnReturn(false);
//        druidXADataSource.setTestWhileIdle(true);
//        druidXADataSource.setTimeBetweenEvictionRunsMillis(30000);
//        druidXADataSource.setMinEvictableIdleTimeMillis(600000);
//        druidXADataSource.setFilters("mergeStat");
//        druidXADataSource.setConnectProperties(this.getConnectProperties());
//        druidXADataSource.setUrl("jdbc:mysql://192.168.0.252:3306/demo3-a");
//        druidXADataSource.setUsername("root");
//        druidXADataSource.setPassword("Sxb889961");
//        
//        return druidXADataSource;
//	}
	
	@Primary
	@Bean(initMethod = "init",destroyMethod = "close")
	public DataSource dataSource() throws SQLException {
		AtomikosNonXADataSourceBean bean = new AtomikosNonXADataSourceBean();
		bean.setUniqueResourceName(DB_DEMO5_A);
		bean.setPoolSize(5);
		bean.setMinPoolSize(3);
		bean.setMaxPoolSize(10);
		bean.setBorrowConnectionTimeout(60);
		bean.setMaxIdleTime(1800);
		bean.setMaintenanceInterval(30);
		bean.setLoginTimeout(3600);
		bean.setTestQuery("SELECT 1");
		bean.setDriverClassName("com.mysql.jdbc.Driver");
		bean.setUrl("jdbc:mysql://192.168.0.252:3306/demo3-a");
		bean.setUser("root");
		bean.setPassword("Sxb889961");
		return bean;
	}
	
	@Primary
	@Bean
	@Autowired
    public SqlSessionFactoryBean sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws IOException{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();  
        bean.setDataSource(dataSource);
        bean.setMapperLocations(resolver.getResources("classpath:com/sxb/lin/transactions/dubbo/test/demo3/a/mapping/*.xml"));
        return bean;
    }
	
	@Bean(initMethod="init",destroyMethod="close")
    public UserTransactionManager userTransactionManager(){
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        return userTransactionManager;
    }
    
    @Bean
    public UserTransactionImp userTransactionImp() throws SystemException{
        UserTransactionImp userTransaction = new UserTransactionImp();
        return userTransaction;
    }

    @Bean
    @Autowired
    public JtaTransactionManager jtaTransactionManager(UserTransactionManager userTransactionManager,
            UserTransactionImp userTransaction){
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setUserTransaction(userTransaction);
        jtaTransactionManager.setTransactionManager(userTransactionManager);
        return jtaTransactionManager;
    }
	
}
