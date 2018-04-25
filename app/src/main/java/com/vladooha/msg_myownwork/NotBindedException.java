package com.vladooha.msg_myownwork;

/**
 * Created by Vladooha on 10.04.2018.
 */

public class NotBindedException extends Exception {
    @Override
    public String getMessage() {
        return "Server adress isn't setteded! (Use 'setServerAdress(String ip, int port)' method to fix it!)";
    }
}
