package com.starbugs.salut;

import com.starbugs.salut.util.GeneralTaskListener;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.Time;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Cursor;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.ScrollPane;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.media.Image;

import java.awt.*;
import java.net.InetAddress;
import java.net.URL;

import static com.starbugs.salut.Salut.getLangResources;

/**
 * Created by starsasumi on 07/05/2014.
 */
public class ChatRoomWindow extends Window implements Bindable{
    public static final String TITLE_BASE = "chatRoomWindow";

    private static final String MAKE_VIDEO_CALL_ACTION = "makeVideoCall";

    @BXML private FlowPane toolBar;
    @BXML private BoxPane chatContent;
    @BXML private TextArea messageTextArea;
    @BXML private Border sendMessageBorder;
    @BXML private BoxPane sendMessagePane;
    @BXML private BoxPane sendMessagePrompt;
    @BXML private ScrollPane chatScrollPane;
    @BXML private PushButton videoButton;

    private ChatActionDelegate chatActionDelegate;
    private CallActionDelegate callActionDelegate;
    private org.apache.pivot.wtk.media.Image selfAvatar;
    private org.apache.pivot.wtk.media.Image otherAvatar;

    public ChatRoomWindow() {
        super();
        Action.getNamedActions().put(MAKE_VIDEO_CALL_ACTION, new Action() {
            @Override
            public void perform(Component component) {
                makeVideoCall();
            }
        });
    }

    public void initialize(Map<String, Object> strings, URL url, Resources strings2) {
        this.sendMessagePrompt.setCursor(Cursor.HAND);

        // Press Enter to Send A message
        this.messageTextArea.getComponentKeyListeners().add(new ComponentKeyListener.Adapter() {
            @Override
            public boolean keyReleased(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
                if (keyCode == Keyboard.KeyCode.ENTER) {
                    sendMessagePrompt.setVisible(true);
                    doSendMessage();
                }
                return super.keyReleased(component, keyCode, keyLocation);
            }
        });

        // Determine whether to display sendMessagePrompt
        this.messageTextArea.getTextAreaContentListeners().add(new TextAreaContentListener.Adapter(){
            @Override
            public void textChanged(TextArea textArea) {
                if (textArea.getText().trim().isEmpty()) {
                    sendMessagePrompt.setVisible(true);
                } else {
                    sendMessagePrompt.setVisible(false);
                }

                super.textChanged(textArea);
            }
        });

        // Determine the area to be shown in the charContent's ScrollPane
        this.chatContent.getComponentListeners().add(new ComponentListener.Adapter(){
            @Override
            public void sizeChanged(Component component, int previousWidth, int previousHeight) {
//                System.out.println("Content: " + chatContent.getHeight() + ", Scroll: " + chatScrollPane.getHeight());
                // TODO: This bounds will cause an exception, fix!
                if (chatContent.getHeight() > chatScrollPane.getHeight())
                    chatScrollPane.scrollAreaToVisible(
                            new Bounds(0, chatContent.getHeight() - chatScrollPane.getHeight(),
                                    chatScrollPane.getWidth(), chatScrollPane.getHeight()));
                super.sizeChanged(component, previousWidth, previousHeight);
            }
        });

        // Click video button action
        this.videoButton.setAction(MAKE_VIDEO_CALL_ACTION);

    }

    private void doSendMessage() {
        final String message = this.messageTextArea.getText().trim();
        this.messageTextArea.setText("");
        Task<String> task = new Task<String>() {
            @Override
            public String execute() throws TaskExecutionException {
                chatActionDelegate.sendMessage(message);
                return "Send: " + message;
            }
        };

        task.execute(new TaskAdapter<String>(new GeneralTaskListener()));
    }

    public void addMessageToRoom(String message, Time time, boolean fromLoginUser) {
//        if (message == null || message.isEmpty()) {
//            return;
//        }
        BXMLSerializer serializer = new BXMLSerializer();
        MessageBubblePane messageBubble;
        try {
            if (fromLoginUser) {
                messageBubble = (MessageBubblePane) serializer.readObject(this.getClass(), "resources/bxml/RightBubblePane.bxml");
                messageBubble.setAvatar(this.selfAvatar);
            } else {
                messageBubble = (MessageBubblePane) serializer.readObject(this.getClass(), "resources/bxml/LeftBubblePane.bxml");
                messageBubble.setAvatar(this.otherAvatar);
            }
            messageBubble.setMessage(message);
            messageBubble.setTime(time);
            this.chatContent.add(messageBubble);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setChatActionDelegate(ChatActionDelegate chatActionDelegate) {
        this.chatActionDelegate = chatActionDelegate;
    }

    private void makeVideoCall() {
        BXMLSerializer serializer = new BXMLSerializer();
        IpRequestDialog dialog = null;
        try {
            Resources langResources = getLangResources();
            dialog = (IpRequestDialog) serializer.readObject(getClass().getResource("resources/bxml/IpRequestDialog.bxml"), langResources);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        dialog.open(this.getDisplay(), this, new DialogCloseListener(){

            @Override
            public void dialogClosed(Dialog components, boolean b) {
                final InetAddress ip = ((IpRequestDialog)components).getIp();
                if (ip != null) {

                    Task<String> task = new Task<String>() {
                        @Override
                        public String execute() throws TaskExecutionException {
                            callActionDelegate.makeVideoCall(ip);
                            return "Make Video Call Action: " + ip;
                        }
                    };
                    task.execute(new TaskAdapter<String>(new GeneralTaskListener()));
                }
            }
        });
    }

    public void setCallActionDelegate(CallActionDelegate callActionDelegate) {
        this.callActionDelegate = callActionDelegate;
    }

    public void receivedVideoCall() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BXMLSerializer serializer = new BXMLSerializer();
                IpRequestDialog dialog = null;
                try {
                    Resources langResources = Salut.getLangResources();
                    dialog = (IpRequestDialog) serializer.readObject(getClass().getResource("resources/bxml/IpRequestDialog.bxml"), langResources);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                dialog.setTitle("You have received a video call");
                dialog.setInfo("You have received a video call. To accept, enter your IP Address please.");
                if (ChatRoomWindow.this.isOpen()) {
                    Display display = ChatRoomWindow.this.getDisplay();
                    Window owner = ChatRoomWindow.this;
                    dialog.open(display, owner, new DialogCloseListener(){

                        @Override
                        public void dialogClosed(Dialog components, boolean b) {
                            final InetAddress ip = ((IpRequestDialog)components).getIp();

                            Task<String> task = new Task<String>() {
                                @Override
                                public String execute() throws TaskExecutionException {
                                    callActionDelegate.answerVideoCall(ip);
                                    return "Make Video Call Action: " + ip;
                                }
                            };
                            task.execute(new TaskAdapter<String>(new GeneralTaskListener()));
                        }
                    });

                } else {
                    // TODO: Tell User there is a video call when ChatRoom is not opened
                }

            }
        });
    }

    public void setOtherAvatar(Image otherAvatar) {
        this.otherAvatar = otherAvatar;
    }

    public void setSelfAvatar(Image selfAvatar) {
        this.selfAvatar = selfAvatar;
    }
}
