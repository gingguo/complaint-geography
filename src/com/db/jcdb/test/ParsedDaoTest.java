package com.db.jcdb.test;

import com.db.jcdb.dao.IParsedDao;
import com.db.jcdb.dao.IWeiboDao;
import com.db.jcdb.dao.impl.ParsedDaoimpl;
import com.db.jcdb.dao.impl.WeiboDaoimpl;
import com.db.jcdb.domain.Parsed;
import com.db.jcdb.domain.Weibo;

import location.*;
import time.Time;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ParsedDaoTest {
    private  static String[][] type = {
            {},
            {"假货","物价","商场","超市","商品","质量","食品","过期","发霉","腐烂","外卖","海鲜","鱼",
                    "虾","蛤蜊","扇贝","蟹","生鲜","奶茶","餐饮","就餐","肉","瓜","豆","菜","水果","炒货","蓝莓","苹果","哈蜜爪",
                    "橙","红枣","荸荠","梅","芒果","柚子","丝瓜","火龙果","柿子","桃","石榴","枣","椰子","荸荠","唇膏","唇釉","时尚","圣女果",
                    "西红柿","Kindle","玉米","白薯","苹果","柚子","木瓜","橘子","海带","茄子","洋葱","生姜","大蒜","黑木耳","香菇",
                    "茶叶","玉米油","葵花子","紫菜","电动牙刷","按摩椅","音响","AirPods","iPad","iPhone","空调","洗衣机","电视","美宝莲"
                    ,"苏宁易购","乌梅","食物中毒"}
            ,{"医院","开药","医保","药价","药品","高血压","低保","补助","工伤","社保","拆迁","保险","脱贫","住院","急诊","降解","白糖","西医","湿疹",
            "克莫司","脑梗药"}
            ,{"运输","违规停车","铁路","公路","高速","交通","地铁","公交","货车超载","出租车","打表","长途汽车"}
            ,{"环境","土地","污染","水费","电费","园林","绿化","污水","环保","可再生资源","大气污染","中石油","减排新能源","垃圾和有毒废物"}
            ,{"税法","税收","税","拒开发票","税率"}
            ,{"物业","市容市貌","公共设施","非法"}
            ,{"人力资源","聘请","劳动合同","职工"}
            ,{"社会保障","低保","补助","工伤","社保","拆迁","保险","职工","教育经费","乡村振兴战略","推进新型城镇化","流浪汉"}
            ,{"物业","市容市貌","公共设施","非法","资本筹集和分配","市场","公安局","民警","房","贿"}
    };

    private static String[] department={"国有资产监督管理委员会","市审管办","杭州经济技术开发区管委会", "市钱江经济开发区管委会", "杭州高新开发区管委会", "杭州西湖风景名胜管委会","市供电公司",
                    "市邮政管理局", "中国邮政公司杭州市分公司" ,"市气象局" ,"市烟草局" ,"国家统计局杭州调查队", "市国安局" ,"杭州电信公司" ,"杭州移动公司" ,"杭州联通公司",
                    "市会展办","市钱江新城管委会", "杭州公积金中心", "市社科院","政府办公厅","发展和改革委员会", "经济和信息化局","教育局", "科学技术局","民族宗教事务局",
                    "公安局","民政局","司法局","财政局","人力资源和社会保障局","国土资源局","规划局", "城乡建设委员会","住房保障和房产管理局", "园林文物局","交通运输局",
                    "农业农村局","林业水利局","商务局","旅游委员会", "文化广电新闻出版局", "体育局","卫生健康委员会", "审计局","统计局","生态环境局","市场监管局",
                    "质量技术监督局","物价局","城市管理局","应急管理局","机关事务管理局","市数据资源局"
                    ,"市投资促进局","金融工作办公室","市政府外事办公室","法制办公室","人民防空办公室","市政府研究室（参事室）"};

    public static void parse(){
        //内容提取
        IWeiboDao daow = new WeiboDaoimpl();
        IParsedDao dao = new ParsedDaoimpl();

        List<Weibo> list= daow.gWeibo();

        for(Weibo weibo1:list){
            Parsed parsed = new Parsed();
            parsed.setContent(weibo1.getContent());
            parsed.setId(weibo1.getId());
            parsed.setCreate_time(weibo1.getCreate_time());
            parsed.setTag(weibo1.getTag());

            System.out.println(weibo1.getContent());

           parsed.setTime(Time.work(weibo1.getContent(),weibo1.getPublish_time()));
            LocationRecognizer locationRecognizer = new LocationRecognizer();
           parsed.setLoc(locationRecognizer.start_location(weibo1.getContent()));

            dao.insert(parsed);
        }

        //分类
        dao.updataType(type);
        dao.setDep(3,"交通运输局");
        dao.setDep(5,"税务局");
        dao.setDep(6,"人力资源和社会保障局");
        dao.setDep(7,"人力资源和社会保障局");
        dao.updateDep(department);
       // dao.allloc();
    }

    public static void main(String[] args){
        //parse();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                parse();
            }
        }, 1000 , 60000);
    }
}
