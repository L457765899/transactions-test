package com.sxb.lin.transactions.dubbo.test.demo4.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;
import javax.transaction.SystemException;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jms.core.JmsTemplate;
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
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.sxb.lin.atomikos.dubbo.mybatis.XASpringManagedTransactionFactory;
import com.sxb.lin.atomikos.dubbo.pool.recover.ConnectionFactoryResource;
import com.sxb.lin.atomikos.dubbo.pool.recover.DataSourceResource;
import com.sxb.lin.atomikos.dubbo.pool.recover.UniqueResource;
import com.sxb.lin.atomikos.dubbo.service.DubboTransactionManagerServiceConfig;
import com.sxb.lin.atomikos.dubbo.service.DubboTransactionManagerServiceProxy;
import com.sxb.lin.atomikos.dubbo.spring.TransactionAttributeSourceProxy;
import com.sxb.lin.atomikos.dubbo.spring.jms.JtaJmsTemplate;
import com.sxb.lin.atomikos.dubbo.tm.DataSourceTransactionManager;


@MapperScan(
	    basePackages = "com.sxb.lin.transactions.dubbo.test.demo3.a.dao",
	    sqlSessionFactoryRef = "sqlSessionFactory"
	)
@Configuration
public class Demo4Config {
	
	public final static String QUEUE_TEST = "mq.queue.test";
	
	public final static String TOPIC_TEST = "mq.topic.test";
	
	public final static ActiveMQQueue DESTINATION_QUEUE_TEST = new ActiveMQQueue(QUEUE_TEST);
	
	public final static ActiveMQTopic DESTINATION_TOPIC_TEST = new ActiveMQTopic(TOPIC_TEST);
	
	public final static String DB_DEMO4_A = "DB-demo4-a";
	
	public final static String MQ = "MQ";
    
    @Bean
    public ActiveMQProperties activeMQProperties(){
    	return new ActiveMQProperties();
    }
    
    @Bean
	@Primary
	@Autowired
    public PooledConnectionFactory pooledJmsConnectionFactory(ActiveMQProperties properties){
    	ActiveMQXAConnectionFactory connectionFactory = new ActiveMQXAConnectionFactory(
    			properties.getUser(),properties.getPassword(),properties.getBrokerUrl());
    	
    	ActiveMQProperties.Pool pool = properties.getPool();
    	PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(connectionFactory);
    	pooledConnectionFactory.setBlockIfSessionPoolIsFull(pool.isBlockIfFull());
		pooledConnectionFactory.setBlockIfSessionPoolIsFullTimeout(pool.getBlockIfFullTimeout());
		pooledConnectionFactory.setCreateConnectionOnStartup(pool.isCreateConnectionOnStartup());
		pooledConnectionFactory.setExpiryTimeout(pool.getExpiryTimeout());
		pooledConnectionFactory.setIdleTimeout(pool.getIdleTimeout());
		pooledConnectionFactory.setMaxConnections(pool.getMaxConnections());
		pooledConnectionFactory.setMaximumActiveSessionPerConnection(pool.getMaximumActiveSessionPerConnection());
		pooledConnectionFactory.setReconnectOnException(pool.isReconnectOnException());
		pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(pool.getTimeBetweenExpirationCheck());
		pooledConnectionFactory.setUseAnonymousProducers(pool.isUseAnonymousProducers());
		return pooledConnectionFactory;
    }
	
