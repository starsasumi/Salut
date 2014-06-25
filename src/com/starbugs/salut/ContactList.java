package com.starbugs.salut;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.media.Picture;
import org.apache.pivot.wtk.media.Image;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import javax.imageio.ImageIO;
import java.awt.EventQueue;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by starsasumi on 06/05/2014.
 */
// TODO: Implement AddContact logic
public class ContactList implements RosterListener, ContactListDelegate, ChatRoomDelegate {
    public static final String AVATAR_BASE = "file://salut/resources/img/";
    public static final String DEFAULT_AVATAR = AVATAR_BASE + "defaultAvatar";

    private ContactListWindow contactListWindow;
    private XMPPConnection connection;
    private Roster roster;

    private SalutMessageListener messageListener = new SalutMessageListener();

    private HashMap<String, ChatRoom> chatRooms = new HashMap<String, ChatRoom>();

    public ContactList(XMPPConnection connection) {
        this.connection = connection;
        this.roster = connection.getRoster();
        this.roster.addRosterListener(this);
        this.connection.getChatManager().addChatListener(
                new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally)
                    {
                        if (!createdLocally)
                            createChatRoom(chat);
                    }
                });
        this.connection.addPacketListener(new MessagePackageListener(), new MessageTypeFilter(Message.Type.chat));
    }

    @Override
    public void entriesAdded(Collection<String> strings) {
        this.contactListWindow.updateContactList();
    }

    @Override
    public void entriesUpdated(Collection<String> strings) {
        this.contactListWindow.updateContactList();
    }

    @Override
    public void entriesDeleted(Collection<String> strings) {
        this.contactListWindow.updateContactList();
    }

    @Override
    public void presenceChanged(Presence presence) {
        System.out.println("presenceChanged");
        if (this.contactListWindow != null)
            this.contactListWindow.updateContactList();
    }

    public void openContactListWindow(Display display) {
        BXMLSerializer bxmlSerializer = new BXMLSerializer();

        try {
            Resources langResources = Salut.getLangResources();
            this.contactListWindow = (ContactListWindow) bxmlSerializer.readObject(getClass().getResource("resources/bxml/ContactListWindow.bxml"),
                    langResources);
            this.contactListWindow.setDelegate(ContactList.this);
            String titleBase = (String) langResources.get(ContactListWindow.TITLE_BASE);
            this.contactListWindow.setTitle(titleBase + Salut.getUserName());
            this.contactListWindow.open(display);
            this.contactListWindow.updateContactList();
            DesktopApplicationContext.sizeHostToFit(this.contactListWindow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Contact> getContactListData() {
        Collection<RosterEntry> entries = this.roster.getEntries();
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        for (RosterEntry entry : entries) {
            Image avatar = getAvatar(entry.getUser());
            contacts.add(new Contact(entry.getName(), this.roster.getPresence(entry.getUser()).toString(), avatar, entry.getUser()));
        }
        return contacts;
    }

    private void createChatRoom(Chat chat) {
        chat.addMessageListener(this.messageListener);
        ChatRoom chatRoom = new ChatRoom(chat, this, this.connection.getRoster().getEntry(chat.getParticipant().split("/")[0]).getName());
        this.chatRooms.put(getChattingFriend(chat), chatRoom);
        System.out.println("Create A ChatRoom:\n\t" + chat.getParticipant() + ", \n\t" + chat.getThreadID());
    }

    @Override
    public void contactClickedAction(Object source) {
        if (source instanceof ContactPane) {
            String userToChat = ((ContactPane) source).getIdentity();
            final ChatRoom chatRoom;
            if (!this.chatRooms.containsKey(userToChat)) {
                this.connection.addPacketListener(new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) {
                        if (packet instanceof Message) {
                            Message message = (Message) packet;
                            System.out.println("Received message: " + message);
                        }
                    }
                }, new FromMatchesFilter(userToChat));
                this.createChatRoom(this.connection.getChatManager().createChat(userToChat, null));
            }
            chatRoom = chatRooms.get(userToChat);
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    chatRoom.openChatRoomWindow();
                }
            });
        }
    }

    private String getChattingFriend(Chat chat) {
        return (chat != null)?chat.getParticipant().split("/")[0]:null;
    }

    private boolean isTextMessage(Message message) {
        return message.getBody() != null && !message.getBody().isEmpty();
    }

    @Override
    public String getUser() {
        return this.connection.getUser().split("/")[0];
    }

    private class SalutMessageListener implements MessageListener {

        @Override
        public void processMessage(Chat chat, Message message) {
            ChatRoom chatRoom = chatRooms.get(getChattingFriend(chat));
            if (chatRoom == null) {
                return;
            }

            if (message.getProperty("SalutVideoCall") != null) {
                chatRoom.receivedVideoCallAction(message);
            } else if(isTextMessage(message)) {
                    chatRoom.addMessage(message);

                contactListWindow.updateExtraInfo(getChattingFriend(chat), message.getBody());
            }
        }
    }

    private class MessagePackageListener implements PacketListener {

        @Override
        public void processPacket(Packet packet) {
            Message message = (Message)packet;
            if (chatRooms.containsKey(packet.getFrom())) {
                String thread = message.getThread();
                ChatRoom chatRoom = chatRooms.get(packet.getFrom());
                if (! chatRoom.getChatThread().equals(message.getThread())) {
                    if (message.getProperty("SalutVideoCall") != null) {
                        System.out.println("Received A video call:\n\t"
                                + message.getProperty("SalutVideoCall") + "\n\t"
                                + message.getProperty("ip") + "\n\t"
                                + message.getProperty("port"));
                    } else {
                        chatRoom.addMessage(message);
                        contactListWindow.updateExtraInfo(packet.getFrom(), message.getBody());
                    }
                }
            }
        }
    }

    public Image getAvatar(String user) {
        user = user.split("/")[0];
        Image avatar = null;
        try {
            URL urlKey = new URL(ContactList.AVATAR_BASE + user);
            avatar = (Picture) DesktopApplicationContext.getResourceCache().get(urlKey);
            if (avatar == null) {
                VCard vCard = new VCard();
                vCard.load(this.connection, user);
                byte[] avatarBytes = vCard.getAvatar();
                if (avatarBytes != null) {
                    avatar = new Picture((ImageIO.read(new ByteArrayInputStream(avatarBytes))));
                    DesktopApplicationContext.getResourceCache().put(urlKey, avatar);
                } else {
                    avatar = (Image) DesktopApplicationContext.getResourceCache().get(new URL(ContactList.DEFAULT_AVATAR));
                    DesktopApplicationContext.getResourceCache().put(urlKey, avatar);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avatar;
    }
}
