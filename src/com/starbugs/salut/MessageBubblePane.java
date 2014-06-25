package com.starbugs.salut;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.Time;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.media.Image;

import java.net.URL;

/**
 * Created by starsasumi on 07/05/2014.
 */
public class MessageBubblePane extends BoxPane implements Bindable{
    @BXML private TextArea messageTextArea;
    @BXML private Label messageTimeLabel;
    @BXML private ImageView avatar;

    @Override
    public void initialize(Map<String, Object> strings, URL url, Resources strings2) {

    }

    public void setMessage(String message) {
        this.messageTextArea.setText(message);
    }

    public void setTime(Time time) {
        this.messageTimeLabel.setText(time.toString());
    }

    public void setAvatar(Image image) {
        avatar.setImage(image);
    }

}
