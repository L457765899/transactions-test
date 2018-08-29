package com.sxb.lin.transactions.dubbo.test.demo4.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.lin.transactions.dubbo.test.demo4.config.Demo4Config;
import com.sxb.lin.transactions.dubbo.test.demo4.service.MqService;
import com.sxb.lin.transactions.dubbo.test.demo4.util.RetUtil;

@RestController
@RequestMapping("/test")
public class Demo4Controller {
	
	@Resource(name = "jmsTemplate")
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private MqService mqService;

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
	
	@RequestMapping(value="/sendJtaQueueTest.json")
	public Map<String,Object> sendJtaQueueTest(String msg){
		mqService.sendJtaQueueTest(msg);
		return RetUtil.getRetValue(msg);
	}
	
	@RequestMapping(value="/sendJtaTopicTest.json")
	public Map<String,Object> sendJtaTopicTest(String msg){
		mqService.sendJtaTopicTest(msg);
		return RetUtil.getRetValue(msg);
	}
	
	@RequestMapping(value="/sendXaQueue.json")
	public Map<String,Object> sendXaQueue(String msg){
		mqService.sendXaQueue(msg);
		return RetUtil.getRetValue(msg);
	}
	
	@RequestMapping(value="/recover.json")
	public Map<String,Object> recover(){
		mqService.recover();
		return RetUtil.getRetValue(true);
	}
	
	@RequestMapping(value="/batchSend.json")
	public Map<String,Object> batchSend(){
		mqService.batchSend();
		return RetUtil.getRetValue(true);
	}
	
	/**
	 * demo4->mq->demo1->mq
	 * @return
	 */
	@RequestMapping(value="/sendDubboXaMsg4.json")
	public Map<String,Object> sendDubboXaMsg4(){
		mqService.sendDubboXaMsg4();
		return RetUtil.getRetValue(true);
	}
}
