package com.sxb.lin.transactions.dubbo.test.demo1.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxb.lin.atomikos.dubbo.spring.jms.JtaJmsTemplate;
import com.sxb.lin.transactions.dubbo.test.demo1.a.dao.T1Mapper;
import com.sxb.lin.transactions.dubbo.test.demo1.a.model.T1;
import com.sxb.lin.transactions.dubbo.test.demo1.b.dao.T2Mapper;
import com.sxb.lin.transactions.dubbo.test.demo1.b.model.T2;
import com.sxb.lin.transactions.dubbo.test.demo1.config.MQConfig;
import com.sxb.lin.transactions.dubbo.test.demo1.service.Demo11Service;
import com.sxb.lin.transactions.dubbo.test.demo1.service.Demo1Service;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo2Service;
import com.sxb.lin.transactions.dubbo.test.demo3.service.Demo33Service;
import com.sxb.lin.transactions.dubbo.test.demo4.service.MqService;

@Service
public class Demo1ServiceImpl implements Demo1Service{

	@Autowired
	private T1Mapper t1Mapper;
	
	@Autowired
	private T2Mapper t2Mapper;
	
	@Reference
	private Demo2Service demo2Service;
	
	@Reference
	private Demo33Service demo33Service;
	
	@Autowired
	private Demo11Service demo11Service;
	
	@Resource(name = "jtaJmsTemplate")
	private JtaJmsTemplate jtaJmsTemplate;
	
	@Reference
	private MqService mqService;
	
	@Override
	@Transactional
	public void add() {
		T1 t1 = new T1();
		t1.setName("demo1-t1-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo1-t2-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo2Service.add();
		
		//throw new RuntimeException();
	}

	@Override
	public void noTransactionAdd() {
		T1 t1 = new T1();
		t1.setName("no-demo1-t1-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("no-demo1-t2-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo2Service.noTransactionAdd();
	}

	@Override
	@Transactional
	public void add1() {
		T1 t1 = new T1();
		t1.setName("1-demo1-t1-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		demo2Service.add1();
		
		throw new RuntimeException();
	}

	@Override
	@Transactional
	public void add11() {
		T1 t1 = new T1();
		t1.setName("demo1-t1-11-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo1-t2-11-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo11Service.add11();
		
		//throw new RuntimeException();
	}

	@Override
	@Transactional
	public void add111() {
		T1 t1 = new T1();
		t1.setName("demo1-t1-111-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo1-t2-111-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo2Service.add111();
		
		demo33Service.add111();
		
		demo11Service.add111();
		
		throw new RuntimeException();
	}

	@Override
	@Transactional
	public void sendJtaQueueTest() {
		
		T1 t1 = new T1();
		t1.setName("demo1-t1-测试消息队列");
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo1-t2-测试消息队列");
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		jtaJmsTemplate.convertAndSend(MQConfig.DESTINATION_QUEUE_TEST, "demo1的消息队列测试" + System.currentTimeMillis());
		
		//throw new RuntimeException();
	}

	@Override
	@Transactional
	public void sendDubboXaMsg1() {
		T1 t1 = new T1();
		t1.setName("dubbo jdbc jms 分布式事务测试-demo1-t1");
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("dubbo jdbc jms 分布式事务测试-demo1-t2");
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		jtaJmsTemplate.convertAndSend(MQConfig.DESTINATION_QUEUE_TEST, "dubbo jdbc jms 分布式事务消息1-demo1");
		
		jtaJmsTemplate.convertAndSend(MQConfig.DESTINATION_TOPIC_TEST, "dubbo jdbc jms 分布式事务消息2-demo1");
		
		mqService.sendDubboXaMsg1();
		
		//throw new RuntimeException();
	}
	
}
