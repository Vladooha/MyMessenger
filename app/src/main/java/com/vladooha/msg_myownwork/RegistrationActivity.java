package com.vladooha.msg_myownwork;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    // Variables needed to connect with server
    protected static boolean CONNECTED_TO_SERVER = false;
    String serverAnswer = null;
    protected static Socket SOCKET;

    // Back-interaction with parent 'LoginActivity'
    final static String REG_LOGIN = "REG_L";
    final static String REG_PASS = "REG_P";

    // Variables for multi-thread binding
    Thread sender;
    String strNickname;
    String strLogin;
    String strPassword;
    String strBday;
    String strBmonth;
    String strByear;

    // Screen view-elements
    EditText nickname;
    EditText login;
    EditText password;
    Spinner bday;
    Spinner bmonth;
    Spinner byear;
    Button signUp;
    Toast toast;
    Resources res;

    private static final String MyLogs = "MyLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Binding a view elements
        nickname = findViewById(R.id.nickname_reg);
        login = findViewById(R.id.login_reg);
        password = findViewById(R.id.password_reg);
        bday = findViewById(R.id.bday_reg);
        bmonth = findViewById(R.id.bmonth_reg);
        byear = findViewById(R.id.byear_reg);
        signUp = findViewById(R.id.sign_up_reg);

        // Const manager
        res = getResources();

        // Init birthday selection
        String[] years = new String[101];
        int nextYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
        years[0] = res.getString(R.string.not_filled);
        for (int i = 1; i < 101; ++i) {
            years[i] = String.valueOf(nextYear - i);
        }
        ArrayAdapter yearsAdapt = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years);
        byear.setAdapter(yearsAdapt);
        byear.setPromptId(R.string.year);

        ArrayAdapter monthsAdapt = ArrayAdapter.createFromResource(this, R.array.monthlist, android.R.layout.simple_spinner_dropdown_item);
        bmonth.setAdapter(monthsAdapt);
        bmonth.setPromptId(R.string.month);

        String[] days = new String[32];
        days[0] = res.getString(R.string.not_filled);
        for (int i = 1; i < 32; ++i) {
            days[i] = String.valueOf(i);
        }
        ArrayAdapter daysAdapt = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days);
        bday.setAdapter(daysAdapt);
        bday.setPromptId(R.string.day);

        // Creating a listener for 'SIGN UP' button
        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Pattern pattLogin = Pattern.compile("^" + res.getString(R.string.patt_email) + "$");
                Pattern pattPassword = Pattern.compile("^" + res.getString(R.string.patt_pass) + "$");
                Pattern pattNickname = Pattern.compile("^" + res.getString(R.string.patt_nick) + "$");
                strNickname = nickname.getText().toString();
                strLogin = login.getText().toString();
                strPassword = password.getText().toString();
                strBday = (String) bday.getSelectedItem();
                strBmonth = (String) bmonth.getSelectedItem();
                strByear = (String) byear.getSelectedItem();
                Matcher matchLogin = pattLogin.matcher(strLogin);
                Matcher matchPassword = pattPassword.matcher(strPassword);
                Matcher matchNickname = pattNickname.matcher(strNickname);
                // Checking taken info
                if (!matchLogin.find()) {
                    Log.d(MyLogs, strLogin + " - " + String.valueOf(!matchLogin.find()));
                    toast = Toast.makeText(RegistrationActivity.this, R.string.ev_no_login, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (!matchPassword.find()) {
                    Log.d(MyLogs, String.valueOf(!matchPassword.find()));
                    toast = Toast.makeText(RegistrationActivity.this, R.string.ev_no_password, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (!matchNickname.find()) {
                    Log.d(MyLogs, String.valueOf(!matchNickname.find()));
                    toast = Toast.makeText(RegistrationActivity.this, R.string.ev_no_nickname, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    // Correct data
                } else {
                    sendRegData();

                    // This cycle with try-catch block makes latency that gives a time for
                    // connection-treads to make their jobs and helps sync data
                    // sender - sendRegData() thread
                    int attempts = getResources().getInteger(R.integer.attempts);
                    while (sender.isAlive() && attempts > 0) {
                        try {
                            Thread.sleep(getResources().getInteger(R.integer.delay));
                        } catch (InterruptedException e) {
                            Log.d(MyLogs, e.getMessage());
                        }
                        --attempts;
                    }

                    if (CONNECTED_TO_SERVER && serverAnswer.equals(res.getString(R.string.cmd_reg))) {
                        Intent ansIntent = new Intent();
                        ansIntent.putExtra(REG_LOGIN, strLogin);
                        ansIntent.putExtra(REG_PASS, strPassword);
                        setResult(RESULT_OK, ansIntent);
                        finish();
                    } else {
                        if (CONNECTED_TO_SERVER && serverAnswer.equals(res.getString(R.string.ans_login_alrd_exst))) {
                            toast = Toast.makeText(RegistrationActivity.this, R.string.ev_log_ex, Toast.LENGTH_SHORT);
                        } else if (!CONNECTED_TO_SERVER) {
                            toast = Toast.makeText(RegistrationActivity.this, R.string.ev_no_connection, Toast.LENGTH_SHORT);
                        } else {
                            toast = Toast.makeText(RegistrationActivity.this, R.string.ev_unknown_err, Toast.LENGTH_SHORT);
                        }
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        setResult(RESULT_CANCELED);
                    }
                }
            }
        });
    }

    private void sendRegData() {
        Log.d(MyLogs, "sendRegData()");
        sender = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // Creatig a connection
                    SOCKET = new Socket(res.getString(R.string.ip), res.getInteger(R.integer.port));

                    OutputStream output = SOCKET.getOutputStream();
                    DataOutputStream streamWriter = new DataOutputStream(output);

                    // Data sending to server
                    boolean isYearWritten = byear.equals(res.getString(R.string.not_filled));
                    boolean isMonthWritten = bmonth.equals(res.getString(R.string.not_filled));
                    boolean isDayWritten = bday.equals(res.getString(R.string.not_filled));
                    streamWriter.writeUTF(res.getString(R.string.cmd_reg) + ":"
                            + res.getString(R.string.key_email) + strLogin + ":"
                            + res.getString(R.string.key_pass) + strPassword + ":"
                            + res.getString(R.string.key_nick) + strNickname + ":"
                            + (isYearWritten ? "" : res.getString(R.string.key_byear) + strByear + ":"
                            + (isMonthWritten && isDayWritten ? "" : res.getString(R.string.key_bmonth) + strBmonth + ":"
                            + (isDayWritten ? "" : res.getString(R.string.key_bday) + strBday + ":"))));

                    // Server's answer processing
                    InputStream input = SOCKET.getInputStream();
                    DataInputStream streamReader = new DataInputStream(input);
                    // Throws an exception if app can't connect to server
                    while (streamReader.available() == 0) { }
                    serverAnswer = streamReader.readUTF();
                    // If last attempt was bad
                    CONNECTED_TO_SERVER = true;

                    streamReader.close();
                    streamWriter.close();
                } catch (IOException e) {
                    Log.d(MyLogs, "Can't connect to server! (server's SOCKET didn't answer)");
                    Log.d(MyLogs, e.getMessage());
                    CONNECTED_TO_SERVER = false;
                }
            }
        });
        sender.start();
    }
}
