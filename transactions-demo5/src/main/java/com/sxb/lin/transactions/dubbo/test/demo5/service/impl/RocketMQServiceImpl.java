package com.sxb.lin.transactions.dubbo.test.demo5.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.rocketmq.client.Validators;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageAccessor;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxb.lin.atomikos.dubbo.rocketmq.MQProducerFor2PC;
import com.sxb.lin.transactions.dubbo.test.demo1.b.dao.T2Mapper;
import com.sxb.lin.transactions.dubbo.test.demo1.b.model.T2;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo2Service;
import com.sxb.lin.transactions.dubbo.test.demo4.util.RetUtil;
import com.sxb.lin.transactions.dubbo.test.demo5.config.Demo5Config;
import com.sxb.lin.transactions.dubbo.test.demo5.service.RocketMQService;

@Service
public class RocketMQServiceImpl implements RocketMQService{
	
	private final InternalLogger log = ClientLogger.getLog();
	
	private List<SendResult> sendResults = new Vector<>();

	@Resource(name = "defaultProducer")
	private TransactionMQProducer producer;
	
	@Resource(name = "producerFor2PC")
	private MQProducerFor2PC producer2PC;
	
	@Autowired
	private T2Mapper t2Mapper;
	
	@Reference
	private Demo2Service demo2Service;
	
