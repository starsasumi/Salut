package com.starbugs.salut.test.libjitsi;

import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.*;
import org.jitsi.service.neomedia.device.MediaDevice;
import org.jitsi.service.neomedia.format.MediaFormat;
import org.jitsi.service.neomedia.format.MediaFormatFactory;
import org.jitsi.util.event.VideoEvent;
import org.jitsi.util.event.VideoListener;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by starsasumi on 08/04/2014.
 */
public class VideoAudioCall {

    // for connection
    private int localPortBase;
    private int remotePortBase;
    private InetAddress remoteAddr;

    // for streaming
    private MediaStream[] mediaStreams;
    private boolean isInit = false;

    // for UI
    private boolean foundVideo = false;
    private VideoMediaStream videoMediaStream;
    private String videoFrameTitle = "Video";
    private  VideoFrame videoFrame;

    public VideoAudioCall(int localPortBase, int remotePortBase, InetAddress remoteAddr) {
        this.localPortBase = localPortBase;
        this.remotePortBase = remotePortBase;
        this.remoteAddr = remoteAddr;
    }

    public boolean init() {
        if (this.isInit)
            return true;

        LibJitsi.start();

        MediaService mediaService = LibJitsi.getMediaService();

        MediaType[] mediaTypes = MediaType.values();
        this.mediaStreams = new MediaStream[mediaTypes.length];

        int localPort = this.localPortBase;
        int remotePort = this.remotePortBase;

        for (MediaType mediaType : mediaTypes) {
            MediaDevice mediaDevice = mediaService.getDefaultDevice(mediaType, MediaUseCase.CALL);
            MediaStream mediaStream = mediaService.createMediaStream(mediaDevice);

            mediaStream.setDirection(MediaDirection.SENDRECV);

            // format
            String encoding;
            double clockRate;
            byte dynamicRTPPayloadType;

            switch (mediaType) {
                case AUDIO:
                    encoding = "PCMU";
                    clockRate = 8000;
                    /*
                     * -1 means PCMU format does not need a dynamicRTPPayloadType value,
                     * it has a static clockRate value instead.
                     */
                    dynamicRTPPayloadType = -1;
                    break;
                case VIDEO:
                    encoding = "H264";
                    clockRate = MediaFormatFactory.CLOCK_RATE_NOT_SPECIFIED;
                    dynamicRTPPayloadType = 99;
                    break;
                default:
                    encoding = null;
                    clockRate = MediaFormatFactory.CLOCK_RATE_NOT_SPECIFIED;
                    dynamicRTPPayloadType = -1;
            }

            if (encoding != null) {
                MediaFormat format = mediaService.getFormatFactory().createMediaFormat(encoding, clockRate);

                /*
                 * The MediaFormat instances which do not have a static RTP
                 * payload type number association must be explicitly assigned
                 * a dynamic RTP payload type number.
                 *
                 * And dynamicRTPPayloadType == -1 means static RTP payload.
                 */
                if (dynamicRTPPayloadType != -1) {
                    mediaStream.addDynamicRTPPayloadType(dynamicRTPPayloadType, format);
                }

                mediaStream.setFormat(format);
            }

            // Add listeners for UI
            if (mediaStream instanceof VideoMediaStream) {
                this.videoMediaStream = (VideoMediaStream) mediaStream;
                this.foundVideo = false;

                this.videoMediaStream.addVideoListener(new VideoListener() {

                    @Override
                    public void videoAdded(VideoEvent event) {
                        checkForVideo();
                    }

                    @Override
                    public void videoRemoved(VideoEvent event) {
                        checkForVideo();
                    }

                    @Override
                    public void videoUpdate(VideoEvent event) {
                        checkForVideo();
                    }
                });
            }

            // Connector
            StreamConnector connector = null;



            int localRTPPort = localPort++;
            int localRTCPPort = localPort++;
            try {
                connector = new DefaultStreamConnector(new DatagramSocket(localRTPPort), new DatagramSocket(localRTCPPort));
            } catch (SocketException e) {
                e.printStackTrace();
                System.err.println(this.getVideoFrameTitle());

                this.isInit = false;
                return false;
            }
            mediaStream.setConnector(connector);

            // Target

            InetSocketAddress remoteRTP = new InetSocketAddress(this.remoteAddr, remotePort++);
            InetSocketAddress remoteRTCP = new InetSocketAddress(this.remoteAddr, remotePort++);
            mediaStream.setTarget(new MediaStreamTarget(remoteRTP, remoteRTCP));

            // Name
            mediaStream.setName(mediaType.toString());

            // Finish init and add to this.mediaStreams
            this.mediaStreams[mediaType.ordinal()] = mediaStream;
        }

        this.isInit = true;
        return true;
    }

    public boolean start() {
        if (!this.isInit)
            return false;

        for (MediaStream mediaStream : this.mediaStreams) {
            mediaStream.start();
        }

        return true;
    }

    /**
     * Close the <tt>MediaStream</tt>s.
     */
    public void closeStreams() {
        if (this.mediaStreams != null) {
            for (int i = 0; i < this.mediaStreams.length; i++) {
                MediaStream mediaStream = this.mediaStreams[i];

                if (mediaStream != null) {
                    try {
                        mediaStream.stop();
                    }
                    finally {
                        mediaStream.close();
                        this.mediaStreams[i] = null;
                    }
                }
            }

            this.mediaStreams = null;
            LibJitsi.stop();
        }
    }

    private void checkForVideo() {
        if (true)
        {
            System.out.println("Still finding video");

            java.util.List<Component> videos = this.videoMediaStream.getVisualComponents();
            if ((videos != null) && (!videos.isEmpty()))
            {
                System.out.println("Found Video!");
                foundVideo = true;

                final Component video = this.videoMediaStream.getVisualComponents().get(0);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                       if(videoFrame == null) {
                           videoFrame = new VideoFrame(getVideoFrameTitle(), video);
                           videoFrame.setResizable(true);
                           videoFrame.setVisible(true);
                           videoFrame.toFront();
                       } else {
                           videoFrame.setVideo(video);
                       }
                        videoFrame.pack();

                        System.out.println(getVideoFrameTitle() + " Show Video");
                    }
                });
            }
        }
    }

    public void setVideoFrameTitle(String title) {
        this.videoFrameTitle = title;
    }

    public String getVideoFrameTitle() {
        return this.videoFrameTitle;
    }

}
