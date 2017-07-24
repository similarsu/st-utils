package cn.st.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by coolearth on 17-7-21.
 */
public class DateUtilsExt {

    private DateUtilsExt(){

    }

    public static List<DateFragment> getDateFragment(String startDateStr,String endDateStr,String startFragmentStr,String endFragmentStr) throws ParseException {
        Date startDate=DateUtils.parseDate(startDateStr,Constants.DATE_TIME_PATTERN);
        Date endDate=DateUtils.parseDate(endDateStr,Constants.DATE_TIME_PATTERN);
        Date startFragment=DateUtils.parseDate(startFragmentStr,Constants.TIME_PATTERN);
        Date endFragment=DateUtils.parseDate(endFragmentStr,Constants.TIME_PATTERN);
        if(startDate.after(endDate)){
            throw new IllegalArgumentException("startDateStr must not be bigger than endDateStr");
        }
        List<DateFragment> dateFragmentList=new ArrayList<>();
        Date minDate=DateUtils.truncate(startDate, Calendar.DATE);
        Date maxDate=DateUtils.ceiling(endDate, Calendar.DATE);
        Date tmp=minDate;
        while (tmp.before(maxDate)){
            if(!startFragment.after(endFragment)){
                Date startDateTmp=DateUtils.parseDate(DateFormatUtils.format(tmp,Constants.DATE_PATTERN)+" "+DateFormatUtils.format(startDate,Constants
                        .TIME_PATTERN),Constants.DATE_TIME_PATTERN);
                Date endDateTmp=DateUtils.parseDate(DateFormatUtils.format(tmp,Constants.DATE_PATTERN)+" "+DateFormatUtils.format(endDate,Constants
                        .TIME_PATTERN),Constants.DATE_TIME_PATTERN);
                Date startFragmentTmp=DateUtils.parseDate(DateFormatUtils.format(tmp,Constants.DATE_PATTERN)+" "+startFragmentStr,Constants.DATE_TIME_PATTERN);
                Date endFragmentTmp=DateUtils.parseDate(DateFormatUtils.format(tmp,Constants.DATE_PATTERN)+" "+endFragmentStr,Constants.DATE_TIME_PATTERN);
                tmp=DateUtils.addDays(tmp,1);
                Date minDateTmp=null;
                Date maxDateTmp=null;
                if(startFragmentTmp.after(endDateTmp)||endFragmentTmp.before(startDateTmp)){
                    continue;
                }else{
                    if(startFragmentTmp.before(startDateTmp)){
                        minDateTmp=startDateTmp;
                    }else{
                        minDateTmp=startFragmentTmp;
                    }
                    if(endFragmentTmp.before(endDateTmp)){
                        maxDateTmp=endFragmentTmp;
                    }else{
                        maxDateTmp=endDateTmp;
                    }
                }


                dateFragmentList.add(new DateFragment(minDateTmp,maxDateTmp));
            }else{
                Date startDateTmp=DateUtils.parseDate(DateFormatUtils.format(tmp,Constants.DATE_PATTERN)+" "+DateFormatUtils.format(startDate,Constants
                        .TIME_PATTERN),Constants.DATE_TIME_PATTERN);
                Date endDateTmp=DateUtils.parseDate(DateFormatUtils.format(tmp,Constants.DATE_PATTERN)+" "+DateFormatUtils.format(endDate,Constants
                        .TIME_PATTERN),Constants.DATE_TIME_PATTERN);
                Date startFragmentTmp=DateUtils.parseDate(DateFormatUtils.format(tmp,Constants.DATE_PATTERN)+" "+endFragmentStr,Constants.DATE_TIME_PATTERN);
                Date endFragmentTmp=DateUtils.parseDate(DateFormatUtils.format(tmp,Constants.DATE_PATTERN)+" "+startFragmentStr,Constants.DATE_TIME_PATTERN);
                tmp=DateUtils.addDays(tmp,1);
                if(startDateTmp.after(startFragmentTmp)&&endDateTmp.before(endFragmentTmp)){
                    continue;
                }else {

                    if(startDateTmp.before(startFragmentTmp)&&endDateTmp.after(endFragmentTmp)){
                        dateFragmentList.add(new DateFragment(startDateTmp,startFragmentTmp));
                        dateFragmentList.add(new DateFragment(endFragmentTmp,endDateTmp));
                    }else{
                        Date minDateTmp=null;
                        Date maxDateTmp=null;
                        if(startDateTmp.after(startFragmentTmp)&&startDateTmp.before(endFragmentTmp)){
                            minDateTmp=endFragmentTmp;
                        }else{
                            minDateTmp=startDateTmp;
                        }
                        if(endDateTmp.after(startFragmentTmp)&&endDateTmp.before(endFragmentTmp)){
                            maxDateTmp=startFragmentTmp;
                        }else {
                            maxDateTmp=endDateTmp;
                        }
                        dateFragmentList.add(new DateFragment(minDateTmp,maxDateTmp));
                    }
                }
            }


        }
        return dateFragmentList;
    }

    public static void printDates(List<DateFragment> dateFragmentList){
        for (DateFragment dateFragment:dateFragmentList) {
            System.out.println(dateFragment.getStartDateStr()+"~"+dateFragment.getEndDateStr());
        }
    }

    public static void main(String[] args) throws ParseException {
        List<DateFragment> dateFragmentList=getDateFragment("2012-10-14 12:00:00","2012-10-17 13:00:00","12:30:00","12:10:00");
        printDates(dateFragmentList);
    }


}
