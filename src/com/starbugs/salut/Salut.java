package com.starbugs.salut;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.json.JSON;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.media.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smackx.packet.VCard;

import java.awt.*;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Created by starsasumi on 04/05/2014.
 */
public class Salut extends Application.Adapter implements LoginActionDelegate{
    public static final String LANGUAGE_KEY = "language";
    private static final String LANGUAGE_RESOURCES = "file://salut/resources/lang";
    private static Resources langResources = null;
    private static String userName = null;
    private static String defaultAccount = "";
    private static String defaultPassword = "";

    private LoginWindow loginWindow = null;

    private XMPPConnection xmppConnection = null;
    private ContactList contactList;

    @Override
    public void startup(Display display, Map<String, String> properties) throws Exception {
        String language = properties.get(LANGUAGE_KEY);
        Locale locale = (language == null) ? Locale.getDefault() : new Locale(language);

        String osName = System.getProperty("os.name");
        System.out.println(osName);
        if (osName.startsWith("Windows ")) {
            Theme.getTheme().setFont(new Font("微软雅黑", Font.PLAIN, 12));
        } else if (osName.equals("Mac OS X")) {
            Theme.getTheme().setFont(new Font("Hiragino Sans GB W3", Font.PLAIN, 12));
        } else {
            Theme theme = Theme.getTheme();
            Font font = theme.getFont();
            // Search for a font that can support the sample string
            String sampleResource = "作者：石夏星abc~>@!#$%^&*())";
            if (font.canDisplayUpTo(sampleResource) != -1) {
                Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
                for (int i = 0; i < fonts.length; i++) {
                    System.out.println(fonts[i].getFontName());
                    if (fonts[i].canDisplayUpTo(sampleResource) == -1) {
                        System.out.println("Found A Font!" + fonts[i].getName());
                        theme.setFont(fonts[i].deriveFont(Font.PLAIN, 12));
                        break;
                    }
                }
            }
        }

        Salut.langResources = new Resources("com/starbugs/salut/resources/lang/Localization", locale);
        // Default Account
        try {
            Resources defaultAccount = new Resources("com/starbugs/salut/resources/config/local/defaultAccount", locale);
            Salut.setDefaultAccount((String) defaultAccount.get("account"));
            Salut.setDefaultPassword((String) defaultAccount.get("password"));
        } catch (Exception e) { e.printStackTrace(); }

        BXMLSerializer bxmlSerializer = new BXMLSerializer();

        // Login Window
        loginWindow = (LoginWindow)bxmlSerializer.readObject(getClass().getResource("resources/bxml/LoginWindow.bxml"),
                langResources);
        loginWindow.setLoginActionDelegate(this);
        display.getHostWindow().setSize(loginWindow.getPreferredWidth(), loginWindow.getPreferredHeight());
        loginWindow.open(display);
        DesktopApplicationContext.sizeHostToFit(loginWindow);
        loginWindow.requestFocus();

    }

    @Override
    public boolean shutdown(boolean optional) {
        if (loginWindow != null) {
            loginWindow.close();
        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.awt.im.style"));
//        System.setProperty("java.awt.im.style", "on-the-spot");
        DesktopApplicationContext.main(Salut.class, args);
    }

    @Override
    public void loginAction(Object source, String userAccount, String password) {
//        System.out.println("Login: " + userAccount + ", " + password);
        final LoginWindow sourceWindow;
        if (source instanceof Window) {
            sourceWindow = (LoginWindow) source;
        } else {
            sourceWindow = this.loginWindow;
        }

//        Connection.DEBUG_ENABLED = true;
        ConnectionConfiguration configuration = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
        configuration.setSASLAuthenticationEnabled(false);
        this.xmppConnection = new XMPPConnection(configuration);
        try {
            this.xmppConnection.connect();
            this.xmppConnection.login(userAccount, password, "Salut");
        } catch (final XMPPException e) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String errorMessage = e.getMessage();
                    errorMessage = errorMessage.substring(errorMessage.indexOf(") ") + 2);  // uncheck, would go wrong
                    System.out.println("Login Fail:" + e.getMessage());
                    sourceWindow.loginFail(errorMessage);
                }
            });
            // Terminated when login fail
            return;
        }
        this.contactList = new ContactList(this.xmppConnection);
        final Display display = sourceWindow.getDisplay();
        try {
            // set default avatar
            org.apache.pivot.wtk.media.Image avatar = org.apache.pivot.wtk.media.Image.load(getClass().getResource("resources/img/avatar.png"));
            DesktopApplicationContext.getResourceCache().put(new URL(ContactList.DEFAULT_AVATAR), avatar);
            // set user avatar and name
            this.contactList.getAvatar(this.xmppConnection.getUser());
            VCard vCard = new VCard();
            vCard.load(this.xmppConnection, this.xmppConnection.getUser());

            Salut.userName = vCard.getField("FN");
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                sourceWindow.close();
                contactList.openContactListWindow(display);
            }
        });


    }

    public static String getUserName() {
        return Salut.userName;
    }

    public static Resources getLangResources() {
        return Salut.langResources;
    }

    public static String getDefaultAccount() {
        return Salut.defaultAccount;
    }

    public static String getDefaultPassword() {
        return Salut.defaultPassword;
    }

    public static void setDefaultAccount(String defaultAccount) {
        if (defaultAccount != null && !defaultAccount.isEmpty())
            Salut.defaultAccount = defaultAccount;
        else
            Salut.defaultAccount = "";
    }

    public static void setDefaultPassword(String defaultPassword) {
        if (defaultPassword != null && !defaultPassword.isEmpty())
            Salut.defaultPassword = defaultPassword;
        else
            Salut.defaultPassword = "";
    }
}
