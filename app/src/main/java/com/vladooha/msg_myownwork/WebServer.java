package com.vladooha.msg_myownwork;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vladooha on 10.04.2018.
 */

public class WebServer {
    final static String MyLogs = "myLogs";

    static Resources res = null;
    static File filesDir = null;
    static String serverAnswer = null;
    private static String login = null;

    public static void setResources(Resources res, File filesDir) {
        WebServer.res = res;
        WebServer.filesDir = filesDir;
    }

    // Core method for server-client communication
    public static String makeRequest(final String request) throws NotBindedException{
        if (res == null || filesDir == null) {
            throw new NotBindedException();
        }
        Log.d(MyLogs, "makeRequest()");
        Thread sender = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // Creatig a connection
                    Socket SOCKET = new Socket(res.getString(R.string.ip), res.getInteger(R.integer.port));

                    OutputStream output = SOCKET.getOutputStream();
                    DataOutputStream streamWriter = new DataOutputStream(output);

                    // Data sending to server
                    streamWriter.writeUTF(request);

                    // Server's answer processing
                    InputStream input = SOCKET.getInputStream();
                    DataInputStream streamReader = new DataInputStream(input);
                    // Throws an exception if app can't connect to server
                    while (streamReader.available() == 0) { }
                    serverAnswer = streamReader.readUTF();

                    streamReader.close();
                    streamWriter.close();
                } catch (IOException e) {
                    Log.d(MyLogs, "Can't connect to server! (server's SOCKET didn't answer)");
                    Log.d(MyLogs, e.getMessage());
                }
            }
        });
        sender.start();

        // This cycle with try-catch block makes latency that
        // gives a time for connection-tread to make its work
        int attempts = res.getInteger(R.integer.attempts);
        while (sender.isAlive() && attempts > 0) {
            try {
                Thread.sleep(res.getInteger(R.integer.delay));
            } catch (InterruptedException e) {
                Log.d(MyLogs, e.getMessage());
            }
            --attempts;
        }

        // If we didn't get (full) answer for latency time
        if (attempts == 0) {
            serverAnswer = null;
        }

        // Stoppin attempts to get answer
        if (sender.isAlive()) {
            sender.interrupt();
        }

        newTokenChecker();

        return serverAnswer;
    }

    public static boolean getServerStatus() {
        String answer = null;
        try {
            answer = makeRequest(res.getString(R.string.cmd_check) + ":" + "42" + ":");
        } catch (NotBindedException e) {
            e.printStackTrace();
        }
        return (answer != null);
    }

    public static boolean makeTokenFile(String login, String token) throws NotBindedException {
        if (res == null || filesDir == null) {
            throw new NotBindedException();
        }
        File tokenFile = new File(filesDir, res.getString(R.string.ext_token_file));
        try {
            if (tokenFile.exists()) {
                tokenFile.delete();
                tokenFile = new File(filesDir, res.getString(R.string.ext_token_file));
            }
            tokenFile.createNewFile();
            BufferedWriter tokenWriter = new BufferedWriter(new FileWriter(tokenFile));
            // ! This consequence uses in token-file reader !
            tokenWriter.write(login + "\r\n");
            tokenWriter.write(token + "\r\n");
            tokenWriter.flush();
            tokenWriter.close();
            return true;
        } catch (IOException e) {
            if (tokenFile.exists()) {
                tokenFile.delete();
            }
            return false;
        }
    }

    @Nullable
    public static Map<String, String> readTokenFile() {
        File tokenFile = new File(filesDir, res.getString(R.string.ext_token_file));
        if (tokenFile.exists()) {
            try {
                Map<String, String> result = new TreeMap<>();
                Scanner tokenReader = new Scanner(tokenFile);
                // ! This consequence was made in token-file writer !
                result.put(res.getString(R.string.key_email), tokenReader.nextLine());
                result.put(res.getString(R.string.key_token), tokenReader.nextLine());
                return result;
            } catch (FileNotFoundException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private static boolean newTokenChecker() {
        if (serverAnswer != null) {
            Pattern tokenPatt = Pattern.compile(res.getString(R.string.key_token)
                    + res.getString(R.string.patt_token));
            Matcher tokenMatcher = tokenPatt.matcher(serverAnswer);
            if (tokenMatcher.find()) {
                String tokenStr = tokenMatcher.group(0)
                        .substring(res.getString(R.string.key_token).length(),
                        tokenMatcher.group(0).length() - 1);
                Map<String, String> data = readTokenFile();
                if (data != null) {
                    String loginStr = data.get(res.getString(R.string.key_email));
                    try {
                        return makeTokenFile(loginStr, tokenStr);
                    } catch (NotBindedException e) {
                        e.printStackTrace();
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
