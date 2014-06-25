package com.starbugs.salut;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.Vote;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.Label;

import java.awt.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Created by starsasumi on 10/05/14.
 */
public class IpRequestDialog extends Dialog implements Bindable {
    @BXML private TextInput ipInput;
    @BXML private Label errorInfoLabel;
    @BXML private ActivityIndicator activityIndicator;
    @BXML private PushButton confirmButton;
    @BXML private Label infoLabel;

    private InetAddress ip = null;

    private static final Pattern IPV4_PATTERN =
            Pattern.compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    private static final Pattern IPV6_STD_PATTERN =
            Pattern.compile(
                    "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN =
            Pattern.compile(
                    "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

    @Override
    public void initialize(Map<String, Object> strings, URL url, Resources strings2) {
        confirmButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                button.setEnabled(false);
                if (check()) {
                    close();
                }
            }
        });

        this.getDialogStateListeners().add(new DialogStateListener.Adapter() {
            @Override
            public Vote previewDialogClose(Dialog components, boolean b) {
                if (b) {
                    return Vote.APPROVE;
                } else {
                    return Vote.DENY;
                }
            }
        });
    }

    public InetAddress getIp() {
        return ip;
    }

    private void wrongIp() {
        ipInput.setText("");
        errorInfoLabel.setText("Wrong IP Format");
        confirmButton.setEnabled(true);
    }

    private boolean check() {
        if (IPV4_PATTERN.matcher(ipInput.getText().trim()).matches()
                || IPV6_STD_PATTERN.matcher(ipInput.getText()).matches()
                || IPV6_HEX_COMPRESSED_PATTERN.matcher(ipInput.getText()).matches()) {
            try {
                ip = InetAddress.getByName(ipInput.getText());
                return true;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            wrongIp();
            return false;
        }
    }

    @Override
    public void close(boolean isEnterPressed) {

        if (isEnterPressed)
            super.close(check());
        else
            super.close(true);

    }

    public void setInfo(final String info) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                infoLabel.setText(info);
            }
        });

    }
}
