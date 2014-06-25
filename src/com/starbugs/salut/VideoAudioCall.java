package com.starbugs.salut;

import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.*;
import org.jitsi.service.neomedia.device.MediaDevice;
import org.jitsi.service.neomedia.format.MediaFormat;
import org.jitsi.service.neomedia.format.MediaFormatFactory;
import org.jitsi.util.event.VideoEvent;
import org.jitsi.util.event.VideoListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    private MediaStream videoStream, audioStream;
    private boolean isInited = false;
    private MediaService mediaService = null;

    // for controlling
    private CallActionListener callActionListener;

    public VideoAudioCall(int localPortBase, int remotePortBase, InetAddress remoteAddr) {
        this.localPortBase = localPortBase;
        this.remotePortBase = remotePortBase;
        this.remoteAddr = remoteAddr;
    }

    public boolean init() {
        if (this.isInited)
            return true;

        LibJitsi.start();

        this.mediaService = LibJitsi.getMediaService();

        int localPort = this.localPortBase;
        int remotePort = this.remotePortBase;

        this.audioStream = initStream(MediaType.AUDIO, "PCMU", 8000, (byte)-1, this.localPortBase, this.remotePortBase);
        this.videoStream = initStream(MediaType.VIDEO, "H264", MediaFormatFactory.CLOCK_RATE_NOT_SPECIFIED, (byte) 99, this.localPortBase + 2, this.remotePortBase + 2);
        this.videoStream.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.err.println("Property Changed: " + evt.toString());
            }
        });

        if (this.audioStream != null && this.videoStream != null) {
            this.isInited = true;
            return true;
        } else {
            this.isInited = false;
            return false;
        }
    }

    private MediaStream initStream(MediaType type, String encoding, double clockRate, byte dynamicRTPPayloadType, int localPort, int remotePort) {
        MediaDevice mediaDevice = mediaService.getDefaultDevice(type, MediaUseCase.CALL);
        MediaStream mediaStream = mediaService.createMediaStream(mediaDevice);

        mediaStream.setDirection(MediaDirection.SENDRECV);

        MediaFormat format = mediaService.getFormatFactory().createMediaFormat(encoding, clockRate);
        /*
         * The MediaFormat instances which do not have a static RTP
         * payload type number association must be explicitly assigned
         * a dynamic RTP payload type number.
         *
         * And dynamicRTPPayloadType == -1 means static RTP payload.
         */
        if (dynamicRTPPayloadType != -1)
            mediaStream.addDynamicRTPPayloadType(dynamicRTPPayloadType, format);

        mediaStream.setFormat(format);

        // Connector
        StreamConnector connector = null;

        int localRTPPort = localPort++;
        int localRTCPPort = localPort++;
        try {
            connector = new DefaultStreamConnector(new DatagramSocket(localRTPPort), new DatagramSocket(localRTCPPort));
        } catch (SocketException e) {
            e.printStackTrace();
            this.isInited = false;
            return null;
        }
        mediaStream.setConnector(connector);

        // Target
        InetSocketAddress remoteRTP = new InetSocketAddress(this.remoteAddr, remotePort++);
        InetSocketAddress remoteRTCP = new InetSocketAddress(this.remoteAddr, remotePort++);
        mediaStream.setTarget(new MediaStreamTarget(remoteRTP, remoteRTCP));

        // Name
        mediaStream.setName(type.toString());
        return mediaStream;
    }

    private boolean start() {
        if (!this.isInited)
            return false;

        this.audioStream.start();
        this.videoStream.start();
        return true;
    }

    // close all media streams
    public void closeStreams() {
        if (this.audioStream != null)
            System.out.println("Close Audio Stream");
            try {
                this.audioStream.stop();
            }
            finally {
                this.audioStream.close();
                this.audioStream = null;
            }

        if (this.videoStream != null)
            System.out.println("Close Video Stream");
            try {
                this.videoStream.stop();
            }
            finally {
                this.videoStream.close();
                this.videoStream = null;
            }
        LibJitsi.stop();
    }

    public JingleStream getJingleStream(MediaType type) {
        if (isInited) {
            if (type.equals(MediaType.VIDEO)) {
                return new JingleStream("video", this.videoStream);
            } else if (type.equals(MediaType.AUDIO)) {
                return new JingleStream("audio", this.audioStream);
            }
        }

        return null;
    }

    public boolean isConnected() {
        ((VideoMediaStream)this.videoStream).addVideoListener(new VideoListener() {
            @Override
            public void videoAdded(VideoEvent videoEvent) {

            }

            @Override
            public void videoRemoved(VideoEvent videoEvent) {

            }

            @Override
            public void videoUpdate(VideoEvent videoEvent) {

            }
        });
        return false;
    }

    public CallActionListener getCallActionListener() {
        return callActionListener;
    }

    public void setCallActionListener(CallActionListener callActionListener) {
        this.callActionListener = callActionListener;
    }

    public static JFrame quickShow(final VideoAudioCall videoAudioCall, String frameTitle) {
        // TODO: Fix resize to show problem
        JingleStream videoJingleStream = videoAudioCall.getJingleStream(MediaType.VIDEO);
        videoAudioCall.start();

        JPanel p = videoJingleStream.getVisualComponent();
        if( p != null ) {
            final JFrame f = new JFrame(frameTitle);
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
//                    videoAudioCall.closeStreams();
                    videoAudioCall.getCallActionListener().callEnded();
                    super.windowClosing(e);
                }

                @Override
                public void windowOpened(WindowEvent e) {
                    e.getWindow().repaint();
                    e.getWindow().pack();
                    super.windowOpened(e);
                }
            });
            f.add(p, BorderLayout.CENTER);
            f.pack();
            f.setResizable(true);
            f.setVisible(true);
            f.toFront();
            p.addContainerListener( new ContainerListener() {
                @Override
                public void componentAdded(ContainerEvent e) {
                    f.repaint();
                    f.pack();
                }
                @Override
                public void componentRemoved(ContainerEvent e) {
                    f.repaint();
                    f.pack();
                }
            } );
            return f;
        }
        return null;
    }

}
