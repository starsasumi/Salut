package com.starbugs.salut;

import com.starbugs.salut.util.GeneralTaskListener;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.util.concurrent.Task;

import java.net.URL;

/**
 * Created by starsasumi on 11/05/14.
 */
public class VideoPhoneWindow extends Window implements Bindable {
    public static final String END_CALL_ACTION = "endCallAction";

    @BXML private BoxPane bigVideoPane;
    @BXML private BoxPane smallVideoPane;
    @BXML private PushButton endCallButton;

    private CallActionDelegate callActionDelegate;

    public VideoPhoneWindow() {
        Action.getNamedActions().put(VideoPhoneWindow.END_CALL_ACTION, new Action() {
            @Override
            public void perform(org.apache.pivot.wtk.Component component) {
                Task<String> endCallTask = new Task() {

                    @Override
                    public String execute() throws TaskExecutionException {
                        callActionDelegate.endCall();
                        return "Call Ended";
                    }
                };

                endCallTask.execute(new TaskAdapter<String>(new GeneralTaskListener()));
            }
        });
    }

    @Override
    public void initialize(Map<String, Object> strings, URL url, Resources strings2) {
        this.endCallButton.setAction(VideoPhoneWindow.END_CALL_ACTION);

    }

    public void setBigVideo(java.awt.Component component) {

    }

    public void setSmallVideo(java.awt.Component component) {

    }

    public void setCallActionDelegate(CallActionDelegate callActionDelegate) {
        this.callActionDelegate = callActionDelegate;
    }
}
