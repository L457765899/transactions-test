package com.sxb.lin.transactions.dubbo.test.demo2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sxb.lin.transactions.dubbo.test.demo2.a.dao.T1Mapper;
import com.sxb.lin.transactions.dubbo.test.demo2.a.model.T1;
import com.sxb.lin.transactions.dubbo.test.demo2.b.dao.T2Mapper;
import com.sxb.lin.transactions.dubbo.test.demo2.b.model.T2;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo22Service;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo2Service;

@Service
@com.alibaba.dubbo.config.annotation.Service
public class Demo2ServiceImpl implements Demo2Service {
	
	@Autowired
	private T1Mapper t1Mapper;
	
	@Autowired
	private T2Mapper t2Mapper;
	
	@Autowired
	private Demo22Service demo22Service;

	@Override
	@Transactional
	public void add() {
		T1 t1 = new T1();
		t1.setName("demo2-t1-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo2-t2-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo22Service.add();
	}

	@Override
	public void noTransactionAdd() {
		T1 t1 = new T1();
		t1.setName("no-demo2-t1-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("no-demo2-t2-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo22Service.noTransactionAdd();
	}

	@Override
	@Transactional
	public void add1() {
		T1 t1 = new T1();
		t1.setName("1-demo2-t1-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
	}

	@Override
	@Transactional
	public void add11() {
		T1 t1 = new T1();
		t1.setName("demo2-t1-11-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo2-t2-11-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo22Service.add11();
		
		//throw new RuntimeException();
	}

	@Override
	@Transactional
	public void add111() {
		T1 t1 = new T1();
		t1.setName("demo2-t1-111-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo2-t2-111-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
	}

	@Override
	@Transactional
	public void add33() {
		T1 t1 = new T1();
		t1.setName("demo2-t1-33-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo2-t2-33-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
	}

}
