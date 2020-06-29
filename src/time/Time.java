package time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Time {

    //timestamp转String
    public static String pTos(Timestamp timestamp){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
        String str = df.format(timestamp);
        return str;
    }

    //String转date
    public static Date stod(String time){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(time);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Date转String
    public static String dtos(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    //String转timestamp
    public static Timestamp stop(String time){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        Timestamp ts = Timestamp.valueOf(time);
        return ts;
    }

     public  static  Timestamp work(String s, Timestamp time){
        TimeRegex a = new TimeRegex();

        String str = pTos(time);
        Date date = stod(str);
         Date d=new Date();
         Date f = new Date(1970-01-01 );
        List<TimeNode> list = a.work(s, date);
        if(!list.isEmpty() && (list.get(0).date[0]!=null)&&list.get(0).date[0].before(d)&&list.get(0).date[0].after(f))
        {
            date = list.get(0).date[0];
            System.out.println(date);
            str = dtos(date);
            System.out.println(str);
            time = stop(str);
        }
        return time;
    }
}