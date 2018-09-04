package com.sxb.lin.transactions.dubbo.test.demo5.mq;

import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.sxb.lin.atomikos.dubbo.rocketmq.MQEvent;
import com.sxb.lin.transactions.dubbo.test.demo4.util.RetUtil;
import com.sxb.lin.transactions.dubbo.test.demo5.config.Demo5Config;

@Component
public class MQConsumer {

	@EventListener(condition = "#event.topic=='" + Demo5Config.TOPIC_TEST + "'")
	public void receiveTopicTest(MQEvent event){
		System.out.println("Listener:"+Thread.currentThread().getName());
		for(MessageExt msg : event.getMsgs()){
			System.out.println(RetUtil.getDatetime() + "test消费成功。" + msg.toString());
			System.out.println(new String(msg.getBody()));
		}
		//throw new RuntimeException();
	}
	
	@EventListener(condition = "#event.topic=='" + Demo5Config.TOPIC_OTHER + "'")
	public void receiveTopicOther(MQEvent event){
		System.out.println("Listener:"+Thread.currentThread().getName());
		for(MessageExt msg : event.getMsgs()){
			System.out.println(RetUtil.getDatetime() + "other消费成功。" + msg.toString());
			System.out.println(new String(msg.getBody()));
		}
	}
}
