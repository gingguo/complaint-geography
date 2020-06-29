package com.db.jcdb.dao.impl;

import com.db.jcdb.dao.IParsedDao;
import com.db.jcdb.domain.Parsed;
import com.db.jcdb.util.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParsedDaoimpl implements IParsedDao {

    public String[][] tag={
            {},
            {"综艺","科普","财经"}
            ,{"运动健身","明星","美妆"}
            ,{"股市" ,"体育","正能量"}
            ,{"动漫","电影","健康","数码"},
            {"政务","养身","科技","军事","音乐","社会","时尚"}
            ,{"法律","校园","家居"}
            ,{"房产搞笑","辟谣","历史"}
            ,{"瘦身","国际"}
            ,{"电视剧"}
    };
    @Override
    public void insert(Parsed parsed) {
        Connection conn = null;
        Statement st = null;
        try {
            conn = JDBCUtil.getConn();
            st = conn.createStatement();
            String sql ="insert into parsed(id,content,time,loc,create_time,tag) value('" + parsed.getId()+ "','" +
                    parsed.getContent() + "','"+parsed.getTime()+"','"+parsed.getLoc()+"','"+parsed.getCreate_time()+"','"+parsed.getTag()+"')";
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(conn,st,null);
        }
    }

    @Override
    public void updataType(String[][] type) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConn();
            st = conn.createStatement();

            String str = null;
            String sql = null;
            for(int i=1;i<type.length ;i++){
                sql = "update parsed set type = '"+i+"'where isnull(type) and content like '%"+type[i][0]+"%'";
                for(int j=1;j<type[i].length;j++){
                    str = " or content like '%"+type[i][j]+"%'";
                    sql += str;
                }
                st.executeUpdate(sql);
            }
            for(int i=1;i<tag.length ;i++){
                sql = "update parsed set type = '"+i+"'where isnull(type) and tag = '"+tag[i][0]+"'";
                for(int j=1;j<tag[i].length;j++){
                    str = " or tag = '"+tag[i][j]+"'";
                    sql += str;
                }
                st.executeUpdate(sql);
            }
            sql = "update parsed set type = '"+9+"' where isnull(type)";
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(conn,st,rs);
        }
    }

    @Override
    public void allloc() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

            String s = "unknown";
            try {
                conn = JDBCUtil.getConn();
                st = conn.createStatement();
                String sql = "update parsed set loc = '" + s + "' where loc = ''";
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                JDBCUtil.close(conn, st, rs);
            }

        }

    @Override
    public void updateDep(String[] department) {
        Connection conn = null;
        Statement st;
        st = null;
        ResultSet rs;
        rs = null;
        try {
            conn = JDBCUtil.getConn();
            st = conn.createStatement();
            List<Parsed> parseds = new ArrayList<>();
            String sql = null;

            for(int i=1;i<department.length ;i++){
                sql = "update parsed set department = '"+department[i]+"'where isnull(department) and content like '%"+department[i]+"%'";
                st.executeUpdate(sql);
            }

            String s="unknown";
            sql = "update parsed set department = '"+s+"'where isnull(department)";
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(conn,st,rs);
        }
    }

    @Override
    public void setDep( int type, String dep) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConn();
            st = conn.createStatement();

                String sql = "update parsed set department ='" + dep+ "' where isnull(department) and type='"+type+"'";
                st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(conn,st,rs);
        }
    }

}
