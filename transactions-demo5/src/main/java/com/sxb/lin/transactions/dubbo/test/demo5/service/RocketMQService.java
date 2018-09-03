package com.sxb.lin.transactions.dubbo.test.demo5.service;

import java.util.Map;

public interface RocketMQService {

	Map<String,Object> send();

	Map<String, Object> sendTra();
	
	Map<String, Object> sendPrepare() throws Exception;
	
	Map<String, Object> sendCommit();
	
	Map<String, Object> sendRollback();

	void sendLocalXa();

	void sendLocalTra();

	void sendDubboXa();
}
