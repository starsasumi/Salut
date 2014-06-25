package com.starbugs.salut.test.pivot;

/**
 * Created by starsasumi on 23/04/2014.
 */
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Locale;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;

import static org.apache.pivot.wtk.DesktopApplicationContext.*;

public class MainTest implements Application {
    private Window contactListWindow = null;
    private Window chatRoomWindow = null;
    private Window videoPhone = null;
    public static final String LANGUAGE_KEY = "language";

    @Override
    public void startup(Display display, Map<String, String> properties)
            throws Exception {
        String language = properties.get(LANGUAGE_KEY);
        Locale locale = (language == null) ? Locale.getDefault() : new Locale(language);
        Resources resources = new Resources("com/starbugs/salut/test/pivot/resources/text/Localization", locale);

        Theme theme = Theme.getTheme();
        Font font = theme.getFont();

        // Search for a font that can support the sample string
        String sampleResource = (String)resources.get("ADD_CONTACT");
        if (font.canDisplayUpTo(sampleResource) != -1) {
            Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

            for (int i = 0; i < fonts.length; i++) {
                if (fonts[i].canDisplayUpTo(sampleResource) == -1) {
                    theme.setFont(fonts[i].deriveFont(Font.PLAIN, 12));
                    break;
                }
            }
        }

        BXMLSerializer bxmlSerializer = new BXMLSerializer();
        contactListWindow = (Window)bxmlSerializer.readObject(MainTest.class.getResource("ContactList.bxml"), resources);
        chatRoomWindow = (Window)bxmlSerializer.readObject(MainTest.class.getResource("ChatRoom.bxml"), resources);
        videoPhone = (Window)bxmlSerializer.readObject(MainTest.class.getResource("VideoPhone.bxml"), resources);

        display.getHostWindow().setSize(260,195);
        display.getHostWindow().setLocation(300, 325);
        contactListWindow.open(display);
        chatRoomWindow.open(DesktopApplicationContext.createDisplay(280, 420, 10, 100, false, true, false, null, null));
        videoPhone.open(DesktopApplicationContext.createDisplay(640, 480, 570, 40, false, true, false, null, null));

    }

    @Override
    public boolean shutdown(boolean optional) {
        if (contactListWindow != null) {
            System.out.println("Window Shutdown");
            contactListWindow.close();
        }

        return false;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    public static void main(String[] args) {
        DesktopApplicationContext.main(MainTest.class, args);
    }
}