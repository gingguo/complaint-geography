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

public class ServletDistinct extends HttpServlet {

    private static int x=1;
    private static int y=1;

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
            response.getWriter().print(callback + "(" + getJson(category).build().toString() + ");");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private JsonObjectBuilder getJson(int category) throws Exception{

        int[][] cate ={
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        };
        int c[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        String url="jdbc:MySQL://localhost/reptile?serverTimezone=UTC";
        String user="root";
        String password="990107ooo@";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(url,user,password);
        String sql="select type,loc from parsed where loc!=\"\" ORDER BY create_time DESC;";
        Statement st=con.createStatement();
        ResultSet res=st.executeQuery(sql);
        while (res.next()){
            int j=Integer.parseInt(res.getString(2).substring(res.getString
                    (2).indexOf("{")+1,res.getString(2).indexOf("}")));
            int t = res.getInt(1);
            cate[t][j]++;
            cate[10][t]++;
        }

        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("status", 1);
        json.add("info", "OK");

//        Random rand = new Random(new Date().getTime());

        int num;
        JsonArrayBuilder hots = Json.createArrayBuilder();
        if (x>14)x=1;
        if (y>14)y=1;
        try{
            if (category == 0) {
                if (x==14){
                    for (int i = 1; i <= 9; i++) {
                        JsonObjectBuilder obj = Json.createObjectBuilder();
                        obj.add("category", i);
                        obj.add("number", cate[10][i]);
                        hots.add(obj);
                    }
                    x=x+1;
                }else {
                    for (int i = 1; i <= 9; i++) {
                        JsonObjectBuilder obj = Json.createObjectBuilder();
                        obj.add("category", i);
                        obj.add("number", cate[i][x]);
                        hots.add(obj);
                    }
                    x=x+1;
                }
            } else {
                if (y==14){
                    JsonObjectBuilder obj = Json.createObjectBuilder();
                    obj.add("category", category);
                    obj.add("number", cate[10][category]);
                    System.out.println(cate[10][category]);
                    hots.add(obj);
                    y=y+1;
                }else{
                    JsonObjectBuilder obj = Json.createObjectBuilder();
                    obj.add("category", category);
                    obj.add("number", cate[category][y]);
                    hots.add(obj);
                    y=y+1;
                }
            }
        }catch (Exception e){
            x=1;y=1;
        }

        json.add("hot", hots);


        return json;
    }

    public static void main(String[] args) throws Exception{
        ServletDistinct s = new ServletDistinct();
        JsonObjectBuilder obj = s.getJson(6);
        System.out.println(obj.build().toString());
    }
}
