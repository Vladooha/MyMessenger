package com.vladooha.msg_myownwork;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vladooha.msg_myownwork.WebServer.MyLogs;

/**
 * Created by Vladooha on 22.07.2018.
 */

public class UserFullInfoObj extends UserObj {
    DataContainer birth;

    public UserFullInfoObj(Resources resCont) {
        super(resCont);
    }

    public void setAsUnknownUser() {
        super.setAsUnknownUser();
        birth = new DataContainer();
    }

    public boolean setUserInfo(String userInfo) {
        super.setUserInfo(userInfo);
        Log.d(MyLogs, "setUserInfo() (Full) got: " + userInfo);

        Matcher matchBuff;

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
        Pattern pattMonth = Pattern.compile(res.getString(R.string.key_bmonth) + res.getString(R.string.patt_month) + ":");
        matchBuff = pattMonth.matcher(userInfo);
        if (matchBuff.find()) {
            String monthStr = matchBuff.group()
                    .replace(res.getString(R.string.key_bmonth), "")
                    .replace(":", "");
            String[] months = res.getStringArray(R.array.monthlist);
            for (int i = 0; i < months.length; ++i) {
                if (monthStr.equals(months[i])) {
                    month = i;
                    Log.d(MyLogs, "Month num is: " + String.valueOf(month));
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

    public DataContainer getBirth() {
        return birth;
    }
}
