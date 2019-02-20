package com.sxb.lin.transactions.dubbo.test.demo2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

@Component
public class ServiceExport {

    @Autowired
    private ApplicationConfig applicationConfig;
    
    @Autowired
    private RegistryConfig registryConfig;
    
    @Autowired
    private ProtocolConfig protocolConfig;
    
    @Autowired
    private ProviderConfig providerConfig;
    
    public <T> void export(ServiceConfig<T> serviceConfig){
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setProtocol(protocolConfig);
        serviceConfig.setProvider(providerConfig);
        serviceConfig.export();
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    public ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }

    public void setProtocolConfig(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }

    public ProviderConfig getProviderConfig() {
        return providerConfig;
    }

    public void setProviderConfig(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }
    
}
