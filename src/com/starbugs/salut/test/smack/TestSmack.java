package com.starbugs.salut.test.smack;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by starsasumi on 19/04/2014.
 */
public class TestSmack {
    public static void main(String[] args) {

        Connection.DEBUG_ENABLED = true;

        ConnectionConfiguration configuration = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
        configuration.setSASLAuthenticationEnabled(false);
        XMPPConnection connection = new XMPPConnection(configuration);
        try {
            connection.connect();
            connection.login("", "", "SmackOnMac");
            Chat chat = connection.getChatManager().createChat("starsasumi@gmail.com", new MessageListener() {

                public void processMessage(Chat chat, Message message) {
                    System.out.println("Received message: " + message);
                }
            });
            chat.sendMessage("What's up?");

        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
