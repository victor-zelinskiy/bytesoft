package com.edu.nc.bytesoft.ui.component;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;

@PrototypeScope
@SpringComponent
public class SignUpForm extends Window {

    Button cancel = new Button("Cancel", this::cancel);
    Button createContactButton = new Button("Create Contact", this::createContact);

    TextField login = new TextField("Login: ");
    PasswordField password = new PasswordField("Password:");
    TextField name = new TextField("Name:");
    TextField companyName = new TextField("Company name:");
    TextField email = new TextField("Email:");
    TextField phone = new TextField("Phone:");

    TextField contactName = new TextField("Contact name:");
    TextField contactEmail = new TextField("Contact email:");
    TextField contactPhone = new TextField("Contact phone:");

    Grid grid = new Grid();

    FormLayout contactForm = new FormLayout();
    FormLayout signUpPanel = new FormLayout();


    @Autowired
    ApplicationContext applicationContext;

    public SignUpForm() {
        center();
        setClosable(true);
        setModal(true);
        setDraggable(false);
        setResizable(false);
        setContent(signUpForm());
    }

    private Component signUpForm() {
        cancel.setStyleName(ValoTheme.BUTTON_PRIMARY);
        AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setSizeFull();

        createSignUpForm();
        createContactForm();
        createContactGrid();

        absoluteLayout.addComponent(signUpPanel, "left: 20px; top: 0px;");
        absoluteLayout.addComponent(contactForm, "left: 350px; top: 0px;");
        absoluteLayout.addComponent(grid, "left: 350px; top: 287px;");
        return absoluteLayout;
    }

    private void createContactForm() {
        contactForm.addComponent(new Label("Add contacts:"));
        contactForm.addComponent(contactName);
        contactForm.addComponent(contactEmail);
        contactForm.addComponent(contactPhone);
        createContactButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        createContactButton.setWidth(185, Sizeable.UNITS_PIXELS);
        createContactButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        contactForm.addComponent(createContactButton);
        contactForm.setVisible(false);
    }

    private void createContactGrid() {
        grid.setWidth("400px");
        grid.setHeight("200px");
        grid.addColumn("Name");
        grid.addColumn("Email");
        grid.addColumn("Phone");
        grid.setVisible(false);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    private void createSignUpForm() {
//        fields.addComponent(new Label("Hello!"));
        signUpPanel.addComponent(new Label("Personal Info"));

        OptionGroup optionGroup = new OptionGroup("Who are you?");
        optionGroup.addItems("Customer", "Developer");

        Button createUser = new Button("Create user");
        createUser.setStyleName(ValoTheme.BUTTON_PRIMARY);
        createUser.setWidth(185, Sizeable.UNITS_PIXELS);

        optionGroup.setImmediate(true);
        optionGroup.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (optionGroup.getValue().equals("Developer")) {
                    signUpPanel.addComponents(login, password, name, email, phone, createUser);
                    signUpPanel.removeComponent(companyName);
                    grid.setVisible(false);
                    contactForm.setVisible(false);
                } else {
                    signUpPanel.removeComponent(login);
                    signUpPanel.removeComponent(password);
                    signUpPanel.removeComponent(name);
                    signUpPanel.removeComponent(email);
                    signUpPanel.removeComponent(phone);
                    signUpPanel.removeComponent(createUser);
                    signUpPanel.addComponents(login, password, name, email, companyName, phone, createUser);
                    grid.setVisible(true);
                    contactForm.setVisible(true);
                }
            }
        });
        signUpPanel.addComponent(optionGroup);
    }

    public void cancel(Button.ClickEvent event) {
        close();
    }

    public void createContact(Button.ClickEvent event) {
        grid.addRow(contactName.getValue(), contactEmail.getValue(), contactPhone.getValue());
        contactName.setValue("");
        contactEmail.setValue("");
        contactPhone.setValue("");
    }

}
