package com.sxb.lin.transactions.dubbo.test.demo4.service;

public interface MqService {

	void sendJtaQueueTest(String msg);

	void sendJtaTopicTest(String msg);

	void sendXaQueue(String msg);

	void recover();

	void batchSend();

	void sendDubboXaMsg4();

	void sendDubboXaMsg1();
}
