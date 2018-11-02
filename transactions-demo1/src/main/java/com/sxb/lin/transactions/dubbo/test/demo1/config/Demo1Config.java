package com.sxb.lin.transactions.dubbo.test.demo1.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;
import javax.transaction.SystemException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.sxb.lin.atomikos.dubbo.pool.recover.ConnectionFactoryResource;
import com.sxb.lin.atomikos.dubbo.pool.recover.DataSourceResource;
import com.sxb.lin.atomikos.dubbo.pool.recover.UniqueResource;
import com.sxb.lin.atomikos.dubbo.service.DubboTransactionManagerServiceConfig;
import com.sxb.lin.atomikos.dubbo.service.DubboTransactionManagerServiceProxy;
import com.sxb.lin.atomikos.dubbo.spring.TransactionAttributeSourceProxy;
import com.sxb.lin.atomikos.dubbo.tm.JtaTransactionManager;

@Configuration
public class Demo1Config {
	
	@Bean(destroyMethod = "destory")
	@Autowired
	public DubboTransactionManagerServiceProxy dubboTransactionManagerServiceProxy(
			ApplicationConfig applicationConfig, RegistryConfig registryConfig, 
			ProtocolConfig protocolConfig, ProviderConfig providerConfig, ConsumerConfig consumerConfig,
			@Qualifier("dataSource1") DataSource ds1, @Qualifier("dataSource2") DataSource ds2,
			@Qualifier("pooledJmsConnectionFactory") ConnectionFactory connectionFactory,
			TransactionInterceptor transactionInterceptor){
		
		TransactionAttributeSource transactionAttributeSource = transactionInterceptor.getTransactionAttributeSource();
    	TransactionAttributeSourceProxy transactionAttributeSourceProxy = new TransactionAttributeSourceProxy();
    	transactionAttributeSourceProxy.setTransactionAttributeSource(transactionAttributeSource);
    	transactionInterceptor.setTransactionAttributeSource(transactionAttributeSourceProxy);
		
		Map<String,UniqueResource> dataSourceMapping = new HashMap<String, UniqueResource>();
		dataSourceMapping.put(AConfig.DB_DEMO1_A, new DataSourceResource(AConfig.DB_DEMO1_A, ds1));
		dataSourceMapping.put(BConfig.DB_DEMO1_B, new DataSourceResource(BConfig.DB_DEMO1_B, ds2));
		dataSourceMapping.put(MQConfig.MQ, new ConnectionFactoryResource(MQConfig.MQ, connectionFactory));
		
		Set<String> excludeResourceNames = new HashSet<>();
		excludeResourceNames.add(AConfig.DB_DEMO1_A);
		excludeResourceNames.add(BConfig.DB_DEMO1_B);
		excludeResourceNames.add(MQConfig.MQ);
		
		DubboTransactionManagerServiceConfig config = new DubboTransactionManagerServiceConfig();
		config.setApplicationConfig(applicationConfig);
		config.setRegistryConfig(registryConfig);
		config.setProtocolConfig(protocolConfig);
		config.setProviderConfig(providerConfig);
		config.setConsumerConfig(consumerConfig);
		config.setUniqueResourceMapping(dataSourceMapping);
		config.setExcludeResourceNames(excludeResourceNames);
		//config.setServiceDispatcher("xa_all");
		//config.setServiceLoadbalance("sticky_roundrobin");
		
		DubboTransactionManagerServiceProxy instance = DubboTransactionManagerServiceProxy.getInstance();
		instance.init(config);
		
		return instance;
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
