package com.sxb.lin.transactions.dubbo.test.demo4.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.sql.DataSource;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import com.alibaba.druid.pool.xa.DruidXADataSource;


@MapperScan(
	    basePackages = "com.sxb.lin.transactions.dubbo.test.demo3.a.dao",
	    sqlSessionFactoryRef = "sqlSessionFactory"
	)
@Configuration
public class Demo4Config {

	@Bean
	@Primary
	public Queue normalQueue(){
		return new ActiveMQQueue("mq.queue.normal.test");
	}
	
	@Bean
	public Queue transactionQueue(){
		return new ActiveMQQueue("mq.queue.transaction.test");
	}
	
	@Bean
	@Primary
	public Topic normalTopic(){
		return new ActiveMQTopic("mq.topic.normal.test");
	}
	
	@Bean
	public Topic transactionTopic(){
		return new ActiveMQTopic("mq.topic.transaction.test");
	}
	
	@Bean
	@Primary
	@Autowired
	public JmsListenerContainerFactory<?> normalJmsListenerContainerQueue(ConnectionFactory activeMQConnectionFactory){
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(activeMQConnectionFactory);
        return bean;
	}
	
	@Bean
	@Autowired
    public JmsListenerContainerFactory<?> normalJmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(activeMQConnectionFactory);
        return bean;
    }
	
//	@Bean
//	@Autowired
//	public JmsListenerContainerFactory<?> transactionJmsListenerContainerQueue(ConnectionFactory activeMQConnectionFactory){
//		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
//        bean.setConnectionFactory(activeMQConnectionFactory);
//        return bean;
//	}
//	
//	@Bean
//	@Autowired
//	public JmsListenerContainerFactory<?> transactionJmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
//        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
//        bean.setPubSubDomain(true);
//        bean.setConnectionFactory(activeMQConnectionFactory);
//        return bean;
//    }
	
	
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
