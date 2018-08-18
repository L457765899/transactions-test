package com.sxb.lin.transactions.dubbo.test.demo2.config;

import java.util.HashMap;
import java.util.Map;

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
		DubboTransactionManagerServiceProxy instance = DubboTransactionManagerServiceProxy.getInstance();
		Map<String,DataSource> dataSourceMapping = new HashMap<String, DataSource>();
		dataSourceMapping.put(AConfig.DB_DEMO2_A, ds1);
		dataSourceMapping.put(BConfig.DB_DEMO2_B, ds2);
		instance.init(applicationConfig, registryConfig, protocolConfig, providerConfig, consumerConfig, dataSourceMapping);
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
