package com.sxb.lin.transactions.dubbo.test.demo1.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.lin.atomikos.dubbo.tm.TerminatedCommittingTransaction;
import com.sxb.lin.atomikos.dubbo.tm.TerminatedCommittingTransactionImpl;
import com.sxb.lin.transactions.dubbo.test.demo1.service.Demo1Service;
import com.sxb.lin.transactions.dubbo.test.demo1.util.RetUtil;

@RestController
@RequestMapping("/test")
public class Demo1Controller {
	
	@Autowired
	private Demo1Service demo1Service;
	
	private TerminatedCommittingTransaction tct = new TerminatedCommittingTransactionImpl();

	/**
	 * demo1->demo2->demo2->demo1
	 * @return
	 */
	@RequestMapping(value="/add.json")
	public Map<String,Object> add(){
		long start = System.currentTimeMillis();
		demo1Service.add();
		long end = System.currentTimeMillis();
		return RetUtil.getRetValue(end - start);
	}
	
	/**
	 * demo1->demo2->demo2->demo1 没有使用事务
	 * @return
	 */
	@RequestMapping(value="/noTransactionAdd.json")
	public Map<String,Object> noTransactionAdd(){
		long start = System.currentTimeMillis();
		demo1Service.noTransactionAdd();
		long end = System.currentTimeMillis();
		return RetUtil.getRetValue(end - start);
	}
	
	/**
	 * demo1->demo2
	 * @return
	 */
	@RequestMapping(value="/add1.json")
	public Map<String,Object> add1(){
		demo1Service.add1();
		return RetUtil.getRetValue(true);
	}
	
	/**
	 * 结束日志中，未完成的超时事务
	 * @param tid
	 * @return
	 */
	@RequestMapping(value="/terminated.json")
	public Map<String,Object> terminated(String tid){
		tct.terminated(tid);
		return RetUtil.getRetValue(true);
	}
	
	@RequestMapping(value="/convertToMysqlXid.json")
	public Map<String,Object> convertToMysqlXid(String globalTransactionId,String branchQualifier){
		String convertToMysqlXid = tct.convertToMysqlXid(globalTransactionId, branchQualifier);
		return RetUtil.getRetValue(convertToMysqlXid);
	}
	
	/**
	 * demo1->demo1->demo2->demo2->demo3->demo3
	 * @return
	 */
	@RequestMapping(value="/add11.json")
	public Map<String,Object> add11(){
		demo1Service.add11();
		return RetUtil.getRetValue(true);
	}
	
	/**
	 * demo1->demo2
	 *   |
	 *   |--->demo3
	 *   |
	 * demo1
	 * @return
	 */
	@RequestMapping(value="/add111.json")
	public Map<String,Object> add111(){
		demo1Service.add111();
		return RetUtil.getRetValue(true);
	}
}
