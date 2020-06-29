package cn.edu.zjut.myong.server;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ServletDynamic extends HttpServlet {



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            operate(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            operate(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void operate(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
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

        String date = request.getParameter("date");
        JsonObjectBuilder json = Json.createObjectBuilder();
        if (date != null) {
            try {
                Date from = format1.parse(date);
                from.setTime(from.getTime());
                Date to = format1.parse(date);
                //to.setTime(from.getTime() + 24 * 60 * 60 * 1000);
                to.setTime(from.getTime() + 24 * 60 * 60 * 1000);
                json = getJson(from, to, category);
            } catch (Exception e) {
                json.add("status", 0);
                json.add("info", "Cannot parse the date!");
            }
        } else {
            // 返回前几天
            String last = request.getParameter("last");
            if (last == null || last.equals("0"))
                last = "1";
            Date to = new Date();
            Date from = new Date();
            from.setTime(from.getTime() - Integer.parseInt(last) * 24 * 60 * 60 * 1000);
            json = getJson(from, to, category);
        }

        String callback = request.getParameter("callback");
        if (callback == null) {
            callback = "callback";
        }
        response.getWriter().print(callback + "(" + json.build().toString() + ");");
    }

    private JsonObjectBuilder getJson(Date from, Date to, int category) throws SQLException, ClassNotFoundException {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("status", 1);
        json.add("info", "OK");

        int[][] TIME ={
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        };

        String url="jdbc:MySQL://localhost/reptile?serverTimezone=UTC";
        String user="root";
        String password="990107ooo@";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(url,user,password);

        String sql="SELECT type,HOUR(time)FROM parsed WHERE time between DATE_SUB(NOW(),INTERVAL 1 DAY) and DATE_SUB(NOW(),INTERVAL 0 DAY)";
        String sq2="select HOUR(sysdate())";
        Statement st=con.createStatement();
        ResultSet res=st.executeQuery(sql);
        Statement st2=con.createStatement();
        ResultSet res2=st2.executeQuery(sq2);
        res2.next();
        int m=res2.getInt(1);
        while(res.next()){
            TIME[res.getInt(1)][res.getInt(2)]++;
        }
        Random rand = new Random(new Date().getTime());
        JsonArrayBuilder dynamic = Json.createArrayBuilder();
        int x=m;
        for (long t = from.getTime(); t < to.getTime(); t += 60*60*1000,x++) {
            if (x>23) x=0;
            JsonObjectBuilder obj = Json.createObjectBuilder();
            obj.add("time", format2.format(new Date(t)));

            JsonArrayBuilder hot = Json.createArrayBuilder();
            if (category == 0) {
                for (int i = 1; i <= 9; i++) {
                    JsonObjectBuilder o = Json.createObjectBuilder();
                    o.add("category", i);
                    o.add("number",TIME[i][x]);
                    hot.add(o);
                }
                System.out.println("!");
            } else {
                JsonObjectBuilder o = Json.createObjectBuilder();
                o.add("category", category);
                o.add("number", TIME[category][x]);
                hot.add(o);
            }

            obj.add("hot", hot);
            dynamic.add(obj);
        }

        json.add("dynamic", dynamic);
        return json;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ServletDynamic s = new ServletDynamic();
        Date to = new Date();
        Date from = new Date(to.getTime() - 3 * 15 * 60 * 1000);
        JsonObjectBuilder obj = s.getJson(from, to, 0);
        System.out.println(obj.build().toString());
    }
}
