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

public class ServletRolling extends HttpServlet {

    public static final String[] Events = new String[100];
    public static final String[] TIME = new String[100];
    public static final int[] NO = new int[100];


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

        String callback = request.getParameter("callback");
        if (callback == null) {
            callback = "callback";
        }
        try {
            response.getWriter().print(callback + "(" +  getJson().build().toString() + ");");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private JsonObjectBuilder getJson() throws Exception{
        String url="jdbc:MySQL://localhost:3306/reptile?serverTimezone=UTC";
        String user="root";
        String password="990107ooo@";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(url,user,password);
        String sql="SELECT * FROM `weiboinfo` order by create_time desc";
        String sq2="SELECT * FROM `parsed` order by create_time desc";
        Statement st=con.createStatement();
        ResultSet res=st.executeQuery(sql);
        Statement st0=con.createStatement();
        ResultSet res0=st0.executeQuery(sq2);
        int t=0;
        while(t<100){
            res.next();
            res0.next();
            Events[t]=res.getString(6);
            TIME[t]=res.getTimestamp(23).toString();
            NO[t++]=res0.getInt(5);
        }
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("status", 1);
        json.add("info", "OK");

        JsonArrayBuilder events = Json.createArrayBuilder();
        for (int i = 0; i < Events.length; i++) {
            JsonObjectBuilder obj = Json.createObjectBuilder();
            obj.add("time",TIME[i]);
            obj.add("category", NO[i]);
            obj.add("content", Events[i]);
            events.add(obj);
        }
        json.add("events", events);

        return json;
    }

    public static void main(String[] args) throws Exception{
        ServletRolling s = new ServletRolling();
        JsonObjectBuilder obj = s.getJson();
        System.out.println(obj.build().toString());
    }
}
