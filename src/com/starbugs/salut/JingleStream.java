package com.starbugs.salut;

import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jitsi.service.neomedia.AudioMediaStream;
import org.jitsi.service.neomedia.MediaStream;
import org.jitsi.service.neomedia.VideoMediaStream;
import org.jitsi.util.event.VideoEvent;
import org.jitsi.util.event.VideoListener;

public class JingleStream {
    private final String name;
    private final MediaStream mediaStream;
    private JPanel visualComponent;

    public JingleStream(String name, MediaStream mediaStream) {
        this.name = name;
        this.mediaStream = mediaStream;
    }

    public String getName() {
        return name;
    }

    /** starts the audio if this is an audio stream. */
    public void startAudio() {
        if( mediaStream instanceof AudioMediaStream ) {
            AudioMediaStream ams = ((AudioMediaStream) mediaStream);
//            ams.setDevice(mediaDevice);
            ams.start();
        }
    }

    /** returns a visual component for this stream or null if this is not a video stream. */
    public JPanel getVisualComponent() {
        if( visualComponent != null )
            return visualComponent;
        if( mediaStream instanceof VideoMediaStream ) {
            visualComponent = new JPanel( new BorderLayout() );
            VideoMediaStream vms = ((VideoMediaStream) mediaStream);
            vms.addVideoListener( new VideoListener() {
                @Override
                public void videoAdded(VideoEvent event) {
                    videoUpdate(event);
                }
                @Override
                public void videoRemoved(VideoEvent event) {
                    videoUpdate(event);
                }
                @Override
                public void videoUpdate(VideoEvent event) {
                    updateVisualComponent();
                }
            } );
            updateVisualComponent();
            return visualComponent;
        } else {
            return null;
        }
    }

    private void updateVisualComponent() {
        visualComponent.removeAll();
        VideoMediaStream vms = ((VideoMediaStream) mediaStream);
        for( Component c : vms.getVisualComponents() ) {
            c.setSize(640, 480);
            visualComponent.add(c); //only the first one
            visualComponent.setPreferredSize(new Dimension(640, 480));
            break;
        }
        visualComponent.revalidate();
        visualComponent.repaint();
    }

    public void shutdown() {
        mediaStream.close();
    }

    public void start() {
        mediaStream.start();
    }
}