	@Autowired
    @Bean(initMethod="init",destroyMethod="close")
    public AtomikosConnectionFactoryBean xaJmsConnectionFactory(ActiveMQProperties properties){
    	ActiveMQXAConnectionFactory connectionFactory = new ActiveMQXAConnectionFactory(
    			properties.getUser(),properties.getPassword(),properties.getBrokerUrl());
    	
    	AtomikosConnectionFactoryBean bean = new AtomikosConnectionFactoryBean();
		bean.setXaConnectionFactory(connectionFactory);
		bean.setMaxPoolSize(properties.getPool().getMaxConnections());
		bean.setUniqueResourceName(MQ);
		return bean;
    }
	
//	@Bean
//	@Primary
//	@Autowired
//	public JmsListenerContainerFactory<?> jmsListenerContainerQueue(
//			@Qualifier("pooledJmsConnectionFactory") ConnectionFactory activeMQConnectionFactory){
//		ExtendDefaultJmsListenerContainerFactory bean = new ExtendDefaultJmsListenerContainerFactory();
//        bean.setConnectionFactory(activeMQConnectionFactory);
//        bean.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
//        bean.setPubSubDomain(false);
//        bean.setConcurrentConsumers(3);
//        bean.setMaxConcurrentConsumers(5);
//        
////        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
////        executor.setCorePoolSize(3);
////        executor.setMaxPoolSize(5);
////        executor.setQueueCapacity(11);
////        executor.setThreadNamePrefix("QueueListener-");
////        executor.initialize();
////        bean.setTaskExecutor(executor);
//        
//        return bean;
//	}
//	
//	@Bean
//	@Autowired
//    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(
//    		@Qualifier("pooledJmsConnectionFactory") ConnectionFactory activeMQConnectionFactory) {
//		ExtendDefaultJmsListenerContainerFactory bean = new ExtendDefaultJmsListenerContainerFactory();
//        bean.setConnectionFactory(activeMQConnectionFactory);
//        bean.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
//        bean.setPubSubDomain(true);
//        return bean;
//    }
	
	@Bean
	@Primary
	@Autowired
	public JmsTemplate jmsTemplate(@Qualifier("pooledJmsConnectionFactory") ConnectionFactory activeMQConnectionFactory){
		JmsTemplate jmsTemplate = new JmsTemplate(activeMQConnectionFactory);
		return jmsTemplate;
	}
	
	@Bean
	@Autowired
	public JtaJmsTemplate jtaJmsTemplate(@Qualifier("xaJmsConnectionFactory") ConnectionFactory connectionFactory,
			@Qualifier("pooledJmsConnectionFactory") ConnectionFactory dubboConnectionFactory){
		JtaJmsTemplate jmsTemplate = new JtaJmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory);
		jmsTemplate.setDubboUniqueResourceName(MQ);
		jmsTemplate.setDubboConnectionFactory(dubboConnectionFactory);
		return jmsTemplate;
	}
	
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
        bean.setTransactionFactory(new XASpringManagedTransactionFactory(DB_DEMO4_A));
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
			TransactionInterceptor transactionInterceptor,@Qualifier("pooledJmsConnectionFactory") ConnectionFactory connectionFactory){
    	
    	TransactionAttributeSource transactionAttributeSource = transactionInterceptor.getTransactionAttributeSource();
    	TransactionAttributeSourceProxy transactionAttributeSourceProxy = new TransactionAttributeSourceProxy();
    	transactionAttributeSourceProxy.setTransactionAttributeSource(transactionAttributeSource);
    	transactionInterceptor.setTransactionAttributeSource(transactionAttributeSourceProxy);
    	
		Map<String,UniqueResource> dataSourceMapping = new HashMap<String, UniqueResource>();
		dataSourceMapping.put(DB_DEMO4_A, new DataSourceResource(DB_DEMO4_A, dataSource));
		dataSourceMapping.put(MQ, new ConnectionFactoryResource(MQ, connectionFactory));
		
		Set<String> excludeResourceNames = new HashSet<>();
		excludeResourceNames.add(MQ);
		
		DubboTransactionManagerServiceConfig config = new DubboTransactionManagerServiceConfig();
		config.setApplicationConfig(applicationConfig);
		config.setRegistryConfig(registryConfig);
		config.setProtocolConfig(protocolConfig);
		config.setProviderConfig(providerConfig);
		config.setConsumerConfig(consumerConfig);
		config.setUniqueResourceMapping(dataSourceMapping);
		config.setExcludeResourceNames(excludeResourceNames);
		
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
