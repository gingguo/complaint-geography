package time;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeNode implements Comparable<TimeNode> {
    public String text;
    public int start, end, row, col, flag;
    public Date date[];
    int chage[];

    public TimeNode(String x, int a, int b, int c, int d) {
        text = x; // 提取出来的文本信息
        start = a; // 文本在原文中开始的位置
        end = b; // 结束的位置
        row = c; // 正则规则下标第一维坐标
        col = d; // 正则规则下标的第二维坐标
        date = new Date[2]; //用于保存具体的时间信息
        date[0] = null; //开始时间
        date[1] = null; //结束时间（如果是时间点，则这个变量为null）
        chage = new int[6]; //用于表示在本次转换时，哪个时间维度进行了修改，便于后面时间信息之间的合并
        for (int i = 0; i < 6; i++)
            chage[i] = 0;
        flag = 0;
    }

//    @Override
//    public String toString() {
//        SimpleDateFormat pattern = new SimpleDateFormat("yyyy/MM/dd_HH:mm");
//        if (date == null || date.length < 1)
//            return "";
//        String txt = "[" + pattern.format(date[0]);
//        for (int i = 1; i < date.length; i++) {
//            if (date[i] == null)
//                break;
//            txt = txt + "——" + pattern.format(date[i]);
//        }
//        txt = txt + "]";
//        return txt;
//    }

    public int compareTo(TimeNode o) {
        if (this.start < o.start)
            return -1;
        else if (this.start == o.start && this.end > o.end)
            return -1;
        else
            return 1;
    }

    public int C2int(String chineseNumber) { // 中文数字转阿拉伯数字
        int flag = 0; // 判断是否有中文数字出现
        int result = 0;
        int temp = 1;// 存放一个单位的数字如：十万
        int count = 0;// 判断是否有chArr
        char[] cnArr = new char[]{'两', '一', '二', '三', '四', '五', '六', '七', '八', '九'};
        char[] chArr = new char[]{'十', '百', '千', '万', '亿'};
        int fx = 0; // 判断是否有单位出现
        for (int i = 0; i < chArr.length; i++) {
            String sx = String.valueOf(chArr[i]);
            if (chineseNumber.contains(sx)) {
                fx = 1;
                break;
            }
        }
        if (fx == 1) {
            for (int i = 0; i < chineseNumber.length(); i++) {
                boolean b = true;// 判断是否是chArr
                char c = chineseNumber.charAt(i);
                for (int j = 0; j < cnArr.length; j++) {// 非单位，即数字
                    if (c == cnArr[j]) {
                        flag = 1;
                        if (0 != count) { // 添加下一个单位之前，先把上一个单位值添加到结果中
                            result += temp;
                            temp = 1;
                            count = 0;
                        }
                        // 下标+1，就是对应的值
                        if (j != 0)
                            temp = j;
                        else
                            temp = 2;
                        b = false;
                        break;
                    }
                }
                if (b) { // 单位{'十','百','千','万','亿'}
                    for (int j = 0; j < chArr.length; j++) {
                        if (c == chArr[j]) {
                            flag = 1;
                            switch (j) {
                                case 0:
                                    temp *= 10;
                                    break;
                                case 1:
                                    temp *= 100;
                                    break;
                                case 2:
                                    temp *= 1000;
                                    break;
                                case 3:
                                    temp *= 10000;
                                    break;
                                case 4:
                                    temp *= 100000000;
                                    break;
                                default:
                                    break;
                            }
                            count++;
                        }
                    }
                }
                if (i == chineseNumber.length() - 1 && flag != 0) {// 遍历到最后一个字符
                    result += temp;
                }
            }
        } else {
            int d = 10, t = 0;
            for (int i = 0; i < chineseNumber.length(); i++) {
                char c = chineseNumber.charAt(i);
                for (int j = 0; j < cnArr.length; j++) {// 非单位，即数字
                    if (c == cnArr[j]) {
                        if (j != 0)
                            t = j;
                        else
                            t = 2;
                        break;
                    }
                }
                result = result * d + t;
            }
        }
        return result;
    }

    public int I2int(String x) {
        int ans = 0;
        ans = Integer.valueOf(x).intValue();
        return ans;
    }

    // 文本数字信息转成int类型，不考虑中文阿拉伯数字混用的情况
    public int W2int(String x) {
        int flag = 0, flag1 = 0;
        for (int i = 0; i < x.length(); i++) {
            if (x.charAt(i) >= '0' && x.charAt(i) <= '9') flag = 1;
            else flag1 = 1;
        }
        if (flag == 1 && flag1 == 0)
            return I2int(x);
        else if (flag == 0 && flag1 == 1)
            return C2int(x);
        else return 0;
    }

    // 从p中用y的原子规则提取出字符串
    public String reg(String y, String p) {
        Pattern pattern = Pattern.compile(y);
        Matcher matcher = pattern.matcher(p);
        String buffer = new String();
        int flag = 0;
        while (matcher.find()) {
            flag = 1;
            buffer = matcher.group();
        }
        if (flag == 1)
            return buffer;
        else
            return null;
    }

    public void work0(Date re) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(re);
        switch (col) {
            case 0: {
                date[0] = cal.getTime();
                for (int i = 0; i < 6; i++)
                    chage[i] = 1;
                break;
            }

            case 1: {
                break;
            } // p[3][1]，差的就是前边的地名，如果只做杭州的提取应该没差吧
            case 2: {
                break;
            } // p[4][12] pw13 +某个具体时间
            case 3: {
                break;
            } // 直接过了
            case 4: {
                break;
            } // 频率不好做啊。。加重动作
            case 5: {
                break;
            }
            case 6: {
                break;
            } // after +提取的一段时间
            case 7: {
                break;
            } // before +提取的一段时间 并到时间段里边
        }
    }

    public void work1(Date re) {
        String sx = new String();
        sx = text;
        // TimeData d = new TimeData();
        Calendar cal = Calendar.getInstance();
        Calendar cal1 = Calendar.getInstance();
        cal.setTime(re);
        cal1.setTime(re);
        switch (col) {
            case 0: {

                break;
            }

            case 1: {
                Pattern pattern = Pattern.compile(TimeData.y[4][0]);
                Matcher matcher = pattern.matcher(sx);
                String buffer = new String();

                if (sx.contains("去年") == true) {
                    cal.add(Calendar.YEAR, -1);
                    cal1.add(Calendar.YEAR, -1);
                } else if (sx.contains("前年") == true) {
                    cal.add(Calendar.YEAR, -2);
                    cal1.add(Calendar.YEAR, -2);
                } else if (sx.contains("今年") == true) {
                    cal.add(Calendar.YEAR, 0);
                    cal1.add(Calendar.YEAR, 0);
                } else if (sx.contains("明年") == true) {
                    cal.add(Calendar.YEAR, +1);
                    cal1.add(Calendar.YEAR, +1);
                } else {
                    while (matcher.find()) {
                        buffer = matcher.group(0);
                    }
                    cal.set(Calendar.YEAR, W2int(buffer));
                    cal1.set(Calendar.YEAR, W2int(buffer));
                }
                if (sx.contains("第一季度") == true) {
                    cal.set(Calendar.MONTH, 0);
                    cal1.set(Calendar.MONTH, 2);
                }
                if (sx.contains("第二季度") == true) {
                    cal.set(Calendar.MONTH, 3);
                    cal1.set(Calendar.MONTH, 5);
                }
                if (sx.contains("第三季度") == true) {
                    cal.set(Calendar.MONTH, 6);
                    cal1.set(Calendar.MONTH, 8);
                }
                if (sx.contains("第四季度") == true) {
                    cal.set(Calendar.MONTH, 9);
                    cal1.set(Calendar.MONTH, 11);
                }
                if (sx.contains("上半年") == true) {
                    cal.set(Calendar.MONTH, 0);
                    cal1.set(Calendar.MONTH, 5);
                }
                if (sx.contains("下半年") == true) {
                    cal.set(Calendar.MONTH, 6);
                    cal1.set(Calendar.MONTH, 11);
                }
                date[0] = cal.getTime();
                date[1] = cal1.getTime();
                flag = 1;
                chage[0] = 1;
                chage[1] = 1;
                break;
            }

            case 2: {

                break;
            }

            case 3: {

                break;
            }

            case 4: {

                Pattern pattern = Pattern.compile(TimeData.y[4][0]);
                Matcher matcher = pattern.matcher(sx);
                String buffer = new String();
                int canshu[] = {Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};
                int f = 0;
                while (matcher.find()) {
                    buffer = matcher.group();
                    // System.out.println(buffer);
                    if (sx.contains("清晨") == true || sx.contains("凌晨") == true) {
                        cal.set(canshu[f], W2int(buffer));
                    } else if (sx.contains("上午") == true || sx.contains("早上") == true) {
                        cal.set(canshu[f], W2int(buffer));
                    } else if (sx.contains("中午") == true || sx.contains("晌午") == true) {
                        cal.set(canshu[f], W2int(buffer));
                    } else if (sx.contains("下午") == true || sx.contains("傍晚") == true || sx.contains("午后") == true) {
                        cal.set(canshu[f], W2int(buffer) + 12);
                    } else if (sx.contains("夜里") == true || sx.contains("晚上") == true || sx.contains("午夜") == true || sx.contains("半夜") == true || sx.contains("夜晚") == true) {
                        cal.set(canshu[f], W2int(buffer) + 12);

                    }
                    chage[f + 3] = 1;
                    f++;
                }
                date[0] = cal.getTime();
                break;
            }

            case 5: {

                int day = 0, month = 0;

                if (sx.compareTo("元旦") == 0) {
                    month = 1;
                    day = 1;
                } else if (sx.compareTo("五一节") == 0 || sx.compareTo("劳动节") == 0) {
                    month = 5;
                    day = 1;
                } else if (sx.compareTo("教师节") == 0) {
                    month = 9;
                    day = 1;
                } else if (sx.compareTo("国庆节") == 0 || sx.compareTo("十一国庆") == 0) {
                    month = 10;
                    day = 1;
                } else if (sx.compareTo("圣诞节") == 0) {
                    month = 12;
                    day = 25;
                } else if (sx.compareTo("妇女节") == 0) {
                    month = 3;
                    day = 8;
                } else if (sx.compareTo("儿童节") == 0) {
                    month = 6;
                    day = 1;
                } else if (sx.compareTo("情人节") == 0) {
                    month = 2;
                    day = 14;
                } else if (sx.compareTo("建军节") == 0) {
                    month = 8;
                    day = 1;
                } else if (sx.compareTo("建党节") == 0) {
                    month = 8;
                    day = 1;
                } else
                    break;
                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.MONTH, month - 1);
                date[0] = cal.getTime();
                chage[1] = 1;
                chage[2] = 1;
                break;

            }
            case 6: {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    date[0] = df.parse(text);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }

        }
    }

    public void work2(Date re) {
        String sx = new String();
        sx = text;
        // TimeData d = new TimeData();
        Calendar cal = Calendar.getInstance();
        cal.setTime(re);
        switch (col) {
            case 0: {
                //一/去年前的前天

                break;
            }
            case 1: {
                //一/去年前的明天
                break;
            }
            case 2: {
                //一/明年前的前天
                break;
            }
            case 3: {
                //一/明年前的明天
                break;
            }
            case 4: {
                //时间表达过于模糊
                break;
            }
            case 5: {
                //时间表达过于模糊
                break;
            }
            case 6: {
                int day = -7;
                String ans = reg(TimeData.y[2][1], sx);
                if (ans == "上个") {
                    //判断文本时间是周几
                    Calendar now = Calendar.getInstance();
                    boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
                    int weekDay = now.get(Calendar.DAY_OF_WEEK);
                    if (isFirstSunday) {
                        weekDay = weekDay - 1;
                        if (weekDay == 0) {
                            weekDay = 7;
                        }
                    }

                    String ans1 = reg(TimeData.y[2][5], sx);
                    if (ans1 == "周六" || ans1 == "星期六" || ans1 == "礼拜六") {
                        day = day + 6 - weekDay;
                    } else
                        day = day + 7 - weekDay;
                    cal.add(Calendar.DAY_OF_MONTH, day);
                    date[0] = cal.getTime();
                    chage[2] = 1;
                }
                break;
            }
            case 7: {
                int day = 7;
                String ans = reg(TimeData.y[2][2], sx);
                if (ans != null) {
                    //判断文本时间是周几
                    Calendar now = Calendar.getInstance();
                    boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
                    int weekDay = now.get(Calendar.DAY_OF_WEEK);
                    if (isFirstSunday) {
                        weekDay = weekDay - 1;
                        if (weekDay == 0) {
                            weekDay = 7;
                        }
                    }

                    String ans1 = reg(TimeData.y[2][5], sx);
                    if (ans1.contains("六") == true) {
                        day = day + 6 - weekDay;
                    } else
                        day = day + 7 - weekDay;
                    cal.add(Calendar.DAY_OF_MONTH, day);
                    date[0] = cal.getTime();
                    chage[2] = 1;
                }
                break;
            }
            case 8: {
                int day = 0;
                //判断文本时间是周几
                Calendar now = Calendar.getInstance();
                boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
                int weekDay = now.get(Calendar.DAY_OF_WEEK);
                if (isFirstSunday) {
                    weekDay = weekDay - 1;
                    if (weekDay == 0) {
                        weekDay = 7;
                    }
                }
                String ans = reg(TimeData.y[2][5], sx);
                if (ans.contains("六") == true) {
                    day = day + 6 - weekDay;

                } else
                    day = day + 7 - weekDay;

                cal.add(Calendar.DAY_OF_MONTH, day);
                date[0] = cal.getTime();
                chage[2] = 1;
                break;
            }
            case 9: {
                //具体时间
                /*for (int i = 1; i <= 3; i++)
                {
					String a = reg(d.y[4][i], sx);
					if (a != null)
					{
						int u = W2int(reg(d.y[4][0], a));
						if (i == 1)
							cal.set(Calendar.YEAR,u);
						else if (i == 2)
							cal.set(Calendar.MONTH,u-1);
						else
							cal.set(Calendar.DAY_OF_MONTH,u);
					}
				}
				//等到删除了重复文本信息

				String ans = reg(d.y[4][0], sx);
				int u = W2int(reg(d.y[4][0], ans));//为什么这里是两个啊。。。。。。
				u = -u;//下个 不用这个代码
				String ans1 = reg(d.y[2][0], sx);
				if(ans1 == "年")
				{

				}
				else if(ans1 == "月")
				{

				}
				else if(ans1 == "天")
				{
					cal.add(Calendar.DAY_OF_MONTH, u);
				}
				else
				{

				}*/
                break;
            }
            case 10: {
                //具体时间 之后的六年
                break;
            }
            case 11: {
                //时间表达过于模糊
                break;
            }
            case 12: {
                //时间表达过于模糊
                break;
            }
            case 13: {
                //时间表达过于模糊
                break;
            }
            case 14: {
                //时间表达过于模糊
                break;
            }
            case 15: {
                //时间表达过于模糊
                break;
            }
        }
    }

    public void work3(Date re) {
        String sx = new String();
        sx = text;
        // TimeData d = new TimeData();
        Calendar cal = Calendar.getInstance();
        cal.setTime(re);
        switch (col) {
            case 0: {
                Calendar now = Calendar.getInstance();
                //一周第一天是否为星期天
                boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
                //获取周几
                int weekDay = now.get(Calendar.DAY_OF_WEEK);
                //若一周第一天为星期天，则-1
                if (isFirstSunday) {
                    weekDay = weekDay - 1;
                    if (weekDay == 0) {
                        weekDay = 7;
                    }
                }
                int i = 0;
                String ans = reg(TimeData.y[3][i], sx);

                if (ans != null) {
                    int a = W2int(reg(TimeData.y[3][1], sx)) - weekDay;
                    int day = 0;
                    if (reg(TimeData.y[3][1], sx) != null) {
                        day = day + a;

                        if (reg(TimeData.y[3][2], sx) != null) {
                            cal.add(Calendar.DAY_OF_MONTH, day);
                            date[0] = cal.getTime();
                            break;
                        }
                        if (reg(TimeData.y[3][3], sx) != null) {
                            day = -7;
                            day = day + a;
                            cal.add(Calendar.DAY_OF_MONTH, day);
                            date[0] = cal.getTime();
                            break;
                        }
                        if (reg(TimeData.y[3][4], sx) != null) {
                            day = 7;
                            day = day + a;
                            cal.add(Calendar.DAY_OF_MONTH, day);
                            date[0] = cal.getTime();
                            break;
                        }
                        cal.add(Calendar.DAY_OF_MONTH, day);
                        date[0] = cal.getTime();
                        chage[2] = 1;
                    }
                }
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                date[0] = cal.getTime();
                for (int i = 0; i < 6; i++)
                    chage[i] = 1;
            }
            break;
            case 5: {
                break;
            }
            case 6: {
                break;
            }
            case 7: {
                break;
            }
        }
    }

    public void work4(Date re) {
        String sx = new String();
        sx = text;
        // TimeData d = new TimeData();
        Calendar cal = Calendar.getInstance();
        cal.setTime(re);
        switch (col) {
            case 0: {
                int day = 0;
                if (sx.compareTo("大前天") == 0)
                    day = -3;
                else
                    day = 3;
                cal.add(Calendar.DAY_OF_MONTH, day);
                date[0] = cal.getTime();
                chage[2] = 1;
                break;
            }
            case 1: {
                for (int i = 4; i <= 8; i++) {
                    String ans = reg(TimeData.y[4][i], sx);
                    if (ans != null) {
                        int day = i - 6;
                        cal.add(Calendar.DAY_OF_MONTH, day);
                        date[0] = cal.getTime();
                        chage[2] = 1;
                        break;
                    }
                }
                break;
            }
            case 2: {
                Pattern pattern = Pattern.compile(TimeData.y[4][0]);
                Matcher matcher = pattern.matcher(sx);
                String buffer = new String();
                int flag = 0;
                while (matcher.find()) {
                    buffer = matcher.group();
                    if (flag == 0) {
                        int fx = W2int(buffer);
                        if (fx < 100)
                            fx += 2000;
                        cal.set(Calendar.YEAR, fx);
                        chage[0] = 1;
                    } else if (flag == 1) {
                        cal.set(Calendar.MONTH, W2int(buffer) - 1);
                        chage[1] = 1;
                    } else {
                        cal.set(Calendar.DAY_OF_MONTH, W2int(buffer));
                        chage[2] = 1;
                    }
                    flag++;
                }
                date[0] = cal.getTime();
                break;
            }
            case 3: {
                for (int i = 1; i <= 3; i++) {
                    String a = reg(TimeData.y[4][i], sx);
                    if (a != null) {
                        int u = W2int(reg(TimeData.y[4][0], a));
                        if (i == 1) {
                            if (u < 100)
                                u += 2000;
                            cal.set(Calendar.YEAR, u);
                            chage[0] = 1;
                        } else if (i == 2) {
                            cal.set(Calendar.MONTH, u - 1);
                            chage[1] = 1;
                        } else {
                            cal.set(Calendar.DAY_OF_MONTH, u);
                            chage[2] = 1;
                        }
                    }
                }
                date[0] = cal.getTime();
                break;
            }
            case 4: {
                date[0] = cal.getTime();
                for (int i = 0; i < 6; i++)
                    chage[i] = 1;
                break;
            }
            case 5: {
                Pattern pattern = Pattern.compile(TimeData.y[4][0]);
                Matcher matcher = pattern.matcher(sx);
                String buffer = new String();
                int flag = 0;
                while (matcher.find()) {
                    buffer = matcher.group();
                    if (flag == 0) {
                        cal.set(Calendar.HOUR_OF_DAY, W2int(buffer));
                        chage[3] = 1;
                    } else if (flag == 1) {
                        cal.set(Calendar.MINUTE, W2int(buffer));
                        chage[4] = 1;
                    }
                    flag++;
                }
                date[0] = cal.getTime();
                break;
            }
            case 6: {
                for (int i = 12; i <= 14; i++) {
                    Pattern pattern = Pattern.compile(TimeData.y[4][i]);
                    Matcher matcher = pattern.matcher(sx);
                    String buffer = null;
                    while (matcher.find()) {
                        buffer = matcher.group();
                        // buffer.append(" ");
                    }
                    sx = matcher.replaceAll("");
                    if (buffer != null) {
                        int u = W2int(reg(TimeData.y[4][0], buffer));
                        if (i == 12) {
                            cal.set(Calendar.HOUR_OF_DAY, u);
                            chage[3] = 1;
                        } else if (i == 13) {
                            cal.set(Calendar.MINUTE, u);
                            chage[4] = 1;
                        } else {
                            cal.set(Calendar.SECOND, u);
                            chage[5] = 1;
                        }
                    }
                }
                String a = reg(TimeData.y[4][0], sx);
                if (a != null) {
                    cal.set(Calendar.MINUTE, W2int(a));
                    chage[4] = 1;
                }
                date[0] = cal.getTime();
                break;
            }
            case 7: {
                String a = reg(TimeData.y[4][0], sx);
                int u = W2int(a);
                a = reg(TimeData.y[4][15], sx);
                Calendar cal2 = Calendar.getInstance();
                cal2.equals(cal);
                if (a != null) {
                    cal2.add(Calendar.SECOND, u);
                    chage[5] = 1;
                } else {
                    cal2.add(Calendar.DAY_OF_MONTH, 7 * u);
                    chage[2] = 1;
                }
                date[0] = cal.getTime();
                date[1] = cal2.getTime();
                flag = 1;
                break;
            }
            case 8: {

                break;
            }
            case 9: {

                break;
            }
            case 10: {

                break;
            }
            case 11: {
                String a;
                a = reg(TimeData.y[4][41], sx);
                Calendar cal2 = Calendar.getInstance();
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                if (a != null) {
                    cal.set(Calendar.HOUR_OF_DAY, 7);
                    cal2.setTime(cal.getTime());
                    cal2.add(Calendar.HOUR_OF_DAY, 2);
                } else {
                    a = reg(TimeData.y[4][42], sx);
                    if (a != null) {
                        cal.set(Calendar.HOUR_OF_DAY, 17);
                        cal2.setTime(cal.getTime());
                        cal2.add(Calendar.HOUR_OF_DAY, 2);
                    } else {
                        cal = null;
                        cal2 = null;
                    }
                    chage[3] = 1;
                }
                date[0] = cal.getTime();
                date[1] = cal2.getTime();
                flag = 1;
                break;
            }
            case 12: {
                for (int i = 4; i <= 8; i++) {
                    String ans = reg(TimeData.y[4][i], sx);
                    if (ans != null) {
                        int day = i - 6;
                        cal.add(Calendar.DAY_OF_MONTH, day);
                        date[0] = cal.getTime();
                        chage[2] = 1;
                        break;
                    }
                }
                int flag = 0;
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(cal.getTime());
                for (int i = 18; i <= 23; i++) {
                    String ans = reg(TimeData.y[4][i], sx);
                    if (ans != null) {
                        flag = 1;
                        if (i == 18) {
                            cal.set(Calendar.HOUR_OF_DAY, 16);
                            cal2.set(Calendar.HOUR_OF_DAY, 19);
                        } else if (i == 19) {
                            cal.set(Calendar.HOUR_OF_DAY, 5);
                            cal2.set(Calendar.HOUR_OF_DAY, 8);
                        } else if (i == 20) {
                            cal.set(Calendar.HOUR_OF_DAY, 11);
                            cal2.set(Calendar.HOUR_OF_DAY, 13);
                        } else if (i == 21) {
                            cal.set(Calendar.HOUR_OF_DAY, 8);
                            cal2.set(Calendar.HOUR_OF_DAY, 11);
                        } else if (i == 22) {
                            cal.set(Calendar.HOUR_OF_DAY, 13);
                            cal2.set(Calendar.HOUR_OF_DAY, 16);
                        } else if (i == 23) {
                            cal.set(Calendar.HOUR_OF_DAY, 19);
                            cal2.set(Calendar.HOUR_OF_DAY, 21);
                        }
                        chage[3] = 1;
                    }
                }
                if (flag == 0) {
                    String a = reg(TimeData.y[4][45], sx);
                    if (a != null) {
                        cal.set(Calendar.HOUR_OF_DAY, 5);
                        cal2.set(Calendar.HOUR_OF_DAY, 8);
                    } else {
                        if (sx.contains("早")) {
                            cal.set(Calendar.HOUR_OF_DAY, 5);
                            cal2.set(Calendar.HOUR_OF_DAY, 8);
                        } else {
                            cal.set(Calendar.HOUR_OF_DAY, 19);
                            cal2.set(Calendar.HOUR_OF_DAY, 21);
                        }
                    }
                    chage[3] = 1;
                }
                date[0] = cal.getTime();
                date[1] = cal2.getTime();
                flag = 1;
                break;
            }
            case 13: {

                break;
            }
            case 14: {
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(cal.getTime());
                cal2.add(Calendar.DAY_OF_MONTH, 1);
                chage[2] = 1;
                date[0] = cal.getTime();
                date[1] = cal2.getTime();
                flag = 1;
                break;
            }
            case 15: {

                break;
            }
        }
    }

    //用于时间信息之间的合并
    public TimeNode merge(TimeNode x, Date re) {
        if (x.date[0] == null)
            return this;
        if (date[0] == null)
            return x;
        Calendar cal = Calendar.getInstance();
        cal.setTime(re);
        int cs[] = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};
        if (x.flag != flag) {
            if (x.flag == 1)
                return this;
            else
                return x;
        } else {
            if (flag == 1)
                return this;
            else {
                for (int i = 0; i < 6; i++) {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(x.date[0]);
                    if (x.chage[i] == 1)
                        cal.set(cs[i], cal1.get(cs[i]));
                    cal1.setTime(date[0]);
                    if (chage[i] == 1)
                        cal.set(cs[i], cal1.get(cs[i]));
                    //System.out.println(x.change[i]);
                }
                date[0] = cal.getTime();
                start = Math.min(start, x.start);
                end = Math.max(end, x.end);
                text += x.text;
                return this;
            }
        }
    }

    // 用于控制台输出测试
    public String test() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date[0]);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (date[0] != null) {
            System.out.println(df.format(cal.getTime()));
            return df.format(cal.getTime());
        }
        else
            return null;
//        if (x == 0) {
//            //System.out.println(df.format(cal.getTime()));
//            return df.format(cal.getTime());
//        } else {
//            cal2.setTime(date[1]);
//           // System.out.println(df.format(cal.getTime()) + "-" + df.format(cal2.getTime()));
//            return df.format(cal.getTime()) + "-" + df.format(cal2.getTime());
//        }
    }

//    public void print() {
//        System.out.println(text + "   " + start + " " + end + " " + row + " " + col);
//        if (date[0] != null && date[1] == null)
//            println(0);
//        else if (date[1] != null)
//            println(1);
//        else
//            System.out.println("未数字化或数字化失败");
//    }
}
