package cn.edu.zjut.myong.server;

import org.apdplat.word.WordFrequencyStatistics;
import org.apdplat.word.segmentation.SegmentationAlgorithm;


import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class ServletFeature extends HttpServlet {

    public static final String[] Words = new String[]{"脏乱", "早衰", "责备", "战祸", "折辱", "中山狼", "清峻", "求索", "热潮", "仁政", "瑞雪", "神采", "省便", "盛开", "盛誉", "诗仙", "爽脆", "株连", "坠毁", "滋生", "自恃", "走后门", "阻力", "罪状", "做戏", "甜头", "头名", "沃壤", "无上", "喜人", "先贤", "相称", "骁骑", "新意", "信奉", "急智", "技艺", "雄劲", "秀俊", "勋绩", "雅兴", "严整", "泱泱", "怡悦", "义诊", "英发", "英伟", "莹润", "勇健", "优厚", "幽雅", "有识", "友邻", "玉人", "元勋", "佳句", "兼爱", "坚守", "荐举", "见称", "见闻", "娇丽", "骄子", "解惑", "津要", "金嗓子", "匀净", "赞词", "箴言", "珍玩", "正理", "指导", "至理", "致敬", "智多星", "忠魂", "主力军", "准绳", "卓特", "进益", "奏凯", "尽瘁", "精白", "精洁", "精妙", "精髓", "精要", "景观", "敬信", "救亡", "哀鸿遍野", "有礼", "易如反掌", "昭然若揭", "异彩纷呈", "味道好", "危在旦夕", "无纪律", "无实质", "不明不白"};

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
        try{
            response.getWriter().print(callback + "(" +  getJson().build().toString() + ");");
        }catch (Exception e){
        }
    }

    private JsonObjectBuilder getJson()throws Exception{
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("status", 1);
        json.add("info", "OK");

        String url="jdbc:MySQL://localhost:3306/reptile?serverTimezone=UTC";
        String user="root";
        String password="990107ooo@";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(url,user,password);
        String sql="SELECT content FROM parsed LIMIT 1,2000;";
        Statement st=con.createStatement();
        ResultSet res=st.executeQuery(sql);
        FileWriter fw=null;
        fw=new FileWriter("out.txt");
        while(res.next()){
            fw.write(res.getString(1));
            //   fw.flush();
            fw.write("\r\n");
        }

        WordFrequencyStatistics wordFrequencyStatistics = new WordFrequencyStatistics();
        wordFrequencyStatistics.setRemoveStopWord(false);
//清除之前的统计结果
        wordFrequencyStatistics.reset();
//对文件进行分词
        wordFrequencyStatistics.seg(new File("out.txt"), new File("./out-seg-result.txt"));
//输出词频统计结果
        wordFrequencyStatistics.dump("file-seg-statistics-result.txt");


        String[] feature1 = new String[100];

        BufferedReader br = null;
        String line =null;
        //StringBuffer buf = new StringBuffer();
        br = new BufferedReader(new FileReader("file-seg-statistics-result.txt"));//filePath中是aaa.txt文件
        String str = "";

        int m=0;
        //循环读取文件的每一行，对需要修改的行进行修改，放入缓冲对象中
        while (((feature1[m]= br.readLine().substring(0,2)) != null)&&(m<99)) {
            //设置正则将多余空格都转为一个空格
            m++;
        }

        m=0;

        Random rand = new Random(new Date().getTime());
        JsonArrayBuilder features = Json.createArrayBuilder();
        for (int i = 0; i < Words.length ; i++) {
            if (rand.nextDouble() < 0.2) {
                JsonObjectBuilder obj = Json.createObjectBuilder();
                obj.add("word", feature1[i]);
                obj.add("weight", rand.nextInt(100) + 1);
                features.add(obj);
            }
        }
        json.add("features", features);

        return json;
    }

    public static void main(String[] args) throws Exception{
        ServletFeature s = new ServletFeature();
        JsonObjectBuilder obj = s.getJson();
        System.out.println(obj.build().toString());
    }
}
