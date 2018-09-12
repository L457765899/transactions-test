package com.sxb.lin.transactions.dubbo.test.demo2.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.transaction.SystemException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.sxb.lin.atomikos.dubbo.pool.recover.DataSourceResource;
import com.sxb.lin.atomikos.dubbo.pool.recover.UniqueResource;
import com.sxb.lin.atomikos.dubbo.rocketmq.MQProducerFor2PC;
import com.sxb.lin.atomikos.dubbo.rocketmq.TransactionListenerImpl;
import com.sxb.lin.atomikos.dubbo.service.DubboTransactionManagerServiceConfig;
import com.sxb.lin.atomikos.dubbo.service.DubboTransactionManagerServiceProxy;
import com.sxb.lin.atomikos.dubbo.tm.JtaTransactionManager;

@Configuration
public class Demo2Config {
	
	@Bean
	@Autowired
	public DubboTransactionManagerServiceProxy dubboTransactionManagerServiceProxy(
			ApplicationConfig applicationConfig,RegistryConfig registryConfig,ProtocolConfig protocolConfig,
			ProviderConfig providerConfig,ConsumerConfig consumerConfig,@Qualifier("dataSource1") DataSource ds1,
			@Qualifier("dataSource2") DataSource ds2){
		
		Map<String,UniqueResource> dataSourceMapping = new HashMap<String, UniqueResource>();
		dataSourceMapping.put(AConfig.DB_DEMO2_A, new DataSourceResource(AConfig.DB_DEMO2_A, ds1));
		dataSourceMapping.put(BConfig.DB_DEMO2_B, new DataSourceResource(BConfig.DB_DEMO2_B, ds2));
		
		Set<String> excludeResourceNames = new HashSet<>();
		excludeResourceNames.add(AConfig.DB_DEMO2_A);
		excludeResourceNames.add(BConfig.DB_DEMO2_B);
		
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
    
    @Bean(initMethod="start",destroyMethod="shutdown")
    public MQProducerFor2PC producerFor2PC(){
    	MQProducerFor2PC producer = new MQProducerFor2PC("producer_test_2PC");
    	producer.setNamesrvAddr("192.168.0.252:9876");
    	producer.setTransactionListener(new TransactionListenerImpl());
		return producer;
    }
}
