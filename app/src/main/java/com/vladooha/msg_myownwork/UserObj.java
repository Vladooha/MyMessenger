package com.vladooha.msg_myownwork;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vladooha.msg_myownwork.WebServer.MyLogs;

/**
 * Created by Vladooha on 29.03.2018.
 */

public class UserObj {
    String nick;
    DataContainer birth;
    Bitmap pic;

    Resources res;

    public UserObj(Resources resCont) {
        res = resCont;
    }

    public void setAsUnknownUser() {
        nick = "*UNKNOWN_USER*";
        birth = new DataContainer();
        pic = null;
    }

    public boolean setUserInfo(String userInfo) {
        Log.d(MyLogs, "setUserInfo() got: " + userInfo);

        Matcher matchBuff;

        Pattern pattNick = Pattern.compile(res.getString(R.string.key_nick) + res.getString(R.string.patt_nick) + ":");
        matchBuff = pattNick.matcher(userInfo);
        if (matchBuff.find()) {
            nick = matchBuff.group()
                    .replace(res.getString(R.string.key_nick), "")
                    .replace(":", "");
        } else {
            setAsUnknownUser();
            return false;
        }


        Pattern pattPic = Pattern.compile(res.getString(R.string.key_url) + res.getString(R.string.patt_url) + ":");
        matchBuff = pattPic.matcher(userInfo);
        if (matchBuff.find()) {
            // TODO: Think about it
            pic = getPicByUrl(userInfo
                    .replace(res.getString(R.string.key_url), "")
                    .replace(":", ""));
        }

        int day = DataContainer.UNKNOWN_VALUE;
        int month = DataContainer.UNKNOWN_VALUE;
        int year = DataContainer.UNKNOWN_VALUE;
        Pattern pattDay = Pattern.compile(res.getString(R.string.key_bday) + res.getString(R.string.patt_day) + ":");
        matchBuff = pattDay.matcher(userInfo);
        if (matchBuff.find()) {
            day = Integer.parseInt(matchBuff.group()
                    .replace(res.getString(R.string.key_bday), "")
                    .replace(":", ""));
        }
        // TODO: Ispravit' etu zalupu v budushem
        Pattern pattMonth = Pattern.compile(res.getString(R.string.key_bmonth)
                + res.getString(R.string.month_jan) + "||"
                + res.getString(R.string.month_feb) + "||"
                + res.getString(R.string.month_mar) + "||"
                + res.getString(R.string.month_apr) + "||"
                + res.getString(R.string.month_may) + "||"
                + res.getString(R.string.month_jun) + "||"
                + res.getString(R.string.month_jul) + "||"
                + res.getString(R.string.month_aug) + "||"
                + res.getString(R.string.month_sep) + "||"
                + res.getString(R.string.month_oct) + "||"
                + res.getString(R.string.month_nov) + "||"
                + res.getString(R.string.month_dec) + ":");
        matchBuff = pattMonth.matcher(userInfo);
        if (matchBuff.find()) {
            String monthStr = matchBuff.group()
                    .replace(res.getString(R.string.key_bmonth), "")
                    .replace(":", "");
            String[] months = res.getStringArray(R.array.monthlist);
            for (int i = 0; i < months.length; ++i) {
                if (monthStr.equals(months[i])) {
                    month = i + 1;
                }
            }
        }

        Pattern pattYear = Pattern.compile(res.getString(R.string.key_byear) + res.getString(R.string.patt_year) + ":");
        matchBuff = pattYear.matcher(userInfo);
        if (matchBuff.find()) {
            year = Integer.parseInt(matchBuff.group()
                    .replace(res.getString(R.string.key_byear), "")
                    .replace(":", ""));
        }

        birth = new DataContainer(day, month, year);

        return true;
    }

//    public static boolean parseUserInfo(String query, int field) {
//        String[] usersData = null;
//        String answer = null;
//
//        // TODO a userInfo request
//        if (field == 0) {
//            StringBuilder request = new StringBuilder()
//                    .append(res.getString(R.string.cmd_check))
//                    .append("");
//            // answer = WebServer.makeRequest();
//        }
//    }

    public String getNick() {
        return nick;
    }

    public Bitmap getPic() {
        if (pic != null) {
            return pic.copy(pic.getConfig(), pic.isMutable());
        } else {
            return null;
        }
    }

    public DataContainer getBirth() {
        return birth;
    }

    // Helping methods
    @Nullable
    private Bitmap getPicByUrl(String url) {
        return null;
    }
}
