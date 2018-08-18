package com.sxb.lin.transactions.dubbo.test.demo1.a.dao;

import com.sxb.lin.transactions.dubbo.test.demo1.a.model.T1;

public interface T1Mapper {
    int deleteByPrimaryKey(Integer id);

    int insert(T1 record);

    int insertSelective(T1 record);

    T1 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(T1 record);

    int updateByPrimaryKey(T1 record);
}