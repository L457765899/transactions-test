package com.sxb.lin.transactions.dubbo.test.demo1.b.dao;

import com.sxb.lin.transactions.dubbo.test.demo1.b.model.T2;

public interface T2Mapper {
    int deleteByPrimaryKey(Integer id);

    int insert(T2 record);

    int insertSelective(T2 record);

    T2 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(T2 record);

    int updateByPrimaryKey(T2 record);
}