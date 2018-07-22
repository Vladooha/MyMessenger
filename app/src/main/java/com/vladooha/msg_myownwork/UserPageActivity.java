package com.vladooha.msg_myownwork;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

import static com.vladooha.msg_myownwork.UserSearchFragment.CONTAINER_UID;
import static com.vladooha.msg_myownwork.WebServer.MyLogs;

/**
 * Created by Vladooha on 22.07.2018.
 */

public class UserPageActivity extends AppCompatActivity {
    Resources res;

    TextView nicknameText;
    TextView birthText;
    ImageView avatarPic;

    static private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        res = getResources();

        id = getIntent().getStringExtra(CONTAINER_UID);

        UserFullInfoObj userInfo = fillUserInfo(id);

        nicknameText = findViewById(R.id.user_page_nick);
        nicknameText.setText(userInfo.getNick());

        birthText = findViewById(R.id.user_page_birth);
        birthText.setText(userInfo.getBirth().getString());nicknameText = findViewById(R.id.user_page_nick);
        nicknameText.setText(userInfo.getNick());
    }

    private UserFullInfoObj fillUserInfo(String id) {
        UserFullInfoObj result = new UserFullInfoObj(res);
        result.setAsUnknownUser();

        if (id != null && res != null) {
            Log.d(MyLogs, "User page got a request");

            WebServer webserver = new WebServer();
            webserver.setResources(res, getFilesDir());
            String serverAnswer = null;
            try {
                serverAnswer = webserver.makeAuthRequest(res.getString(R.string.cmd_full_info),
                        res.getString(R.string.key_uid) + id);
            } catch (NotBindedException e) {
                e.printStackTrace();
                return result;
            }

            if (serverAnswer != null && serverAnswer.startsWith(res.getString(R.string.key_nick)))
            {
                Log.d(MyLogs, "We got users info");
                result.setUserInfo(serverAnswer.split(":")[0].replace("|", ":") + ":");
                return result;
            } else {
                Log.d(MyLogs, "Bad server's answer");
                return result;
            }

        } else {
            Log.d(MyLogs, "User searcher got bad data");
            return result;
        }
    }
}
