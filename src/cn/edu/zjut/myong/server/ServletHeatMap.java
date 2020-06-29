package cn.edu.zjut.myong.server;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Random;

public class ServletHeatMap extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        operate(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        operate(req, resp);
    }

    private void operate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        int category;
        String c = request.getParameter("category");
        if (c == null) {
            category = 0;
        } else {
            category = Integer.parseInt(c);
        }

        String callback = request.getParameter("callback");
        if (callback == null) {
            callback = "callback";
        }
        try {
            response.getWriter().print(callback + "(" +  getJson(category).build().toString() + ");");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private JsonObjectBuilder getJson(int category) throws Exception {
        String url="jdbc:MySQL://localhost/reptile?serverTimezone=UTC";
        String user="root";
        String password="990107ooo@";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(url,user,password);

        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("status", 1);
        json.add("info", "OK");

        int i;

        String sql="SELECT * FROM `parsed` WHERE loc <> \"\"";
        String sq2="SELECT * FROM `weiboinfo` order by create_time desc";
        Statement st=con.createStatement();
        ResultSet res=st.executeQuery(sql);
        Statement st2=con.createStatement();
        ResultSet res2=st2.executeQuery(sq2);

        JsonArrayBuilder heats = Json.createArrayBuilder();

        if (category == 0){
            while(res.next()&&res2.next()){
                try{
                    JsonArrayBuilder geo = Json.createArrayBuilder();
                    geo.add(Double.parseDouble(res.getString(6).substring(res.getString(6).indexOf("(")+1,res.getString(6).indexOf(","))));
                    geo.add(Double.parseDouble(res.getString(6).substring(res.getString(6).indexOf(",")+1,res.getString(6).indexOf(")"))));

                    i=res2.getInt(9);
                    JsonObjectBuilder ht = Json.createObjectBuilder();
                    ht.add("location", geo);
                    ht.add("number", i);
                    heats.add(ht);
                }catch (Exception e){
                    continue;}
            }
        }else{
            while(res.next()&&res2.next()){
                try{
                    JsonArrayBuilder geo = Json.createArrayBuilder();
                    if (res.getInt(5)==category){
                        geo.add(Double.parseDouble(res.getString(6).substring(res.getString(6).indexOf("(")+1,res.getString(6).indexOf(","))));
                        geo.add(Double.parseDouble(res.getString(6).substring(res.getString(6).indexOf(",")+1,res.getString(6).indexOf(")"))));
                    }
                    i=res2.getInt(9);
                    JsonObjectBuilder ht = Json.createObjectBuilder();
                    ht.add("location", geo);
                    ht.add("number", i);
                    heats.add(ht);
                }catch (Exception e){
                    continue;}
            }
        }

        json.add("heats", heats);
//这里要改
        return json;
    }

    public static void main(String[] args) throws Exception{
        ServletHeatMap s = new ServletHeatMap();
        JsonObjectBuilder obj = s.getJson(0);
        System.out.println(obj.build().toString());
    }
}
