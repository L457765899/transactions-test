package com.sxb.lin.transactions.dubbo.test.demo1.config;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.sxb.lin.atomikos.dubbo.spring.jms.ExtendDefaultJmsListenerContainerFactory;
import com.sxb.lin.atomikos.dubbo.spring.jms.JtaJmsTemplate;

@Configuration
public class MQConfig {
	
	public final static String QUEUE_TEST = "mq.queue.test";
	
	public final static String TOPIC_TEST = "mq.topic.test";
	
	public final static ActiveMQQueue DESTINATION_QUEUE_TEST = new ActiveMQQueue(QUEUE_TEST);
	
	public final static ActiveMQTopic DESTINATION_TOPIC_TEST = new ActiveMQTopic(TOPIC_TEST);
	
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
        return bean;
    }
	
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
}
