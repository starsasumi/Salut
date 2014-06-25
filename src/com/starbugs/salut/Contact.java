package com.starbugs.salut;

import org.apache.pivot.wtk.media.Image;

import java.awt.*;

/**
 * Created by starsasumi on 06/05/2014.
 */
public class Contact {
    private String userName;
    private String extraInfo;
    private Image avatar;
    private String identity;

    public Contact() {
        this(null, null, null, null);
    }

    public Contact(String userName, String extraInfo, Image avatar, String identity) {
        setUserName(userName);
        setExtraInfo(extraInfo);
        setAvatar(avatar);
        setIdentity(identity);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public  String getIdentity() { return this.identity; }

    public void setIdentity(String identity) { this.identity = identity; }
}
