package com.sxb.lin.transactions.dubbo.test.demo2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxb.lin.transactions.dubbo.test.demo1.service.Demo11Service;
import com.sxb.lin.transactions.dubbo.test.demo2.a.dao.T1Mapper;
import com.sxb.lin.transactions.dubbo.test.demo2.a.model.T1;
import com.sxb.lin.transactions.dubbo.test.demo2.b.dao.T2Mapper;
import com.sxb.lin.transactions.dubbo.test.demo2.b.model.T2;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo22Service;
import com.sxb.lin.transactions.dubbo.test.demo3.service.Demo33Service;

@Service
public class Demo22ServiceImpl implements Demo22Service {

	@Autowired
	private T1Mapper t1Mapper;
	
	@Autowired
	private T2Mapper t2Mapper;
	
	@Reference
	private Demo11Service demo11Service;

	@Reference
	private Demo33Service demo33Service;
	
	@Override
	@Transactional
	public void add() {
		T1 t1 = new T1();
		t1.setName("demo2-t1-REQUIRED"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo2-t2-REQUIRED"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo11Service.add();
	}

	@Override
	public void noTransactionAdd() {
		T1 t1 = new T1();
		t1.setName("no-demo2-t1-REQUIRED"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("no-demo2-t2-REQUIRED"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo11Service.noTransactionAdd();
	}

	@Override
	@Transactional
	public void add11() {
		T1 t1 = new T1();
		t1.setName("demo2-t1-11REQUIRED"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo2-t2-11REQUIRED"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo33Service.add11();
	}

}
