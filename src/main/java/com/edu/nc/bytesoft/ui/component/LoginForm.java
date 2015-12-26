package com.edu.nc.bytesoft.ui.component;

import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.ui.MainUI;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.security.util.SuccessfulLoginEvent;

@PrototypeScope
@SpringComponent
public class LoginForm extends VerticalLayout {
    private final VaadinSecurity vaadinSecurity;
    private final EventBus.SessionEventBus eventBus;
    private static final Log LOG = Log.get(LoginForm.class);

    @Autowired
    ApplicationContext applicationContext;


    @Autowired
    public LoginForm(VaadinSecurity vaadinSecurity, EventBus.SessionEventBus eventBus) {
        this.vaadinSecurity = vaadinSecurity;
        this.eventBus = eventBus;
        initLayout();
    }

    private void initLayout() {
        setSizeFull();
        Component loginForm = buildLoginForm();
        addComponent(loginForm);
        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
    }

    private Component buildLoginForm() {
        final VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setSizeUndefined();
        loginPanel.setSpacing(true);
        Responsive.makeResponsive(loginPanel);
        loginPanel.addComponent(buildLabels());
        loginPanel.addComponent(buildFields());
        return loginPanel;
    }

    private Component buildLabels() {
        CssLayout labels = new CssLayout();

        Label welcome = new Label("Welcome to ");
        welcome.setSizeUndefined();
        welcome.addStyleName(ValoTheme.LABEL_H4);
        welcome.addStyleName(ValoTheme.LABEL_COLORED);
        labels.addComponent(welcome);

        Label title = new Label("ByteSoft");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        labels.addComponent(title);
        return labels;
    }


    private Component buildFields() {
        HorizontalLayout fields = new HorizontalLayout();
        fields.setSpacing(true);
        fields.addStyleName("fields");

        final TextField username = new TextField("Username");
        username.setIcon(FontAwesome.USER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        final PasswordField password = new PasswordField("Password");
        password.setIcon(FontAwesome.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        final Button signin = new Button("Sign In");
        signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        signin.focus();

        final Button createModule = new Button("Create module");
        createModule.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        createModule.focus();

        final Button createTask = new Button("Create Task");
        createModule.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        createModule.focus();

        final Button signup = new Button("Sign Up");
        signup.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signup.focus();

        fields.addComponents(username, password, signin, signup, createModule, createTask);
        fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);
        fields.setComponentAlignment(createModule, Alignment.BOTTOM_LEFT);
        fields.setComponentAlignment(createTask, Alignment.BOTTOM_LEFT);
        fields.setComponentAlignment(signup, Alignment.BOTTOM_LEFT);

        signin.addClickListener((Button.ClickListener) event -> login(username
                .getValue(), password.getValue()));

        signup.addClickListener((Button.ClickListener) event -> setSignUpWindow());
        createModule.addClickListener((Button.ClickListener) moduleEvent -> MainUI.getCurrent().setContent(
                new ModuleForm(vaadinSecurity, eventBus)));
        createTask.addClickListener((Button.ClickListener) moduleEvent -> MainUI.getCurrent().setContent(
                new TaskForm(vaadinSecurity, eventBus)));
        return fields;

    }

    private void login(String login, String password) {
        try {
            final Authentication authentication = vaadinSecurity.login(login, password);
            eventBus.publish(this, new SuccessfulLoginEvent(getUI(), authentication));
        } catch (AuthenticationException ex) {
            Notification notification = new Notification(
                    "Wrong password or username");
            notification.setStyleName(ValoTheme.NOTIFICATION_ERROR);
            notification.setPosition(Position.MIDDLE_CENTER);
            notification.setDelayMsec(1500);
            notification.show(Page.getCurrent());
        } catch (Exception ex) {
            Notification.show("An unexpected error occurred", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            LoggerFactory.getLogger(getClass()).error("Unexpected error while logging in", ex);
        }
    }

    public void setSignUpWindow(){
        SignUpForm sub = new SignUpForm();
        sub.setWidth("95%");
        sub.setHeight("95%");
        MainUI.getCurrent().addWindow(sub);
    }
}
