package com.sxb.lin.transactions.dubbo.test.demo4.mq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.sxb.lin.transactions.dubbo.test.demo4.config.Demo4Config;

@Component
public class JMSConsumer {

	@JmsListener(destination = Demo4Config.QUEUE_TEST,containerFactory = "jmsListenerContainerQueue")
	public void receiveQueueNormalTest(String msg){
		System.out.println("收到一条queue信息:" + msg);
		throw new RuntimeException();
	}
	
	@JmsListener(destination = Demo4Config.TOPIC_TEST,containerFactory = "jmsListenerContainerTopic")
	public void receiveTopicNormalTest(String msg){
		System.out.println("收到一条topic信息:" + msg);
	}
}
