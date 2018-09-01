package com.sxb.lin.transactions.dubbo.test.demo5.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.lin.transactions.dubbo.test.demo4.util.RetUtil;
import com.sxb.lin.transactions.dubbo.test.demo5.service.Demo5Service;
import com.sxb.lin.transactions.dubbo.test.demo5.service.RocketMQService;

@RestController
@RequestMapping("/test")
public class Demo5Controller {
	
	@Autowired
	private Demo5Service demo5Service;
	
	@Autowired
	private RocketMQService rocketMQService;

	/**
	 * 测试AtomikosNonXADataSourceBean
	 * @return
	 */
	@RequestMapping(value="/add.json")
	public Map<String,Object> add(){
		demo5Service.add();
		return RetUtil.getRetValue(true);
	}
	
	@RequestMapping(value="/send.json")
	public Map<String,Object> send(){
		return rocketMQService.send();
	}
	
	@RequestMapping(value="/sendTra.json")
	public Map<String,Object> sendTra(){
		return rocketMQService.sendTra();
	}
	
	@RequestMapping(value="/sendPrepare.json")
	public Map<String,Object> sendPrepare() throws Exception{
		return rocketMQService.sendPrepare();
	}
	
	@RequestMapping(value="/sendCommit.json")
	public Map<String,Object> sendCommit() throws Exception{
		return rocketMQService.sendCommit();
	}
	
	@RequestMapping(value="/sendRollback.json")
	public Map<String,Object> sendRollback() throws Exception{
		return rocketMQService.sendRollback();
	}
	
	@RequestMapping(value="/sendLocalXa.json")
	public Map<String,Object> sendLocalXa(){
		rocketMQService.sendLocalXa();
		return RetUtil.getRetValue(true);
	}
	
	@RequestMapping(value="/sendLocalTra.json")
	public Map<String,Object> sendLocalTra(){
		rocketMQService.sendLocalTra();
		return RetUtil.getRetValue(true);
	}
}
