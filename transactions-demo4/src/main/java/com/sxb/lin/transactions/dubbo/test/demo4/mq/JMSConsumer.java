package com.sxb.lin.transactions.dubbo.test.demo4.mq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JMSConsumer {

	@JmsListener(destination = "mq.queue.normal.test",containerFactory = "normalJmsListenerContainerQueue")
	public void receiveQueueNormalTest(String msg){
		System.out.println("收到一条queue信息:" + msg);
	}
	
	@JmsListener(destination = "mq.topic.normal.test",containerFactory = "normalJmsListenerContainerTopic")
	public void receiveTopicNormalTest(String msg){
		System.out.println("收到一条topic信息:" + msg);
	}
}
