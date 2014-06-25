package com.starbugs.salut;

import com.starbugs.salut.util.GeneralTaskListener;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.*;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.Window;

import javax.xml.ws.Binding;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

/**
 * Created by starsasumi on 04/05/2014.
 */
public class LoginWindow extends Window implements Bindable{
    @BXML private TextInput userAccountInput;
    @BXML private TextInput passwordInput;
    @BXML private PushButton loginButton;
    @BXML private ActivityIndicator loginActivityIndicator;
    @BXML private Label errorInfo;

    private ComponentKeyListener enterPressListener = new ComponentKeyListener.Adapter() {
        @Override
        public boolean keyReleased(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
            if (keyCode == Keyboard.KeyCode.ENTER) {
                if (component == userAccountInput) {
                    passwordInput.requestFocus();
                } else if (component == passwordInput) {
                    loginButton.press();
                }
            }
            return true;
        }
    };

    private LoginActionDelegate loginActionDelegate;

    @Override
    public void initialize(Map<String, Object> strings, URL url, Resources strings2) {
        // Add Enter Key Action
        userAccountInput.getComponentKeyListeners().add(enterPressListener);
        passwordInput.getComponentKeyListeners().add(enterPressListener);

        // Add Login Action
        loginButton.getButtonPressListeners().add(new ButtonPressListener(){
            @Override
            public void buttonPressed(Button button) {
                loginActivityIndicator.setActive(true);
                loginButton.setEnabled(false);
                userAccountInput.setEnabled(false);
                passwordInput.setEnabled(false);
                LoginWindow.this.repaint();

                LoginTask task = new LoginTask(loginActionDelegate, LoginWindow.this, getUserAccount(), getPassword());

                task.execute(new TaskAdapter<String>(new GeneralTaskListener()));

            }
        });
        userAccountInput.setText(Salut.getDefaultAccount());
        passwordInput.setText(Salut.getDefaultPassword());

        userAccountInput.requestFocus();
    }

    public String getUserAccount() {
        return userAccountInput.getText();
    }

    public String getPassword() {
        return passwordInput.getText();
    }

    public void setLoginActionDelegate(LoginActionDelegate loginActionDelegate) {
        this.loginActionDelegate = loginActionDelegate;
    }

    public void loginFail(String error) {
        this.errorInfo.setText(error);
        this.loginActivityIndicator.setActive(false);
        this.userAccountInput.setText("");
        this.passwordInput.setText("");
        this.userAccountInput.setEnabled(true);
        this.passwordInput.setEnabled(true);
        this.loginButton.setEnabled(true);
        this.userAccountInput.requestFocus();
    }

    class LoginTask extends Task<String> {
        private LoginActionDelegate delegate;
        private Object source;
        private String userAccount;
        private String password;

        public LoginTask(LoginActionDelegate delegate, Object source, String userAccount, String password) {
            this.delegate = delegate;
            this.source = source;
            this.userAccount = userAccount;
            this.password = password;
        }

        @Override
        public String execute() throws TaskExecutionException {
            this.delegate.loginAction(this.source, this.userAccount, this.password);
            return "Login OK";
        }
    }
}
