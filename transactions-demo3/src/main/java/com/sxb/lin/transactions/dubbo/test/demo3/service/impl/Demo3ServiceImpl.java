package com.sxb.lin.transactions.dubbo.test.demo3.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxb.lin.atomikos.dubbo.annotation.XA;
import com.sxb.lin.transactions.dubbo.test.demo1.service.Demo11Service;
import com.sxb.lin.transactions.dubbo.test.demo2.service.Demo2Service;
import com.sxb.lin.transactions.dubbo.test.demo3.a.dao.T1Mapper;
import com.sxb.lin.transactions.dubbo.test.demo3.a.model.T1;
import com.sxb.lin.transactions.dubbo.test.demo3.service.Demo333Service;
import com.sxb.lin.transactions.dubbo.test.demo3.service.Demo3Service;

@Service
public class Demo3ServiceImpl implements Demo3Service{
	
	@Autowired
	private T1Mapper t1Mapper;
	
	@Reference
	private Demo11Service demo11Service;
	
	@Reference
	private Demo2Service demo2Service;
	
	@Autowired
	private Demo333Service demo333Service;

	@Override
	@Transactional
	public void add() {
		T1 t1 = new T1();
		t1.setName("demo3-t1-noxa-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		demo11Service.add();
	}

	@Override
	@XA
	@Transactional
	public void add3() {
		T1 t1 = new T1();
		t1.setName("demo3-t1-3-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		demo11Service.add3();
		
		//throw new RuntimeException();
	}

	@Override
	@XA
	@Transactional
	public void add33() {
		T1 t1 = new T1();
		t1.setName("demo3-t1-33-"+System.currentTimeMillis());
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		demo11Service.add33();
		
		demo2Service.add33();
		
		demo333Service.add33();
		
		//throw new RuntimeException();
	}

}
