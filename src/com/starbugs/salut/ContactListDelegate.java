package com.starbugs.salut;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by starsasumi on 06/05/2014.
 */
public interface ContactListDelegate {
    public ArrayList<Contact> getContactListData();

    void contactClickedAction(Object source);
}
