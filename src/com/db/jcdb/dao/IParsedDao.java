package com.db.jcdb.dao;

import com.db.jcdb.domain.Parsed;


public interface IParsedDao {
    //插入更新数据
    void insert(Parsed parsed);
    //类别
    void updataType(String[][] type);
    //完成地址栏
    void allloc();
    //部门
    void updateDep(String[] department);
    void setDep(int type,String dep);
}
