package com.starbugs.salut;

import com.starbugs.salut.util.GeneralTaskListener;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Cursor;
import org.apache.pivot.wtk.Window;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by starsasumi on 05/05/2014.
 */
public class ContactListWindow extends Window implements Bindable{
    public static final String TITLE_BASE = "contactListWindow";

    @BXML private BoxPane addContactPane;
    @BXML private GridPane contactListPane;

    private ContactListDelegate delegate;
    private ArrayList<Contact> contacts;
    private Task<ArrayList<Contact>> getDataTask = new Task<ArrayList<Contact>>() {
        @Override
        public ArrayList<Contact> execute() throws TaskExecutionException {
            return  contacts = delegate.getContactListData();
        }
    };

    @Override
    public void initialize(Map<String, Object> strings, URL url, Resources strings2) {
        addContactPane.setCursor(Cursor.HAND);
    }

    public void updateContactList() {
        if (this.getDataTask.isPending()) {
            return;
        }
        this.getDataTask.execute(new TaskAdapter<ArrayList<Contact>>(new TaskListener<ArrayList<Contact>>() {
            @Override
            public void taskExecuted(Task<ArrayList<Contact>> arrayListTask) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }

            @Override
            public void executeFailed(Task<ArrayList<Contact>> arrayListTask) {
                System.err.println("Failed to get contact list");
            }
        }));
    }

    private void updateUI() {
        if (this.contactListPane.getRows().getLength() > 0) {
            this.contactListPane.getRows().remove(0, this.contactListPane.getRows().getLength());
        }
        for (Contact contact : contacts) {
            addContact(contact);
        }

        if (this.getDisplay() != null)
            DesktopApplicationContext.sizeHostToFit(this);
    }

    private void addContact(Contact contact) {
        ContactPane contactPane = null;
        BXMLSerializer bxmlSerializer = new BXMLSerializer();
        try {
            contactPane = (ContactPane) bxmlSerializer.readObject(this.getClass(), "resources/bxml/ContactPane.bxml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SerializationException e) {
            e.printStackTrace();
        }

        contactPane.setUserName(contact.getUserName());
        contactPane.setExtraInfo(contact.getExtraInfo());
        contactPane.setAvatar(contact.getAvatar());
        contactPane.setIdentity(contact.getIdentity());
        contactPane.getComponentMouseButtonListeners().add(new ComponentMouseButtonListener.Adapter(){
            @Override
            public boolean mouseUp(Component component, Mouse.Button button, int x, int y) {
                if (button.equals(Mouse.Button.LEFT) && (component instanceof ContactPane)) {
                    final ContactPane pane = (ContactPane) component;
                    pane.setExtraInfoColor("#929292");
                    Task<String> contactClickedTask = new Task<String>() {
                        @Override
                        public String execute() throws TaskExecutionException {
                            delegate.contactClickedAction(pane);
                            return pane.getUserName() + " is clicked! User Id: " +pane.getIdentity();
                        }
                    };
                    contactClickedTask.execute(new TaskAdapter<String>(new GeneralTaskListener()));
                }
                return super.mouseUp(component, button, x, y);
            }
        });

        GridPane.Row row = new GridPane.Row();
        row.add(contactPane);
        this.contactListPane.getRows().add(row);

    }

    public void setDelegate(ContactListDelegate delegate) {
        this.delegate = delegate;
    }

    public void updateExtraInfo(final String identity, final String extraInfo) {
        GridPane.RowSequence contactPanes = contactListPane.getRows();
        for (GridPane.Row row : contactPanes) {
            ContactPane contactPane = (ContactPane) row.get(0);
            if (contactPane.getIdentity().equals(identity)) {
                final ContactPane target = contactPane;
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        target.setExtraInfoColor("#68B25B");
                        target.setExtraInfo(extraInfo);
                    }
                });
                break;
            }
        }
    }
}
