package com.starbugs.salut;

import java.net.InetAddress;

/**
 * Created by starsasumi on 11/05/14.
 */
public interface CallActionListener {
    public void callEnded();
    public void videoCallMade();
    public void videoCallAnswered();
    public void videoCallRejected();

    public enum CallAction {
        MAKE_CALL("MAKE_CALL_ACTION"), ANSWER_OK("ANSWER_OK_ACTION"), ANSWER_REJECT("ANSWER_REJECT_ACTION"), END_CALL("END_CALL_ACTION");

        private String action;

        CallAction(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return this.action;
        }
    }
}
