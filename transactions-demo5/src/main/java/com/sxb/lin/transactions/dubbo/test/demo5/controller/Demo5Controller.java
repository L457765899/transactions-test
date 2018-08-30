package com.sxb.lin.transactions.dubbo.test.demo5.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.lin.transactions.dubbo.test.demo4.util.RetUtil;
import com.sxb.lin.transactions.dubbo.test.demo5.service.Demo5Service;

@RestController
@RequestMapping("/test")
public class Demo5Controller {
	
	@Autowired
	private Demo5Service demo5Service;

	/**
	 * 测试AtomikosNonXADataSourceBean
	 * @return
	 */
	@RequestMapping(value="/add.json")
	public Map<String,Object> add(){
		demo5Service.add();
		return RetUtil.getRetValue(true);
	}
}
