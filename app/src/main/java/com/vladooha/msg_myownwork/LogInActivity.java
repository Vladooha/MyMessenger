package com.vladooha.msg_myownwork;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogInActivity extends AppCompatActivity {
    // Variables needed to connect with server
    protected static boolean CONNECTED_TO_SERVER = false;
    String serverAnswer = null;

    // Cross-activities channels
    Intent mainMenuIntent;

    // Interaction with 'RegistartionActivity'
    final static int REG_CODE = 222;
    final static String REG_LOGIN = "REG_L";
    final static String REG_PASS = "REG_P";

    // Variables for multi-thread binding (common data for processLoginData() and makeRequest())
    private String strLogin = "";
    private String strPassword = "";
    private String strToken = "";

    private static final String MyLogs = "MyLogs";

    // Screen view-elements
    EditText login;
    EditText password;
    Button logIn;
    TextView registr;
    Toast toast;

    // Resources cahce
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Binding a view elements
        login = (EditText)findViewById(R.id.login);
        password = (EditText)findViewById(R.id.password);
        logIn = (Button)findViewById(R.id.button_log_in);
        registr = (TextView)findViewById(R.id.registration);

        // registr.setMovementMethod(LinkMovementMethod.getInstance());

        // Const manager
        res = getResources();

        // Creating a listener for 'LOG IN' button
        logIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                processLoginData(false);
            }
        });

        // Creating a listener for registration button
        registr.setClickable(true);
        registr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LogInActivity.this, RegistrationActivity.class);
                startActivityForResult(regIntent, REG_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        WebServer.setResources(res, getFilesDir());
        Map<String, String> data = WebServer.readTokenFile();
        if (data != null) {
            try {
                strLogin = data.get(res.getString(R.string.key_email));
                strToken = data.get(res.getString(R.string.key_token));
                serverAnswer = WebServer.makeRequest(res.getString(R.string.cmd_login_by_token) + ":"
                        + res.getString(R.string.key_email) + strLogin + ":"
                        + res.getString(R.string.key_token) + strToken + ":");
            } catch (NotBindedException e) {
                e.printStackTrace();
            }

            if (serverAnswer != null && serverAnswer.startsWith(res.getString(R.string.key_token))) {
                if (!serverAnswer.contains(strToken)) {
                    try {
                        WebServer.makeTokenFile(strLogin, strToken);
                    } catch (NotBindedException e) {
                    }
                }
                startMainMenuActivity();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REG_CODE:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    if (resultCode == RESULT_OK) {
                        strLogin = data.getStringExtra(REG_LOGIN);
                        strPassword = data.getStringExtra(REG_PASS);
                        processLoginData(true);
                    }
                }
                break;
        }
    }

    // Custom helping methods
    private void processLoginData(boolean isFilled) {
        // Reading user's data from screen elements if it's necessary (pressed 'LOG IN' button)
        if (!isFilled) {
            strLogin = login.getText().toString();
            strPassword = password.getText().toString();
        }
        // Checking taken info
        if (strLogin.equals("")) {
            toast = Toast.makeText(LogInActivity.this, R.string.ev_no_login, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if(strPassword.equals("")) {
            toast = Toast.makeText(LogInActivity.this, R.string.ev_no_password, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            // Correct data
        } else {
            // Method-tread that contacts with server and recieve server's answer
            try {
                serverAnswer = WebServer.makeRequest(res.getString(R.string.cmd_login) + ":"
                        + res.getString(R.string.key_email) + strLogin + ":"
                        + res.getString(R.string.key_pass) + strPassword + ":");
            } catch (NotBindedException e) {
                e.printStackTrace();
            }

            // Checking server's answer
            CONNECTED_TO_SERVER = WebServer.getServerStatus();
            if (serverAnswer == null || !CONNECTED_TO_SERVER) {
                Log.d(MyLogs, "Server status: " + String.valueOf(CONNECTED_TO_SERVER));
                toast = Toast.makeText(LogInActivity.this, R.string.ev_no_connection,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (serverAnswer.startsWith(res.getString(R.string.key_email) + strLogin + ":"
                    + res.getString(R.string.key_pass) + strPassword + ":")) {
                startMainMenuActivity();
            } else {
                toast = Toast.makeText(LogInActivity.this, R.string.ev_bad_login, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    private void startMainMenuActivity() {
//        Pattern pattToken = Pattern.compile(res.getString(R.string.key_token) + res.getString(R.string.patt_token) + ":");
//        Matcher matchBuff = pattToken.matcher(serverAnswer);
//        if (matchBuff.find()) {
//            strToken = matchBuff.group(0).substring(res.getString(R.string.key_token).length(),
//                    matchBuff.group(0).length() - 1);
//            serverAnswer = null;
//            if (WebServer.makeTokenFile(strLogin, strToken)) {
//                mainMenuIntent = new Intent(this, MainMenuActivity.class);
//                startActivity(mainMenuIntent);
//                finish();
//            }
//        } else {
//            serverAnswer = null;
//            toast = Toast.makeText(LogInActivity.this, R.string.ev_unknown_err, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//        }

        mainMenuIntent = new Intent(this, MainMenuActivity.class);
        startActivity(mainMenuIntent);
        finish();
    }


}
