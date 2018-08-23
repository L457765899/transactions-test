package com.sxb.lin.transactions.dubbo.test.demo4.mq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.sxb.lin.transactions.dubbo.test.demo4.config.Demo4Config;

@Component
public class JMSConsumer {

	@JmsListener(destination = Demo4Config.QUEUE_TEST,containerFactory = "jmsListenerContainerQueue")
	public void receiveQueueNormalTest(String msg){
		long currentTimeMillis = System.currentTimeMillis();
		System.out.println(Thread.currentThread().getName()+"收到一条queue信息:" + msg + currentTimeMillis);
		try {
			Thread.sleep(10000);
			System.out.println(currentTimeMillis + "完成");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//throw new RuntimeException();
	}
	
	@JmsListener(destination = Demo4Config.TOPIC_TEST,containerFactory = "jmsListenerContainerTopic")
	public void receiveTopicNormalTest(String msg){
		System.out.println(Thread.currentThread().getName()+"收到一条topic信息:" + msg);
	}
}
