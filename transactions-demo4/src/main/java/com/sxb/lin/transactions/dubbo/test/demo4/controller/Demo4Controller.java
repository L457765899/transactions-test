package com.sxb.lin.transactions.dubbo.test.demo4.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.lin.transactions.dubbo.test.demo4.config.Demo4Config;
import com.sxb.lin.transactions.dubbo.test.demo4.util.RetUtil;

@RestController
@RequestMapping("/test")
public class Demo4Controller {
	
	@Autowired
	private JmsTemplate jmsTemplate;

	@RequestMapping(value="/sendQueueTest.json")
	public Map<String,Object> sendQueueTest(String msg){
		jmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, msg);
		return RetUtil.getRetValue(msg);
	}
	
	@RequestMapping(value="/sendTopicTest.json")
	public Map<String,Object> sendTopicTest(String msg){
		jmsTemplate.convertAndSend(Demo4Config.DESTINATION_TOPIC_TEST, msg);
		return RetUtil.getRetValue(msg);
	}
}
