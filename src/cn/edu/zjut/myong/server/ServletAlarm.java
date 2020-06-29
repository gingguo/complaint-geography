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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ServletAlarm extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            operate(req, resp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
        try{
            operate(req, resp);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void operate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String callback = request.getParameter("callback");
        if (callback == null) {
            callback = "callback";
        }
        response.getWriter().print(callback + "(" + getJson().build().toString() + ");");
    }

    private JsonObjectBuilder getJson() throws  Exception{
        String url="jdbc:MySQL://localhost/reptile?serverTimezone=UTC";
        String user="root";
        String password="990107ooo@";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con=DriverManager.getConnection(url,user,password);
        String sql="SELECT * FROM `weiboinfo` WHERE tag = '社会' order by create_time desc";
        String sq2="select * from parsed order by create_time desc limit 0,10;";
        Statement st=con.createStatement();
        Statement stt=con.createStatement();
        ResultSet res=st.executeQuery(sql);
        ResultSet res1=stt.executeQuery(sq2);

        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("status", 1);
        json.add("info", "OK");

        JsonArrayBuilder events = Json.createArrayBuilder();
        for (int i = 1; i <5; i++) {
            JsonObjectBuilder obj = Json.createObjectBuilder();

            res1.next();
            res.next();

            obj.add("time", res1.getTimestamp(3).toString());

            JsonArrayBuilder name = Json.createArrayBuilder();
            name.add(res.getString(3));

            obj.add("name",name);

            JsonArrayBuilder organization = Json.createArrayBuilder();
            organization.add(res.getString(12));

            obj.add("organization",organization);

            obj.add("category", res1.getInt(5));

            obj.add("content", res.getString(6));

            JsonArrayBuilder keywords = Json.createArrayBuilder();
            keywords.add(res.getString(12));
            obj.add("keywords", keywords);

            events.add(obj);
        }
        json.add("events", events);

        return json;
    }

    public static void main(String[] args) throws  Exception{
        ServletAlarm s = new ServletAlarm();
        JsonObjectBuilder obj = s.getJson();
        System.out.println(obj.build().toString());
    }
}