package com.sxb.lin.transactions.dubbo.test.demo3.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.SystemException;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.sxb.lin.atomikos.dubbo.mybatis.XASpringManagedTransactionFactory;
import com.sxb.lin.atomikos.dubbo.pool.recover.DataSourceResource;
import com.sxb.lin.atomikos.dubbo.pool.recover.UniqueResource;
import com.sxb.lin.atomikos.dubbo.service.DubboTransactionManagerServiceConfig;
import com.sxb.lin.atomikos.dubbo.service.DubboTransactionManagerServiceProxy;
import com.sxb.lin.atomikos.dubbo.spring.TransactionAttributeSourceProxy;
import com.sxb.lin.atomikos.dubbo.tm.DataSourceTransactionManager;


@MapperScan(
    basePackages = "com.sxb.lin.transactions.dubbo.test.demo3.a.dao",
    sqlSessionFactoryRef = "sqlSessionFactory"
)
@Configuration
public class Demo3Config {
	
	public final static String DB_DEMO3_A = "DB-demo3-a";
	
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
	
	/*CREATE TABLE `t1` (
		`id`  int NOT NULL AUTO_INCREMENT ,
		`name`  varchar(64) NULL ,
		`time`  bigint NOT NULL ,
		PRIMARY KEY (`id`)
	);*/
	@Bean(initMethod = "init",destroyMethod = "close")
	public DataSource dataSource() throws SQLException{
		
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
        druidXADataSource.setUrl("jdbc:mysql://192.168.0.252:3306/demo3-a");
        druidXADataSource.setUsername("demo");
        druidXADataSource.setPassword("123456");
        
        return druidXADataSource;
	}
	
	@Bean
	@Autowired
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();  
        bean.setDataSource(dataSource);
        bean.setMapperLocations(resolver.getResources("classpath:com/sxb/lin/transactions/dubbo/test/demo3/a/mapping/*.xml"));
        bean.setTransactionFactory(new XASpringManagedTransactionFactory(DB_DEMO3_A));
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
	public DubboTransactionManagerServiceProxy dubboTransactionManagerServiceProxy(
			ApplicationConfig applicationConfig,RegistryConfig registryConfig,ProtocolConfig protocolConfig,
			ProviderConfig providerConfig,ConsumerConfig consumerConfig,DataSource dataSource,
			TransactionInterceptor transactionInterceptor){
    	
    	TransactionAttributeSource transactionAttributeSource = transactionInterceptor.getTransactionAttributeSource();
    	TransactionAttributeSourceProxy transactionAttributeSourceProxy = new TransactionAttributeSourceProxy();
    	transactionAttributeSourceProxy.setTransactionAttributeSource(transactionAttributeSource);
    	transactionInterceptor.setTransactionAttributeSource(transactionAttributeSourceProxy);
    	
		Map<String,UniqueResource> dataSourceMapping = new HashMap<String, UniqueResource>();
		dataSourceMapping.put(DB_DEMO3_A, new DataSourceResource(DB_DEMO3_A, dataSource));
		
		DubboTransactionManagerServiceConfig config = new DubboTransactionManagerServiceConfig();
		config.setApplicationConfig(applicationConfig);
		config.setRegistryConfig(registryConfig);
		config.setProtocolConfig(protocolConfig);
		config.setProviderConfig(providerConfig);
		config.setConsumerConfig(consumerConfig);
		config.setUniqueResourceMapping(dataSourceMapping);
		//config.setServiceDispatcher("xa_all");
		//config.setServiceLoadbalance("sticky_roundrobin");
		
		DubboTransactionManagerServiceProxy instance = DubboTransactionManagerServiceProxy.getInstance();
		instance.init(config);
		
		return instance;
	}
    
    @Bean
	@Autowired
    public DataSourceTransactionManager dataSourceTransactionManager(UserTransactionManager userTransactionManager,
            UserTransactionImp userTransaction,DataSource dataSource){
    	DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
    	dataSourceTransactionManager.setDataSource(dataSource);
    	dataSourceTransactionManager.setUserTransaction(userTransaction);
    	dataSourceTransactionManager.setTransactionManager(userTransactionManager);
    	return dataSourceTransactionManager;
    }
}
