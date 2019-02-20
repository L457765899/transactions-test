package com.sxb.lin.transactions.dubbo.test.demo6.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo222Service;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo2Service;

@RestController
@RequestMapping("/test")
public class Demo5Controller {

	@Reference
	private Demo2Service demo2Service;
	
	@Reference
	//@Reference(url = "dubbo://192.168.0.68:22881/com.sxb.lin.transactions.dubbo.test.demo2.service.Demo222Service")
	//@Reference(url = "dubbo://192.168.0.68:22881/com.sxb.lin.transactions.dubbo.test.demo2.service.Demo222Service",timeout=60000)
	private Demo222Service demo222Service;
	
	@RequestMapping(value="/dubbo.json")
	public Map<String,Object> dubbo(){
		demo2Service.add1();
		return new HashMap<String, Object>();
	}
	
	@RequestMapping(value="/test.json")
	public Map<String,Object> test(){
		demo222Service.test();
		return new HashMap<String, Object>();
	}
}
