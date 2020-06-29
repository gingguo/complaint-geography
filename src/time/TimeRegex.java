package time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用来进行正则表达式的匹配，提取文本中的时间信息
 * void work(String s,Date time) 该函数用来传入要提取的文本内容s以及作为基准时间的time变量。在work函数中遍历匹配了TimeData中的正则表达式，
 * 并进行了排序与去重。
 * void solve() 用于将提取后的时间文本信息数字化。并将在原文本中相邻的两个信息进行合并。
 */
public class TimeRegex {
    public List<TimeNode> list;
    public Date retime;

    public static void main(String[] args) {
//        TimeRegex a = new TimeRegex();
//        Date time = new Date();
//        List<TimeNode> list = a.work("1111111", time);
//        list.get(0).test();
    }

    private void solve() {
        for (int i = 0; i < list.size(); i++) {
            int f = list.get(i).row;
            if (f == 0)
                list.get(i).work0(retime);
            else if (f == 1)
                list.get(i).work1(retime);
            else if (f == 2)
                list.get(i).work2(retime);
            else if (f == 3)
                list.get(i).work3(retime);
            else if (f == 4)
                list.get(i).work4(retime);
        }
        List<TimeNode> a = new ArrayList<TimeNode>();
        for (int i = 0; i < list.size(); i++) //将在原文本中相邻的两个信息进行合并。
        {
            if (i < list.size() - 1 && list.get(i).end + 1 == (list.get(i + 1).start)) {
                a.add(list.get(i).merge(list.get(i + 1), retime));
                i++;
                if (i == list.size() - 2)
                    break;
            } else
                a.add(list.get(i));
        }
        list.clear();
        list = a;
    }
//
//    public void print() {
//        for (int i = 0; i < list.size(); i++) {
//            //list.get(i).print();
//            System.out.println(" ");
//        }
//    }

    //s表示输入的文本，time表示作为基准的时间
    public List<TimeNode> work(String s, Date time) {
        retime = time;
        list = new ArrayList<TimeNode>();
        // TimeData d = new TimeData();
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 16; j++) {
                if (i == 0 && j > 0)
                    break;
                if (i == 1 && j > 6)
                    break;
                if (i == 2 && j > 9)
                    break;
                if (i == 2 && j == 1)
                    continue;
                if (i == 3 && j > 7)
                    break;
                //System.out.println(i+" "+j);
                Pattern pattern = Pattern.compile(TimeData.p[i][j]);
                Matcher matcher = pattern.matcher(s);
                String buffer = new String();
                while (matcher.find()) {
                    buffer = matcher.group();
                    //buffer.append(" ");
                    // ·········································································································································
                    TimeNode a = new TimeNode(buffer, matcher.start(), matcher.end() - 1, i, j);
                    list.add(a);
                }
                // s = matcher.replaceAll("");
            }
        Collections.sort(list);
        List<TimeNode> a = new ArrayList<TimeNode>();
        for (int i = 0; i < list.size(); i++) {
            int flag = 0;
            for (int j = 0; j < a.size(); j++) {
                if (list.get(i).end <= a.get(j).end)
                    flag = 1;
            }
            if (flag == 0){
                a.add(list.get(i));
            }

        }
        list.clear();
        list = a;
        solve();

        return list;
    }
}