	@Override
	public Map<String,Object> send() {
		Message msg = new Message(
				Demo5Config.TOPIC_TEST,
				"TagA",
				(System.currentTimeMillis() + "RocketMQ发送消息。").getBytes());
		try {
			SendResult send = producer.send(msg);
			return RetUtil.getRetValue(send);
		} catch (MQClientException e) {
			e.printStackTrace();
		} catch (RemotingException e) {
			e.printStackTrace();
		} catch (MQBrokerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return RetUtil.getRetValue(false); 
	}

	@Override
	public Map<String, Object> sendTra() {
		String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            try {
                Message msg =
                    new Message(Demo5Config.TOPIC_TEST, tags[i % tags.length], "KEY" + i,
                        ("Hello RocketMQ " + i).getBytes());
                SendResult sendResult = producer.sendMessageInTransaction(msg, null);
                System.out.printf("%s%n", sendResult);
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }
        return RetUtil.getRetValue(true); 
	}

	@Override
	public Map<String, Object> sendPrepare() throws Exception {
		long currentTimeMillis = System.currentTimeMillis();
		Message msg = new Message(
				Demo5Config.TOPIC_TEST,
				"TagA",
				(RetUtil.getDatetime(currentTimeMillis) + "RocketMQ发送2PC消息。").getBytes());
		
		DefaultMQProducerImpl defaultMQProducerImpl = producer.getDefaultMQProducerImpl();
		Validators.checkMessage(msg, producer);
		
        MessageAccessor.putProperty(msg, MessageConst.PROPERTY_TRANSACTION_PREPARED, "true");
        MessageAccessor.putProperty(msg, MessageConst.PROPERTY_PRODUCER_GROUP, producer.getProducerGroup());
        //MessageAccessor.putProperty(msg, MessageConst.PROPERTY_CHECK_IMMUNITY_TIME_IN_SECONDS, "60");
        SendResult sendResult = null;
        try {
        	sendResult = defaultMQProducerImpl.send(msg);
        	sendResults.add(sendResult);
        } catch (Exception e) {
            throw new MQClientException("send message Exception", e);
        }
		
        Map<String, Object> retValue = RetUtil.getRetValue(sendResult);
        retValue.put("time", RetUtil.getDatetime(currentTimeMillis));
        return retValue; 
	}

	@Override
	public Map<String, Object> sendCommit() {
		
		List<TransactionSendResult> list = new ArrayList<TransactionSendResult>();
		for(SendResult sendResult : sendResults){
			DefaultMQProducerImpl defaultMQProducerImpl = producer.getDefaultMQProducerImpl();
	        LocalTransactionState localTransactionState = LocalTransactionState.ROLLBACK_MESSAGE;
	        switch (sendResult.getSendStatus()) {
	            case SEND_OK:
	                localTransactionState = LocalTransactionState.COMMIT_MESSAGE;
	                break;
	            case FLUSH_DISK_TIMEOUT:
	                break;
	            case FLUSH_SLAVE_TIMEOUT:
	            	break;
	            case SLAVE_NOT_AVAILABLE:
	                break;
	            default:
	                break;
	        }
	
	        try {
	        	defaultMQProducerImpl.endTransaction(sendResult, localTransactionState, null);
	        } catch (Exception e) {
	            log.warn("local transaction execute " + localTransactionState + ", but end broker transaction failed", e);
	        }
	
	        TransactionSendResult transactionSendResult = new TransactionSendResult();
	        transactionSendResult.setSendStatus(sendResult.getSendStatus());
	        transactionSendResult.setMessageQueue(sendResult.getMessageQueue());
	        transactionSendResult.setMsgId(sendResult.getMsgId());
	        transactionSendResult.setQueueOffset(sendResult.getQueueOffset());
	        transactionSendResult.setTransactionId(sendResult.getTransactionId());
	        transactionSendResult.setLocalTransactionState(localTransactionState);
	        list.add(transactionSendResult);
		}
		//sendResults.clear();
		
        return RetUtil.getRetValue(list); 
	}

	@Override
	public Map<String, Object> sendRollback() {
		
		List<TransactionSendResult> list = new ArrayList<TransactionSendResult>();
		for(SendResult sendResult : sendResults){
			DefaultMQProducerImpl defaultMQProducerImpl = producer.getDefaultMQProducerImpl();
	        LocalTransactionState localTransactionState = LocalTransactionState.ROLLBACK_MESSAGE;
	        
	        try {
	        	defaultMQProducerImpl.endTransaction(sendResult, localTransactionState, null);
	        } catch (Exception e) {
	            log.warn("local transaction execute " + localTransactionState + ", but end broker transaction failed", e);
	        }
	
	        TransactionSendResult transactionSendResult = new TransactionSendResult();
	        transactionSendResult.setSendStatus(sendResult.getSendStatus());
	        transactionSendResult.setMessageQueue(sendResult.getMessageQueue());
	        transactionSendResult.setMsgId(sendResult.getMsgId());
	        transactionSendResult.setQueueOffset(sendResult.getQueueOffset());
	        transactionSendResult.setTransactionId(sendResult.getTransactionId());
	        transactionSendResult.setLocalTransactionState(localTransactionState);
	        list.add(transactionSendResult);
		}
		//sendResults.clear();
		
        return RetUtil.getRetValue(list); 
	}

	@Override
	@Transactional
	public void sendLocalXa() {

		T2 t2 = new T2();
		t2.setName("test send local xa");
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		Message msg = new Message(
				Demo5Config.TOPIC_TEST,
				"TagB",
				(System.currentTimeMillis() + "本地事务测试两段提交。").getBytes());
		producer2PC.send2PCMessageInTransaction(msg);
		
		//throw new RuntimeException();
	}

	@Override
	@Transactional
	public void sendLocalTra() {
		T2 t2 = new T2();
		t2.setName("test send local tra");
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		Message msg1 = new Message(
				Demo5Config.TOPIC_TEST,
				"TagA",
				("本地事务后提交1。").getBytes());
		producer2PC.sendMessageAfterTransaction(msg1);
		
		Message msg2 = new Message(
				Demo5Config.TOPIC_OTHER,
				"TagA",
				("本地事务后提交2。").getBytes());
		producer2PC.sendMessageAfterTransaction(msg2);
		
		Message msg3 = new Message(
				Demo5Config.TOPIC_OTHER,
				"TagA",
				("本地事务后提交3。").getBytes());
		producer2PC.sendMessageAfterTransaction(msg3);
		
		//throw new RuntimeException();
	}

	@Override
	@Transactional
	public void sendDubboXa() {
		T2 t2 = new T2();
		t2.setName("test send dubbo xa");
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		Message msg = new Message(
				Demo5Config.TOPIC_TEST,
				"TagA",
				(System.currentTimeMillis() + "demo5 dubbo事务测试两段提交。").getBytes());
		producer2PC.send2PCMessageInTransaction(msg);
		
		demo2Service.sendDubboXa();
		
		//throw new RuntimeException();
	}

	
}
