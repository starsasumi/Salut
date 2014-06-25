package com.starbugs.salut;

import java.net.InetAddress;

/**
 * Created by starsasumi on 11/05/14.
 */
public interface CallActionDelegate {
    public void endCall();
    public void makeVideoCall(InetAddress ip);
    public void answerVideoCall(InetAddress ip);
}
