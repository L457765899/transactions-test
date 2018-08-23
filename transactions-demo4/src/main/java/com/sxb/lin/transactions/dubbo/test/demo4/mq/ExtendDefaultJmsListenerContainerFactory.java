package com.sxb.lin.transactions.dubbo.test.demo4.mq;

import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class ExtendDefaultJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory{
	
	private Integer concurrentConsumers;

	private Integer maxConcurrentConsumers;

	@Override
	protected void initializeContainer(DefaultMessageListenerContainer container) {
		super.initializeContainer(container);
		if(concurrentConsumers != null){
			container.setConcurrentConsumers(concurrentConsumers);
		}
		if(maxConcurrentConsumers != null){
			container.setMaxConcurrentConsumers(maxConcurrentConsumers);
		}
	}

	public void setConcurrentConsumers(int concurrentConsumers) {
		this.concurrentConsumers = concurrentConsumers;
	}

	public void setMaxConcurrentConsumers(int maxConcurrentConsumers) {
		this.maxConcurrentConsumers = maxConcurrentConsumers;
	}

}
