package com.sxb.lin.transactions.dubbo.test.demo1.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxb.lin.transactions.dubbo.test.demo1.a.dao.T1Mapper;
import com.sxb.lin.transactions.dubbo.test.demo1.a.model.T1;
import com.sxb.lin.transactions.dubbo.test.demo1.b.dao.T2Mapper;
import com.sxb.lin.transactions.dubbo.test.demo1.b.model.T2;
import com.sxb.lin.transactions.dubbo.test.demo1.service.Demo11Service;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo2Service;
import com.sxb.lin.transactions.dubbo.test.demo3.service.Demo33Service;

@Service
@com.alibaba.dubbo.config.annotation.Service
public class Demo11ServiceImpl implements Demo11Service{
	
	@Autowired
	private T1Mapper t1Mapper;
	
	@Autowired
	private T2Mapper t2Mapper;
	
	@Reference
	private Demo33Service demo33Service;
	
	@Reference
	private Demo2Service demo2Service;

	@Override
	@Transactional
	public void add() {
		T1 t1 = new T1();
		t1.setName("demo1-t1-cycle"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo1-t2-cycle"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
	}

	@Override
	public void noTransactionAdd() {
		T1 t1 = new T1();
		t1.setName("no-demo1-t1-cycle"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("no-demo1-t2-cycle"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
	}

	@Override
	@Transactional
	public void add3() {
		T1 t1 = new T1();
		t1.setName("demo1-t1-3-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo1-t2-3-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo33Service.add3();
	}

	@Override
	@Transactional
	public void add11() {
		T1 t1 = new T1();
		t1.setName("demo1-t1-11cycle"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo1-t2-11cycle"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		demo2Service.add11();
	}

	@Override
	@Transactional
	public void add111() {
		T1 t1 = new T1();
		t1.setName("demo1-t1-111cycle"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo1-t2-111cycle"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
	}

	@Override
	@Transactional
	public void add33() {
		T1 t1 = new T1();
		t1.setName("demo1-t1-33-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("demo1-t2-33-"+System.currentTimeMillis());
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
	}

}
