package com.vladooha.msg_myownwork;

import android.provider.ContactsContract;

/**
 * Created by Vladooha on 19.07.2018.
 */

public class DataContainer {
    int day;
    int month;
    int year;

    static final public int UNKNOWN_VALUE = 0;

    public DataContainer(int u_day, int u_month, int u_year) {
        day = u_day;
        month = u_month;
        year = u_year;
        if (day > 31 || day < UNKNOWN_VALUE) {
            day = UNKNOWN_VALUE;
        }
        if (month > 12 || month < UNKNOWN_VALUE) {
            month = UNKNOWN_VALUE;
        }
        if (year > 3000 || year < UNKNOWN_VALUE) {
            month = UNKNOWN_VALUE;
        }
    }

    public DataContainer(int u_year) {
        this(UNKNOWN_VALUE, UNKNOWN_VALUE, u_year);
    }

    public DataContainer () {
        this(UNKNOWN_VALUE, UNKNOWN_VALUE, UNKNOWN_VALUE);
    }

    public boolean setByString(String data) {
        String[] dataParts = data.split(".");
        if (dataParts.length != 3) {
            return false;
        }
        try {
            day = Integer.parseInt(dataParts[0]);
            if (day > 31 || day < UNKNOWN_VALUE) {
                return false;
            }
            month = Integer.parseInt(dataParts[1]);
            if (month > 12 || month < UNKNOWN_VALUE) {
                return false;
            }
            year = Integer.parseInt(dataParts[2]);
            if (year > 3000 || year < UNKNOWN_VALUE) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getString() {
        StringBuilder result = new StringBuilder();
        result.append(day > UNKNOWN_VALUE ? String.valueOf(day) : "-");
        result.append(".");
        result.append(month > UNKNOWN_VALUE ? String.valueOf(month) : "-");
        result.append(".");
        result.append(year > UNKNOWN_VALUE ? String.valueOf(year) : "-");
        return result.toString();
    }

    public int getDay() { return day; }

    public int getMonth() { return month; }

    public int getyear() { return year; }
 }
