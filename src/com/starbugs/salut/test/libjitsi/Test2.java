package com.starbugs.salut.test.libjitsi;

import com.starbugs.salut.*;
import com.starbugs.salut.VideoAudioCall;
import org.jitsi.service.neomedia.MediaType;

import java.net.InetAddress;

/**
 * Created by starsasumi on 08/04/2014.
 */
public class Test2 {
    public static void main(String[] args) {

        try {
            com.starbugs.salut.VideoAudioCall call2 = new com.starbugs.salut.VideoAudioCall(5020, 2000, InetAddress.getByName("223.65.142.30"));
            call2.init();
//            call2.getJingleStream(MediaType.VIDEO).quickShow(call2.getJingleStream(MediaType.AUDIO));
            VideoAudioCall.quickShow(call2, "Call 2");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
