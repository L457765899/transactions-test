package com.sxb.lin.transactions.dubbo.test.demo5.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.transaction.SystemException;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import com.sxb.lin.atomikos.dubbo.pool.recover.DataSourceResource;
import com.sxb.lin.atomikos.dubbo.pool.recover.UniqueResource;
import com.sxb.lin.atomikos.dubbo.rocketmq.MQProducerFor2PC;
import com.sxb.lin.atomikos.dubbo.rocketmq.TransactionListenerImpl;
import com.sxb.lin.atomikos.dubbo.service.DubboTransactionManagerServiceProxy;
import com.sxb.lin.atomikos.dubbo.tm.JtaTransactionManager;
import com.sxb.lin.transactions.dubbo.test.demo4.util.RetUtil;


@MapperScan(
	    basePackages = "com.sxb.lin.transactions.dubbo.test.demo3.a.dao",
	    sqlSessionFactoryRef = "sqlSessionFactory"
	)
@Configuration
public class Demo5Config {
	
	public final static String TOPIC_TEST = "mq_topic_test";
	
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
	public DubboTransactionManagerServiceProxy dubboTransactionManagerServiceProxy(
			ApplicationConfig applicationConfig, RegistryConfig registryConfig, 
			ProtocolConfig protocolConfig, ProviderConfig providerConfig, ConsumerConfig consumerConfig,
			@Qualifier("dataSource2") DataSource ds2){
		
		Map<String,UniqueResource> dataSourceMapping = new HashMap<String, UniqueResource>();
		dataSourceMapping.put(BConfig.DB_DEMO1_B, new DataSourceResource(BConfig.DB_DEMO1_B, ds2));
		
		Set<String> excludeResourceNames = new HashSet<>();
		excludeResourceNames.add(BConfig.DB_DEMO1_B);
		
		DubboTransactionManagerServiceProxy instance = DubboTransactionManagerServiceProxy.getInstance();
		instance.init(applicationConfig, registryConfig, protocolConfig, 
				providerConfig, consumerConfig, dataSourceMapping, excludeResourceNames);
		
		return instance;
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
	
    @Primary
    @Bean(initMethod="start",destroyMethod="shutdown")
    public TransactionMQProducer defaultProducer(){
    	TransactionMQProducer producer = new TransactionMQProducer("producer_test");
    	producer.setNamesrvAddr("192.168.0.252:9876");
    	producer.setTransactionListener(new TransactionListener() {
			@Override
			public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
				System.out.println("本地事务完成。");
				return LocalTransactionState.COMMIT_MESSAGE;
			}
			
			@Override
			public LocalTransactionState checkLocalTransaction(MessageExt msg) {
				System.out.println(RetUtil.getDatetime() + "事务消息检查。" + msg.toString());
				//return LocalTransactionState.COMMIT_MESSAGE;
				return LocalTransactionState.UNKNOW;
			}
		});
    	return producer;
    }
    
    @Bean(initMethod="start",destroyMethod="shutdown")
    public MQProducerFor2PC producerFor2PC(){
    	MQProducerFor2PC producer = new MQProducerFor2PC("producer_test_2PC");
    	producer.setNamesrvAddr("192.168.0.252:9876");
    	producer.setTransactionListener(new TransactionListenerImpl());
		return producer;
    }
    
    @Bean(initMethod="start",destroyMethod="shutdown")
    public DefaultMQPushConsumer defaultConsumer() throws MQClientException {
    	DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_test");
    	consumer.setNamesrvAddr("192.168.0.252:9876");
    	consumer.subscribe(TOPIC_TEST, "*");
    	consumer.registerMessageListener(new MessageListenerConcurrently(){

			@Override
			public ConsumeConcurrentlyStatus consumeMessage(
					List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				for(MessageExt msg : msgs){
					System.out.println(RetUtil.getDatetime() + "消费成功。" + msg.toString());
					System.out.println(new String(msg.getBody()));
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
    		
    	});
    	return consumer;
    }
}
