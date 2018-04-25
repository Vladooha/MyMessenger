package com.vladooha.msg_myownwork;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vladooha on 29.03.2018.
 */

public class UserObj {
    String uid;
    String nick;
    Calendar birth;
    String lastMess;
    Bitmap pic;

    boolean nickReq;
    boolean birthReq;
    boolean lastMessReq;
    boolean picReq;

    Resources res;

    public UserObj(Resources resCont) {
        res = resCont;
        nickReq = birthReq = lastMessReq = picReq = false;
    }

    private boolean setUserInfo(String userInfo) {
        Matcher matchBuff;

        Pattern pattUid = Pattern.compile(res.getString(R.string.key_uid) + res.getString(R.string.patt_uid) + ":");
        matchBuff = pattUid.matcher(userInfo);
        if (matchBuff.find()) {
            uid = matchBuff.group()
                    .replace(res.getString(R.string.key_uid), "")
                    .replace(":", "");
        } else {
            uid = "0";
            return false;
        }

        if (nickReq) {
            Pattern pattNick = Pattern.compile(res.getString(R.string.key_nick) + res.getString(R.string.patt_nick) + ":");
            matchBuff = pattNick.matcher(userInfo);
            if (matchBuff.find()) {
                nick = matchBuff.group()
                        .replace(res.getString(R.string.key_nick), "")
                        .replace(":", "");
            }
        }

        if (picReq) {
            Pattern pattPic = Pattern.compile(res.getString(R.string.key_url) + res.getString(R.string.patt_url) + ":");
            matchBuff = pattPic.matcher(userInfo);
            if (matchBuff.find()) {
                // TODO think about it
                pic = getPicByUrl(userInfo
                        .replace(res.getString(R.string.key_url), "")
                        .replace(":", ""));
            }
        }

        if (birthReq) {
            int day = -1;
            int month = -1;
            int year = -1;
            Pattern pattDay = Pattern.compile(res.getString(R.string.key_bday) + res.getString(R.string.patt_day) + ":");
            matchBuff = pattDay.matcher(userInfo);
            if (matchBuff.find()) {
                day = Integer.parseInt(matchBuff.group()
                        .replace(res.getString(R.string.key_bday), "")
                        .replace(":", ""));
            }
            // TODO ispravit' etu zalupu v budushem
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

            birth.set(day, month, year);
        }

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

    public String getUid() {
        return uid;
    }

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

    public Calendar getBirth() {
        return birth;
    }

    public boolean isLastMessReq() {
        return lastMessReq;
    }

    // Helping methods
    @Nullable
    private Bitmap getPicByUrl(String url) {
        return null;
    }
}
