package com.sxb.lin.transactions.dubbo.test.demo5.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sxb.lin.transactions.dubbo.test.demo1.b.dao.T2Mapper;
import com.sxb.lin.transactions.dubbo.test.demo1.b.model.T2;
import com.sxb.lin.transactions.dubbo.test.demo3.a.dao.T1Mapper;
import com.sxb.lin.transactions.dubbo.test.demo3.a.model.T1;
import com.sxb.lin.transactions.dubbo.test.demo5.service.Demo5Service;

@Service
public class Demo5ServiceImpl implements Demo5Service{
	
	@Autowired
	private T2Mapper t2Mapper;
	
	@Autowired
	private T1Mapper t1Mapper;

	@Override
	@Transactional
	public void add() {
		T1 t1 = new T1();
		t1.setName("test AtomikosNonXADataSourceBean");
		t1.setTime(System.currentTimeMillis());
		t1Mapper.insertSelective(t1);
		
		T2 t2 = new T2();
		t2.setName("test AtomikosNonXADataSourceBean");
		t2.setTime(System.currentTimeMillis());
		t2Mapper.insertSelective(t2);
		
		throw new RuntimeException();
	}

}
