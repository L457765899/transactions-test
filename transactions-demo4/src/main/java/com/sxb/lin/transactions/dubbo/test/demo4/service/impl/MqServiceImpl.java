package com.sxb.lin.transactions.dubbo.test.demo4.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sxb.lin.transactions.dubbo.test.demo3.a.dao.T1Mapper;
import com.sxb.lin.transactions.dubbo.test.demo3.a.model.T1;
import com.sxb.lin.transactions.dubbo.test.demo4.config.Demo4Config;
import com.sxb.lin.transactions.dubbo.test.demo4.service.MqService;

@Service
public class MqServiceImpl implements MqService{
	
	@Autowired
	private T1Mapper t1Mapper;
	
	@Resource(name = "jtaJmsTemplate")
	private JmsTemplate jtaJmsTemplate;

	@Override
	@Transactional
	public void sendJtaQueueTest(String msg) {
		
		T1 t1 = new T1();
		t1.setName("mq queue 事务");
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, msg);
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, msg + "，多加一条");
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_TOPIC_TEST, msg);
		
		throw new RuntimeException();
	}

	@Override
	@Transactional
	public void sendJtaTopicTest(String msg) {
		
		T1 t1 = new T1();
		t1.setName("mq topic 事务");
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_TOPIC_TEST, msg);
	}

}
