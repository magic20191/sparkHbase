package org.kin.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


/**
 * @description:
 * @author: Andy
 * @time: 2021/9/24 17:08
 */
public class TimeUtils {


    public static void main(String[] args) throws ParseException {

//        TimeUtils timeutis = new TimeUtils();
//
//        System.out.println(getField(new Date(), Calendar.MINUTE));
//
////        2020-12-04 13:25
//        Date a = timeutis.StrToDate1("2020-12-04 13:25",2);
//        System.out.println(a);
//
//        System.out.println(a.getTime()/1000);
//
//        Date date1 = new Date(a.getTime());
//        System.out.println(date1.toString());
//
//        System.out.println(timeutis.getNextDay(a,-1));
//        System.out.println(timeutis.getTodayStartTime(timeutis.getNextDay(a,-1)).getTime()/1000);   //传入日期前一天的0点
//        System.out.println(timeutis.getEndTime(timeutis.getNextDay(a,-1)).getTime()/1000);     //传入时间前一天的最后一刻
//
//        System.out.println(getcaltime("2020-12-04 13:25"));
//        System.out.println(LocalDate.now.minus(1, ChronoUnit.DAYS) format DateTimeFormatter.ofPattern("yyyyMMdd"));


        //处理day_id
//        LocalDate day_id = "";
        //获取昨天的日期
//        LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);
//        yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")) ;
        System.out.println( getcaltime());


    }


    /**
     * 获取当前日期的前一天
     * @param pattern 需要返回的日期格式，例如：yyyy-MM-dd HH:mm:ss
     * @return 前一天日期字符串
     */
    public static String beforeDayByNow(String pattern){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //得到前一天
        Date date = calendar.getTime();
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }


    /**
     *
     * 根据传入时间返回其前一日的0点与24点 ，以 ，符号分割
     * @param s 传入字符串 yymmdd /yymmdd hh:mm /yymmdd hh:mm:ss
     * @return s "以字符,分割 前为0时，后为24时 ，long格式时间"
     *
     * 例子 传入 2020-12-04 13:25
     * 返回 2020-12-03 00:00 2020-12-04 00:00
     *
     */
    public static String getcaltime(String s) {
        Date a = StrToDate1(s,3);
        String b = String.valueOf(getTodayStartTime(getNextDay(a,-1)).getTime()/1000);   //传入日期前一天的0点
        String c = String.valueOf(getTodayStartTime(getNextDay(a,-0)).getTime()/1000) ;     //传入时间的0点
        return  b +","+ c ;
    }

    /**
     *
     * 根据传入时间返回其前一日的0点与24点 ，以 ，符号分割
     * @return s "以字符,分割 前为0时，后为24时 ，long格式时间"
     *
     * 例子 传入 2020-12-04 13:25
     * 返回 2020-12-03 00:00 2020-12-04 00:00
     *
     */
    public static String getcaltime() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String b = String.valueOf(getTodayStartTime(getNextDay(date,-1)).getTime()/1000);   //传入日期前一天的0点
        String c = String.valueOf(getTodayStartTime(getNextDay(date,-0)).getTime()/1000) ;     //传入时间的0点
        return  b +","+ c ;
    }


    // private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //private static SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyy-MM-dd");

    // private static SimpleDateFormat sdfShiFen = new SimpleDateFormat("HH:mm");

    //private static SimpleDateFormat sdfStr = new SimpleDateFormat("yyyyMMddHHmmss");

    //  private static SimpleDateFormat new SimpleDateFormat("yyyyMMdd") = new SimpleDateFormat("yyyyMMdd");

    // private static SimpleDateFormat new SimpleDateFormat("yyyy年MM月dd日") = new SimpleDateFormat("yyyy年MM月dd日");

    //字符串转换date
    public static Date StrToDate1(String s,int type) {
        try {
            if(type ==1){
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
            }else if(type ==2){
                return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(s);
            }else if(type ==3){
                return new SimpleDateFormat("yyyyMMdd").parse(s);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date(0);
    }





}
