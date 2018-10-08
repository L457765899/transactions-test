package com.sxb.lin.transactions.dubbo.test.demo2.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.sxb.lin.atomikos.dubbo.mybatis.XASpringManagedTransactionFactory;

@MapperScan(
    basePackages = "com.sxb.lin.transactions.dubbo.test.demo2.b.dao",
    sqlSessionFactoryRef = "sqlSessionFactory2"
)
@Configuration
public class BConfig {
	
	public final static String DB_DEMO2_B = "DB-demo2-b";
	
	private Properties getConnectProperties(){
		Properties connectProperties = new Properties();
		connectProperties.put("characterEncoding", "utf-8");
		connectProperties.put("allowMultiQueries", "true");
		connectProperties.put("tinyInt1isBit", "false");
		connectProperties.put("zeroDateTimeBehavior", "convertToNull");
		connectProperties.put("useSSL", "false");
		connectProperties.put("pinGlobalTxToPhysicalConnection", "true");
		return connectProperties;
	}

	/*CREATE TABLE `t2` (
	`id`  int NOT NULL AUTO_INCREMENT ,
	`name`  varchar(64) NULL ,
	`time`  bigint NOT NULL ,
	PRIMARY KEY (`id`)
	);*/
	@Bean(initMethod = "init",destroyMethod = "close")
	public DataSource dataSource2() throws SQLException{
		
		DruidXADataSource druidXADataSource = new DruidXADataSource();
        druidXADataSource.setInitialSize(5);
        druidXADataSource.setMaxActive(10);
        druidXADataSource.setMinIdle(3);
        druidXADataSource.setMaxWait(60000);
        druidXADataSource.setValidationQuery("SELECT 1");
        druidXADataSource.setTestOnBorrow(false);
        druidXADataSource.setTestOnReturn(false);
        druidXADataSource.setTestWhileIdle(true);
        druidXADataSource.setTimeBetweenEvictionRunsMillis(30000);
        druidXADataSource.setMinEvictableIdleTimeMillis(600000);
        druidXADataSource.setFilters("mergeStat");
        druidXADataSource.setConnectProperties(this.getConnectProperties());
        druidXADataSource.setUrl("jdbc:mysql://192.168.0.252:3306/demo2-b");
        druidXADataSource.setUsername("demo");
        druidXADataSource.setPassword("123456");
        
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setUniqueResourceName(DB_DEMO2_B);
        atomikosDataSourceBean.setPoolSize(5);
        atomikosDataSourceBean.setMinPoolSize(3);
        atomikosDataSourceBean.setMaxPoolSize(10);
        atomikosDataSourceBean.setBorrowConnectionTimeout(60);
        atomikosDataSourceBean.setMaxIdleTime(1800);
        atomikosDataSourceBean.setMaintenanceInterval(30);
        atomikosDataSourceBean.setLoginTimeout(3600);
        atomikosDataSourceBean.setTestQuery("SELECT 1");
        atomikosDataSourceBean.setXaDataSource(druidXADataSource);
        
        return atomikosDataSourceBean;
	}
	
	@Bean
    public SqlSessionFactoryBean sqlSessionFactory2(@Qualifier("dataSource2") DataSource ds2) throws IOException{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();  
        bean.setDataSource(ds2);
        bean.setMapperLocations(resolver.getResources("classpath:com/sxb/lin/transactions/dubbo/test/demo2/b/mapping/*.xml"));
        bean.setTransactionFactory(new XASpringManagedTransactionFactory(DB_DEMO2_B));
        return bean;
    }
}
