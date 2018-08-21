package com.sxb.lin.transactions.dubbo.test.demo4.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.lin.transactions.dubbo.test.demo4.mq.JMSProducer;
import com.sxb.lin.transactions.dubbo.test.demo4.util.RetUtil;

@RestController
@RequestMapping("/test")
public class Demo4Controller {
	
	@Autowired
	private JMSProducer producer;

	@RequestMapping(value="/sendQueueNormalTest.json")
	public Map<String,Object> sendQueueNormalTest(String msg){
		producer.sendQueueNormalTest(msg);
		return RetUtil.getRetValue(msg);
	}
	
	@RequestMapping(value="/sendTopicNormalTest.json")
	public Map<String,Object> sendTopicNormalTest(String msg){
		producer.sendTopicNormalTest(msg);
		return RetUtil.getRetValue(msg);
	}
}
