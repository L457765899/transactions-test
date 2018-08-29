package com.sxb.lin.transactions.dubbo.test.demo4.service.impl;

import java.util.UUID;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.XAConnection;
import javax.jms.XASession;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atomikos.datasource.xa.XID;
import com.sxb.lin.atomikos.dubbo.annotation.XA;
import com.sxb.lin.transactions.dubbo.test.demo1.service.Demo11Service;
import com.sxb.lin.transactions.dubbo.test.demo3.a.dao.T1Mapper;
import com.sxb.lin.transactions.dubbo.test.demo3.a.model.T1;
import com.sxb.lin.transactions.dubbo.test.demo4.config.Demo4Config;
import com.sxb.lin.transactions.dubbo.test.demo4.service.MqService;

@Service
@com.alibaba.dubbo.config.annotation.Service
public class MqServiceImpl implements MqService{
	
	@Autowired
	private T1Mapper t1Mapper;
	
	@Resource(name = "jtaJmsTemplate")
	private JmsTemplate jtaJmsTemplate;
	
	@Resource(name = "pooledJmsConnectionFactory")
	private PooledConnectionFactory pooledConnectionFactory;
	
	@Reference
	private Demo11Service demo11Service;

	@Override
	@XA
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

	@Override
	public void sendXaQueue(String msg) {
		try {
			PooledConnection pooledConnection = (PooledConnection) pooledConnectionFactory.createConnection();
			XAConnection connection = (XAConnection) pooledConnection.getConnection();
			System.out.println(pooledConnection.toString());
			System.out.println(connection.toString());
			XASession createXASession = connection.createXASession();
			String tid = "demo4.atomikos.tm" + System.currentTimeMillis();
			String resourceURL = UUID.randomUUID().toString();
			XID xid = new XID(tid,resourceURL);
			XAResource xaResource = createXASession.getXAResource();
			try {
				xaResource.start(xid, XAResource.TMNOFLAGS);
			} catch (XAException e) {
				e.printStackTrace();
			}
			
			MessageProducer createProducer = createXASession.createProducer(Demo4Config.DESTINATION_QUEUE_TEST);
			TextMessage createTextMessage = createXASession.createTextMessage("xa jms");
			createProducer.send(createTextMessage);
			try {
				xaResource.end(xid, XAResource.TMSUCCESS);
			} catch (XAException e) {
				e.printStackTrace();
			}
			
			try {
				xaResource.prepare(xid);
				
				Xid[] recover = xaResource.recover(XAResource.TMSTARTRSCAN);
				for(Xid x : recover){
					System.out.println(x.toString());
				}
			} catch (XAException e) {
				e.printStackTrace();
			}
			
//			try {
//				xaResource.commit(xid, false);
//			} catch (XAException e) {
//				e.printStackTrace();
//			}
			
			createProducer.close();
			createXASession.close();
			pooledConnection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void recover() {
		try {
			PooledConnection pooledConnection = (PooledConnection) pooledConnectionFactory.createConnection();
			XAConnection connection = (XAConnection) pooledConnection.getConnection();
			XASession createXASession = connection.createXASession();
			XAResource xaResource = createXASession.getXAResource();
			try {
				Xid[] recover = xaResource.recover(XAResource.TMSTARTRSCAN);
				for(Xid x : recover){
					System.out.println(x.toString());
					//xaResource.commit(x, false);
				}
			} catch (XAException e) {
				e.printStackTrace();
			}
			createXASession.close();
			pooledConnection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	@XA
	@Transactional
	public void batchSend() {
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, "批量发送1");
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, "批量发送2");
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, "批量发送3");
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_TOPIC_TEST, "批量发送4");
		
		throw new RuntimeException();
	}

	@Override
	@XA
	@Transactional
	public void sendDubboXaMsg4() {
		T1 t1 = new T1();
		t1.setName("dubbo jdbc jms 分布式事务测试-demo4");
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, "dubbo jdbc jms 分布式事务消息1-demo4");
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, "dubbo jdbc jms 分布式事务消息2-demo4");
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_TOPIC_TEST, "dubbo jdbc jms 分布式事务消息3-demo4");
		
		demo11Service.sendDubboXaMsg4();
		
		throw new RuntimeException();
	}

	@Override
	@Transactional
	public void sendDubboXaMsg1() {
		T1 t1 = new T1();
		t1.setName("dubbo jdbc jms 分布式事务测试-demo4");
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, "dubbo jdbc jms 分布式事务消息1-demo4");
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_QUEUE_TEST, "dubbo jdbc jms 分布式事务消息2-demo4");
		
		jtaJmsTemplate.convertAndSend(Demo4Config.DESTINATION_TOPIC_TEST, "dubbo jdbc jms 分布式事务消息3-demo4");
	}

}
