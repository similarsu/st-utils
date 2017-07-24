package cn.st.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

class DateFragment{
    private Date startDate;
    private Date endDate;
    private String startDateStr;
    private String endDateStr;

    public DateFragment(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getStartDateStr() {
        return DateFormatUtils.format(startDate,Constants.DATE_TIME_PATTERN);
    }

    public String getEndDateStr() {
        return DateFormatUtils.format(endDate,Constants.DATE_TIME_PATTERN);
    }
}