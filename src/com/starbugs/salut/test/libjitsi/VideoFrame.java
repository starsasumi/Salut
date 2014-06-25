package com.starbugs.salut.test.libjitsi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

/**
 * Created by starsasumi on 08/04/2014.
 */
class VideoFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    private JPanel vc;

    VideoFrame(String title, final Component video)
    {
        super(title);
        this.setLayout(new BorderLayout());
        this.setSize(640, 480);

        vc = new JPanel();
        setVideo(video);
        this.add(vc, BorderLayout.CENTER);

        this.addContainerListener( new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                pack();
            }
            @Override
            public void componentRemoved(ContainerEvent e) {
                pack();
            }
        } );
    }

    public void setVideo(final Component video) {
        video.setSize(640,480);

        vc.removeAll();
        vc.add(video);
        vc.setPreferredSize(new Dimension(640, 480));
        vc.revalidate();
    }
}
