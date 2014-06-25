package com.starbugs.salut;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.media.Image;

import java.net.URL;

/**
 * Created by starsasumi on 05/05/2014.
 */
public class ContactPane extends BoxPane implements Bindable {
    @BXML private ImageView avatarImage;
    @BXML private Label userNameLabel;
    @BXML private Label extraInfoLabel;

    private String identity;

    @Override
    public void initialize(Map<String, Object> strings, URL url, Resources strings2) {

        this.setCursor(Cursor.HAND);
        this.getComponentMouseListeners().add(new ComponentMouseListener.Adapter() {
            @Override
            public void mouseOver(Component component) {
                component.getStyles().put("backgroundColor", "#F5F5F5");
                super.mouseOver(component);
            }

            @Override
            public void mouseOut(Component component) {
                component.getStyles().put("backgroundColor", "#FFFFFF");
                super.mouseOut(component);
            }
        });
    }

    public String getUserName() {
        return userNameLabel.getText();
    }

    public void setUserName(String userName) {
        this.userNameLabel.setText(userName);
    }

    public String getExtraInfo() {
        return this.extraInfoLabel.getText();
    }

    public void setExtraInfo(String extraInfo) {
        if (extraInfo != null && !extraInfo.isEmpty())
            this.extraInfoLabel.setText(extraInfo);
    }

    public Image getAvatar() {
        return this.avatarImage.getImage();
    }

    public void setAvatar(Image avatar) {
        if (avatar != null)
            this.avatarImage.setImage(avatar);
    }

    public void setIdentity(String identity) {
        this.identity =identity;
    }

    public String getIdentity(){
        return this.identity;
    }

    public void setExtraInfoColor(String color) {
        this.extraInfoLabel.getStyles().put("color", color);
    }
}
