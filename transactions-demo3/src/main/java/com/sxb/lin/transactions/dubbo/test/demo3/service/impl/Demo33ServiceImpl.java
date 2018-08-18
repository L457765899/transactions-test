package com.sxb.lin.transactions.dubbo.test.demo3.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sxb.lin.transactions.dubbo.test.demo3.a.dao.T1Mapper;
import com.sxb.lin.transactions.dubbo.test.demo3.a.model.T1;
import com.sxb.lin.transactions.dubbo.test.demo3.service.Demo333Service;
import com.sxb.lin.transactions.dubbo.test.demo3.service.Demo33Service;

@Service
@com.alibaba.dubbo.config.annotation.Service
public class Demo33ServiceImpl implements Demo33Service{

	@Autowired
	private T1Mapper t1Mapper;
	
	@Autowired
	private Demo333Service demo333Service;
	
	@Override
	@Transactional
	public void add3() {
		T1 t1 = new T1();
		t1.setName("demo3-t1-33-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		demo333Service.add3();
	}

	@Override
	@Transactional
	public void add11() {
		T1 t1 = new T1();
		t1.setName("demo3-t1-11-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		demo333Service.add11();
	}

	@Override
	@Transactional
	public void add111() {
		T1 t1 = new T1();
		t1.setName("demo3-t1-111-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
	}

}
