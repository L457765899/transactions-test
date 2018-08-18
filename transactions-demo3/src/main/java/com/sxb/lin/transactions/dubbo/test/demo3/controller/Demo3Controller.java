package com.sxb.lin.transactions.dubbo.test.demo3.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.lin.transactions.dubbo.test.demo3.service.Demo3Service;
import com.sxb.lin.transactions.dubbo.test.demo3.util.RetUtil;

@RestController
@RequestMapping("/test")
public class Demo3Controller {

	@Autowired
	private Demo3Service demo3Service;
	
	/**
	 * demo3->demo1 没有使用分布式事务
	 * @return
	 */
	@RequestMapping(value="/add.json")
	public Map<String,Object> add(){
		demo3Service.add();
		return RetUtil.getRetValue(true);
	}
	
	/**
	 * demo3->demo1->demo3->demo3
	 * @return
	 */
	@RequestMapping(value="/add3.json")
	public Map<String,Object> add3(){
		demo3Service.add3();
		return RetUtil.getRetValue(true);
	}
	
	/**
	 * demo3->demo1
	 *   |
	 *   |--->demo2
	 *   |
	 * demo3
	 * @return
	 */
	@RequestMapping(value="/add33.json")
	public Map<String,Object> add33(){
		demo3Service.add33();
		return RetUtil.getRetValue(true);
	}
}
