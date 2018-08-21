package com.sxb.lin.transactions.dubbo.test.demo4.mq;

import javax.annotation.Resource;
import javax.jms.Queue;
import javax.jms.Topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class JMSProducer {

	@Autowired
    private JmsTemplate jmsTemplate;
	
	@Resource(name = "normalQueue")
	private Queue normalQueue;
	
	@Resource(name = "transactionQueue")
	private Queue transactionQueue;
	
	@Resource(name = "normalTopic")
	private Topic normalTopic;
	
	@Resource(name = "transactionTopic")
	private Topic transactionTopic;
	
	
	public void sendQueueNormalTest(String message){
		jmsTemplate.convertAndSend(normalQueue, message);
	}
	
	public void sendQueueTransactionTest(String message){
		
	}
	
	public void sendTopicNormalTest(String message){
		jmsTemplate.convertAndSend(normalTopic, message);
	}
	
	public void sendTopicTransactionTest(String message){
		
	}
}
