package com.starbugs.salut.test.libjitsi;

import com.starbugs.salut.*;
import com.starbugs.salut.VideoAudioCall;
import org.jitsi.service.neomedia.MediaType;

import java.net.InetAddress;

/**
 * Created by starsasumi on 08/04/2014.
 */
public class Test1 {
    public static void main(String[] args) {

        try {
            com.starbugs.salut.VideoAudioCall call1 = new com.starbugs.salut.VideoAudioCall(2000, 5020, InetAddress.getByName("223.65.142.30"));
            call1.init();

//            call1.getJingleStream(MediaType.VIDEO).quickShow(call1.getJingleStream(MediaType.AUDIO));
            VideoAudioCall.quickShow(call1, "Call 1");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
