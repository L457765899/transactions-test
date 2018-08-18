package com.sxb.lin.transactions.dubbo.test.demo3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;

@Configuration
@DubboComponentScan("com.sxb.lin.transactions.dubbo.test.demo3.service.impl")
public class DubboConfig {
	
	@Value("${dubbo.application.name}")
	private String applicationName;
    
    @Value("${dubbo.zookeeper.address}")
    private String address;
    
    @Value("${dubbo.provider.token}")
    private String token;
    
    @Value("${dubbo.registry.file}")
    private String file;
    
    @Value("${dubbo.protocol.port}")
    private Integer port;
    
    @Value("${dubbo.protocol.name}")
    private String protocolName;
    
    @Value("${dubbo.protocol.dispatcher}")
    private String dispatcher;
    
    @Value("${dubbo.provider.timeout}")
    private Integer timeout;
    
    @Value("${dubbo.provider.threads}")
    private Integer threads;
    
    @Value("${dubbo.provider.loadbalance}")
    private String loadbalance;
    
    @Value("${dubbo.provider.filter}")
    private String providerFilter;
    
    @Value("${dubbo.consumer.filter}")
    private String consumerFilter;

    @Bean
    public ApplicationConfig applicationConfig(){
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName);
        applicationConfig.setQosEnable(false);
        return applicationConfig;
    }
    
    @Bean
    public RegistryConfig registryConfig(){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(address);
        registryConfig.setFile(file);
        return registryConfig;
    }
    
    @Bean
    public ProtocolConfig protocolConfig(){
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setPort(port);
        protocolConfig.setName(protocolName);
        protocolConfig.setDispatcher(dispatcher);
        return protocolConfig;
    }
    
    /**
     * 消费者未加版本号--能调用未加版本号的提供者--不能调用加了版本号的提供者
     * 消费者加了版本号--能调用加了版本号的提供者--不能调用未加版本号的提供者
     * 消费者加了版本号，提供者也加了版本号--消费者和提供者版本号必须一致才能调用
     * @return
     */
    @Bean
    public ProviderConfig providerConfig(){
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setTimeout(timeout);
        providerConfig.setThreads(threads);
        providerConfig.setLoadbalance(loadbalance);
        providerConfig.setToken(token);
        providerConfig.setFilter(providerFilter);
        return providerConfig;
    }
    
    @Bean
    public ConsumerConfig consumerConfig(){
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setCheck(false);
        consumerConfig.setFilter(consumerFilter);
        return consumerConfig;
    }
}
