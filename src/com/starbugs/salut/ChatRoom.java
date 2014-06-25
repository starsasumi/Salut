package com.starbugs.salut;


import com.starbugs.salut.util.AvailablePortFinder;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.Time;
import org.apache.pivot.wtk.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by starsasumi on 07/05/2014.
 */
// TODO: Find a better way to handle multi chat problem
public class ChatRoom implements ChatActionDelegate, CallActionListener, CallActionDelegate {
    private final Chat chat;
    private ChatRoomWindow chatRoomWindow;
    private int localPort = -1;
    private int remotePort = -1;
    private InetAddress remoteAddr = null;
    private VideoAudioCall videoAudioCall;
    private JFrame videoFrame;

    private ChatRoomDelegate chatRoomDelegate;
    private String participantName;

    public ChatRoom(Chat chat, ChatRoomDelegate chatRoomDelegate, String participantName) {
        this.chat = chat;
        setChatRoomDelegate(chatRoomDelegate);
        setParticipantName(participantName);
        findLocalPort();
        // init ChatRoomWindow
        BXMLSerializer bxmlSerializer = new BXMLSerializer();
        try {
            Resources langResources = Salut.getLangResources();
            this.chatRoomWindow = (ChatRoomWindow) bxmlSerializer.readObject(getClass().getResource("resources/bxml/ChatRoomWindow.bxml"), langResources);
            this.chatRoomWindow.setChatActionDelegate(this);
            this.chatRoomWindow.setCallActionDelegate(this);

            String titleBase = (String)langResources.get(ChatRoomWindow.TITLE_BASE);

            this.chatRoomWindow.setTitle(titleBase + this.participantName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Set avatars for this room
        if (this.chatRoomDelegate != null) {
            String user = this.chatRoomDelegate.getUser();
            this.chatRoomWindow.setSelfAvatar(this.chatRoomDelegate.getAvatar(user));
            this.chatRoomWindow.setOtherAvatar(this.chatRoomDelegate.getAvatar(this.chat.getParticipant()));
        }
    }

    public void openChatRoomWindow() {
        if (!this.chatRoomWindow.isOpening()) {
            Display display;
            if (this.chatRoomWindow.getDisplay() == null) {
                display  = DesktopApplicationContext.createDisplay(0, 0, 0, 0, false, true, false, null, new DesktopApplicationContext.DisplayListener() {
                    @Override
                    public void hostWindowOpened(Display components) {

                    }

                    @Override
                    public void hostWindowClosed(Display components) {
                        chatRoomWindow.close();
                    }
                });
            } else {
                display = this.chatRoomWindow.getDisplay();
            }

            this.chatRoomWindow.open(display);
            DesktopApplicationContext.sizeHostToFit(this.chatRoomWindow);
            this.sendMessage("");
        }
    }

    @Override
    public void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (message != null && !message.isEmpty())
                        chatRoomWindow.addMessageToRoom(message, new Time(), true);

                }
            });
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callEnded() {
        if (this.videoFrame != null) {
            this.videoFrame.dispose();
            this.videoFrame.setVisible(false);
            this.videoFrame = null;
        }
        // (this.videoAudioCall == null) means there was no video call
        if (this.videoAudioCall != null) {
            this.videoAudioCall.closeStreams();
            this.videoAudioCall = null;
            Message endCall = new Message(null);
            endCall.setProperty("SalutVideoCall", CallAction.END_CALL);
            try {
                this.chat.sendMessage(endCall);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void videoCallMade() {
        // No usage
    }

    @Override
    public void videoCallAnswered() {
        // No usage
    }

    @Override
    public void videoCallRejected() {
        // No usage
    }

    @Override
    public void endCall() {
        // No usage
    }

    @Override
    public void makeVideoCall(InetAddress localIp) {
        Message videoCall = new Message(null);
        videoCall.setProperty("SalutVideoCall", CallAction.MAKE_CALL);
        videoCall.setProperty("ip", localIp);
        videoCall.setProperty("port", this.localPort);
        try {
            this.chat.sendMessage(videoCall);
        } catch (XMPPException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void answerVideoCall(InetAddress localIp) {
        if (localIp != null) {
            Message videoCall = new Message(null);
            videoCall.setProperty("SalutVideoCall", CallAction.ANSWER_OK);
            videoCall.setProperty("ip", localIp);
            videoCall.setProperty("port", this.localPort);
            try {
                this.chat.sendMessage(videoCall);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
            startVideoCall();
        } else {
            Message videoCall = new Message(null);
            videoCall.setProperty("SalutVideoCall", CallAction.ANSWER_REJECT);
            try {
                this.chat.sendMessage(videoCall);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    private void startVideoCall() {
        try {
            this.videoAudioCall = new VideoAudioCall(this.localPort, this.remotePort, this.remoteAddr);
            this.videoAudioCall.init();
            this.videoAudioCall.setCallActionListener(this);
            this.videoFrame = VideoAudioCall.quickShow(this.videoAudioCall, this.chat.getParticipant());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addMessage(final Message message) {
        if (this.chatRoomWindow != null) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (message.getBody() != null && !message.getBody().isEmpty())
                        chatRoomWindow.addMessageToRoom(message.getBody(), new Time(), false);
                }
            });
        }
    }

    public String getChatThread() {
        return this.chat.getThreadID();
    }

    private void findLocalPort() {
        boolean foundPort = false;
        int port = AvailablePortFinder.getNextAvailable();
        while (!foundPort) {
            foundPort = true;
            for (int i = 1; i < 4; ++i) {
                if (!AvailablePortFinder.available(port + i)) {
                    foundPort = false;
                    port = AvailablePortFinder.getNextAvailable(port + 4);
                    break;
                }
            }
        }
        this.localPort = port;
        System.out.println("Found Local Port: " + this.localPort);
    }

    public void receivedVideoCallAction(Message message) {
        CallAction callAction = (CallAction) message.getProperty("SalutVideoCall");

        System.out.println("Received A video call Action: " + callAction);
        switch (callAction) {
            case MAKE_CALL:
                receivedMackCall(message);
                break;
            case ANSWER_OK:
                receivedAnswerOk(message);
                break;
            case ANSWER_REJECT:
                receivedAnswerReject(message);
                break;
            case END_CALL:
                receivedEndCall(message);
                break;
            default:
                System.err.println("Received An Unknown Call Action: " + callAction);
        }
    }

    private void receivedMackCall(Message message) {
        this.remoteAddr = (InetAddress) message.getProperty("ip");
        this.remotePort = (Integer) message.getProperty("port");
        this.chatRoomWindow.receivedVideoCall();
    }

    private void receivedAnswerOk(Message message) {
        this.remoteAddr = (InetAddress) message.getProperty("ip");
        this.remotePort = (Integer) message.getProperty("port");
        startVideoCall();
    }

    private void receivedAnswerReject(Message message) {
        // TODO: Tell user that his video call request was rejected
        this.remotePort = -1;
        this.remoteAddr = null;
    }

    private void receivedEndCall(Message message) {
        System.out.println("Received End Call");
        this.remotePort = -1;
        this.remoteAddr = null;
        this.callEnded();
    }

    public void setChatRoomDelegate(ChatRoomDelegate chatRoomDelegate) {
        this.chatRoomDelegate = chatRoomDelegate;
    }

    public void setParticipantName(String name) {
        if (name != null && !name.isEmpty())
            this.participantName = name;
        else
            this.participantName = this.chat.getParticipant();
    }

    public String getParticipantName() {
        return this.participantName;
    }
}
