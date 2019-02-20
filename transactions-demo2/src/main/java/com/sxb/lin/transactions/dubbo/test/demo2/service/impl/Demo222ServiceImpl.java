package com.sxb.lin.transactions.dubbo.test.demo2.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.sxb.lin.transactions.dubbo.test.demo2.config.ServiceExport;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo222Service;

@Service
public class Demo222ServiceImpl implements Demo222Service,ApplicationContextAware,InitializingBean{
	
	private ApplicationContext applicationContext;
	
	@Autowired
	private ServiceExport serviceExport;

	@Override
	@Transactional
	public boolean test() {
		System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
		System.out.println("调用前");
		try {
			Thread.sleep(3 * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("调用后");
		return false;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Demo222Service bean = applicationContext.getBean(Demo222Service.class);
		
		List<MethodConfig> methods = new ArrayList<>();
	    MethodConfig test = new MethodConfig();
	    test.setName("test");
	    test.setAsync(true);
	    test.setRetries(5);
	    methods.add(test);
	    
	    ServiceConfig<Demo222Service> serviceConfig = new ServiceConfig<>();
	    serviceConfig.setInterface(Demo222Service.class);
	    serviceConfig.setRef(bean);
	    serviceConfig.setMethods(methods);
	    serviceExport.export(serviceConfig);
	}

}
