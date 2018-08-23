package com.sxb.lin.transactions.dubbo.test.demo4.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.sql.DataSource;
import javax.transaction.SystemException;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.sxb.lin.transactions.dubbo.test.demo4.mq.ExtendDefaultJmsListenerContainerFactory;


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
	@Primary
	@Autowired
	public JmsListenerContainerFactory<?> jmsListenerContainerQueue(
			@Qualifier("pooledJmsConnectionFactory") ConnectionFactory activeMQConnectionFactory){
		ExtendDefaultJmsListenerContainerFactory bean = new ExtendDefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(activeMQConnectionFactory);
        bean.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        bean.setPubSubDomain(false);
        bean.setConcurrentConsumers(3);
        bean.setMaxConcurrentConsumers(5);
        
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(3);
//        executor.setMaxPoolSize(5);
//        executor.setQueueCapacity(11);
//        executor.setThreadNamePrefix("QueueListener-");
//        executor.initialize();
//        bean.setTaskExecutor(executor);
        
        return bean;
	}
	
	@Bean
	@Autowired
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(
    		@Qualifier("pooledJmsConnectionFactory") ConnectionFactory activeMQConnectionFactory) {
		ExtendDefaultJmsListenerContainerFactory bean = new ExtendDefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(activeMQConnectionFactory);
        bean.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        bean.setPubSubDomain(true);
        bean.setConcurrentConsumers(3);
        bean.setMaxConcurrentConsumers(5);
        
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(3);
//        executor.setMaxPoolSize(5);
//        executor.setQueueCapacity(11);
//        executor.setThreadNamePrefix("TopicListener-");
//        executor.initialize();
//        bean.setTaskExecutor(executor);
        
        return bean;
    }
	
	@Bean
	@Primary
	@Autowired
	public JmsTemplate jmsTemplate(@Qualifier("pooledJmsConnectionFactory") ConnectionFactory activeMQConnectionFactory){
		JmsTemplate jmsTemplate = new JmsTemplate(activeMQConnectionFactory);
		return jmsTemplate;
	}
	
//	@Bean
//	@Autowired
//	public JmsTemplate jtaJmsTemplate(@Qualifier("jmsConnectionFactory") ConnectionFactory activeMQConnectionFactory){
//		JmsTemplate jmsTemplate = new JmsTemplate(activeMQConnectionFactory);
//		return jmsTemplate;
//	}
	
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
        druidXADataSource.setUsername("root");
        druidXADataSource.setPassword("Sxb889961");
        
        return druidXADataSource;
	}
	
	@Bean
	@Autowired
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();  
        bean.setDataSource(dataSource);
        bean.setMapperLocations(resolver.getResources("classpath:com/sxb/lin/transactions/dubbo/test/demo3/a/mapping/*.xml"));
        return bean;
    }
	
}
