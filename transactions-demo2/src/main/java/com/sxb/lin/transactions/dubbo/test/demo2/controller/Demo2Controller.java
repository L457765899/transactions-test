package com.sxb.lin.transactions.dubbo.test.demo2.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo2Service;
import com.sxb.lin.transactions.dubbo.test.demo2.util.RetUtil;

@RestController
@RequestMapping("/test")
public class Demo2Controller {

	@Autowired
	private Demo2Service demo2Service;

	/**
	 * demo2->demo2->demo1
	 * @return
	 */
	@RequestMapping(value="/add.json")
	public Map<String,Object> add(){
		demo2Service.add();
		return RetUtil.getRetValue(true);
	}
}
