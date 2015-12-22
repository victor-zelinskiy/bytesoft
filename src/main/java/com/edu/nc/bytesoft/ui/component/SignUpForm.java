package com.edu.nc.bytesoft.ui.component;

import com.edu.nc.bytesoft.ui.MainUI;
import com.vaadin.server.Responsive;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.security.VaadinSecurity;

@PrototypeScope
@SpringComponent
public class SignUpForm extends VerticalLayout{

    private final VaadinSecurity vaadinSecurity;
    private final EventBus.SessionEventBus eventBus;


    @Autowired
    public SignUpForm(VaadinSecurity vaadinSecurity, EventBus.SessionEventBus eventBus) {
        this.vaadinSecurity = vaadinSecurity;
        this.eventBus = eventBus;
        initLayout();
    }

    private void initLayout() {
        setSizeFull();
        Component signUpForm = signUpForm();
        addComponent(signUpForm);
        setComponentAlignment(signUpForm, Alignment.MIDDLE_CENTER);
    }

    private Component signUpForm() {
        final VerticalLayout signUpPanel = new VerticalLayout();
        signUpPanel.setSizeUndefined();
        signUpPanel.setSpacing(true);
        Responsive.makeResponsive(signUpPanel);
        signUpPanel.addComponent(buildFields());
        return signUpPanel;
    }

    private Component buildFields() {

        addComponent(new Label("Hello!"));

        VerticalLayout fields = new VerticalLayout();
        fields.setSpacing(true);
        fields.addStyleName("fields");
        addComponent(new TextField("Login:"));
        addComponent(new PasswordField("Password:"));
        addComponent(new TextField("Name:"));
        addComponent(new TextField("Email:"));
        addComponent(new TextField("Phone:"));
//        TextField login = new TextField("Login:");
//        login.setStyleName(ValoTheme.TEXTAREA_HUGE);
//        fields.addComponent(login);
//
//        TextArea loginArea = new TextArea("login");
//        fields.addComponent(loginArea);

        Component buttonBack = new Button("Back");
        buttonBack.setStyleName(ValoTheme.BUTTON_PRIMARY);
        addComponent(buttonBack);
        buttonBack.addListener((Listener) event -> MainUI.getCurrent().setContent(new MainUI()));

        return fields;
    }

}
