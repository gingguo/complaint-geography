package com.db.jcdb.dao.impl;

import com.db.jcdb.dao.IWeiboDao;
import com.db.jcdb.domain.Weibo;
import com.db.jcdb.util.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class WeiboDaoimpl implements IWeiboDao {

    @Override
    public List<Weibo> gWeibo() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConn();
            st = conn.createStatement();
            String sql = "select * from weiboinfo where id not in(select id from parsed) and DATEDIFF(create_time,now())<=0 ";
            rs = st.executeQuery(sql);
            List<Weibo> list = new ArrayList<Weibo>();
            while(rs.next()){
                Weibo weibo = new Weibo();
                weibo.setContent(rs.getString("content"));
                weibo.setPublish_time(rs.getTimestamp("publish_time"));
                weibo.setCreate_time(rs.getTimestamp("create_time"));
                weibo.setId(rs.getString("id"));
                weibo.setTag(rs.getString("tag"));

                System.out.println(weibo.getId());
                list.add(weibo);
            }
            return list;
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(conn,st,rs);
        }
        return null;
    }
}
