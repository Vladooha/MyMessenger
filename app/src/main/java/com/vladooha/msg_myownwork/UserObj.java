package com.vladooha.msg_myownwork;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vladooha.msg_myownwork.WebServer.MyLogs;

/**
 * Created by Vladooha on 29.03.2018.
 */

public class UserObj {

    String nick;
    String id;
    Bitmap pic;

    Resources res;

    public UserObj(Resources resCont) {
        res = resCont;
    }

    public void setAsUnknownUser() {
        nick = "*UNKNOWN_USER*";
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
            Log.d(MyLogs, "There is no nick!");
            setAsUnknownUser();
            return false;
        }

        Pattern pattId = Pattern.compile(res.getString(R.string.key_uid) + res.getString(R.string.patt_uid) + ":");
        matchBuff = pattId.matcher(userInfo);
        if (matchBuff.find()) {
            id = matchBuff.group()
                    .replace(res.getString(R.string.key_uid), "")
                    .replace(":", "");
        } else {
            Log.d(MyLogs, "There is no id!");
        }


        Pattern pattPic = Pattern.compile(res.getString(R.string.key_url) + res.getString(R.string.patt_url) + ":");
        matchBuff = pattPic.matcher(userInfo);
        if (matchBuff.find()) {
            // TODO: Think about it
            pic = getPicByUrl(userInfo
                    .replace(res.getString(R.string.key_url), "")
                    .replace(":", ""));
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

    public String getNick() { return nick; }

    public Bitmap getPic() {
        if (pic != null) {
            return pic.copy(pic.getConfig(), pic.isMutable());
        } else {
            return null;
        }
    }

    public String getId() { return id; }

    // Helping methods
    @Nullable
    private Bitmap getPicByUrl(String url) {
        return null;
    }
}
