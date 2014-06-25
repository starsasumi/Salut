package com.starbugs.salut;

import org.apache.pivot.wtk.media.Image;

/**
 * Created by starsasumi on 13/05/14.
 */
public interface ChatRoomDelegate {
    public String getUser();
    public Image getAvatar(String User);
}
