package com.edu.nc.bytesoft.ui.component;

import com.edu.nc.bytesoft.ui.MainUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.security.VaadinSecurity;

@PrototypeScope
@SpringComponent
public class SignUpForm extends VerticalLayout{

    @Autowired
    ApplicationContext applicationContext;

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
        setComponentAlignment(signUpForm, Alignment.TOP_LEFT);
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

        VerticalLayout fields = new VerticalLayout();

        fields.addComponent(new Label("Hello!"));

        OptionGroup optionGroup = new OptionGroup("Who are you?");
        Item item1 = optionGroup.addItem("Customer");
        Item item2 = optionGroup.addItem("Developer");
//        optionGroup.addItems("Customer", "Developer");

        Notification notification = new Notification("azaza");
        notification.setPosition(Position.MIDDLE_CENTER);

        optionGroup.setImmediate(true);
        optionGroup.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {

            }
        });


        fields.addComponent(optionGroup);
        fields.addComponent(new TextField("Login:"));
        fields.addComponent(new PasswordField("Password:"));
        fields.addComponent(new TextField("Name:"));
        fields.addComponent(new TextField("Email:"));
        fields.addComponent(new TextField("Phone:"));

        Component buttonBack = new Button("Back");
        buttonBack.setStyleName(ValoTheme.BUTTON_PRIMARY);
        fields.addComponent(buttonBack);
        buttonBack.addListener((Listener) event -> MainUI.getCurrent().setContent(applicationContext.getBean(MainUI.class)));

        return fields;
    }

}
