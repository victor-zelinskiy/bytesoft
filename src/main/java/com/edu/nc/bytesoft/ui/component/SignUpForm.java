package com.edu.nc.bytesoft.ui.component;

import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.model.Contact;
import com.edu.nc.bytesoft.model.NamedEntity;
import com.edu.nc.bytesoft.model.Role;
import com.edu.nc.bytesoft.model.User;
import com.edu.nc.bytesoft.service.UserService;
import com.edu.nc.bytesoft.service.exception.NotUniqueEmailException;
import com.edu.nc.bytesoft.service.exception.NotUniqueLoginException;
import com.edu.nc.bytesoft.ui.events.SuccessUserCreatedEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@PrototypeScope
@SpringComponent
public class SignUpForm extends Window {

    private static final Log LOG = Log.get(SignUpForm.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EventBus.SessionEventBus eventBus;


    private Button cancel = new Button("Cancel", this::cancel);
    private Button createContactButton = new Button("Create Contact", this::createContact);

    private TextField login = new TextField("Login: ");
    private PasswordField password = new PasswordField("Password:");
    private TextField name = new TextField("Name:");
    private TextField companyName = new TextField("Company name:");
    private TextField email = new TextField("Email:");
    private TextField phone = new TextField("Phone:");

    private TextField contactName = new TextField("Contact name:");
    private TextField contactEmail = new TextField("Contact email:");
    private TextField contactPhone = new TextField("Contact phone:");

    private Grid grid = new Grid();

    private FormLayout contactForm = new FormLayout();
    private FormLayout signUpPanel = new FormLayout();

    private User newUser = new User();
    private List contactList = new ArrayList<>();
    private List phoneList = new ArrayList<>();

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
        signUpPanel.addComponent(new Label("Personal Info"));

        OptionGroup optionGroup = new OptionGroup("Who are you?");
        optionGroup.addItems("Customer", "Developer");
        Button createUser = new Button("Create user", event -> {
            newUser.setUsername(login.getValue());
            newUser.setPassword(password.getValue());
            newUser.setEmail(email.getValue());
            newUser.setRegistered(new Date());
            newUser.setCompanyName(companyName.getValue());
            newUser.setName("".equals(name.getValue()) ? null : name.getValue());
            newUser.setContacts(contactList);
            if(!(phone.getValue().isEmpty())) {
                phoneList.add(new NamedEntity(phone.getValue()));
                newUser.setPhones(phoneList);
            }
            Role userRole = null;
            switch (String.valueOf(optionGroup.getValue())) {
                case "Customer" :
                    userRole = Role.ROLE_CUSTOMER;
                    newUser.setCompanyName("".equals(companyName.getValue()) ? null : companyName.getValue());
                    break;
                case "Developer" : userRole = Role.ROLE_DEVELOPER;
                    break;
            }
            newUser.getRoles().add(userRole);
            try {
                userService.save(newUser);
             //   LOG.error("after save " + userService.getUserById(test.getId()));
                eventBus.publish(SignUpForm.this, new SuccessUserCreatedEvent(getUI(), newUser));
                cancel(event);
            } catch (NoSuchObjectException | SQLException | NotUniqueLoginException | NotUniqueEmailException e) {
                Notification.show("Error during creation user", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });
        createUser.setEnabled(false);
        createUser.setStyleName(ValoTheme.BUTTON_PRIMARY);
        createUser.setWidth(185, Sizeable.UNITS_PIXELS);
        password.addValidator(new StringLengthValidator("Password must be at least 6 characters", 6, 30, false));
        password.setRequired(true);
        password.setRequiredError("Password must be filled in");
        password.addValueChangeListener(event -> createUser.setEnabled(checkValidFields()));

        login.addValidator(new StringLengthValidator("Login must be at least 5 characters", 5, 30, false));
        login.addValidator((Validator) value -> {
            try {
                if(!userService.isUsernameUnique((String) value)) {
                    throw new Validator.InvalidValueException("Login already exists");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        login.setRequired(true);
        login.setRequiredError("Login must be filled in");
        login.addValueChangeListener(event -> createUser.setEnabled(checkValidFields()));

        email.addValidator(new EmailValidator("Invalid email address"));
        email.addValidator((Validator) value -> {
            try {
                if(!userService.isEmailUnique((String) value)) {
                    throw new Validator.InvalidValueException("Email already in use");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        email.setRequired(true);
        email.setRequiredError("Email must be filled in");
        email.addValueChangeListener(event -> createUser.setEnabled(checkValidFields()));
        companyName.setRequired(true);
        optionGroup.setImmediate(true);
        optionGroup.addListener((Property.ValueChangeListener) event -> {
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
        });
        signUpPanel.addComponent(optionGroup);
    }

    private boolean checkValidFields() {
        return password.isValid() && login.isValid() && email.isValid();
    }
    private boolean checkValidFieldsContact() {
        return contactName.isValid() && contactEmail.isValid() && contactPhone.isValid();
    }
    public void cancel(Button.ClickEvent event) {
        close();
    }

    public void createContact(Button.ClickEvent event) {
        contactName.addValidator(new StringLengthValidator("Contact name must be at least 6 characters", 6, 30, false));
        contactName.setRequired(true);
        contactName.setRequiredError("Contact must be filled in");
        contactName.addValueChangeListener(event1 -> createContactButton.setEnabled(checkValidFieldsContact()));
        contactEmail.addValidator(new EmailValidator("Invalid email address"));
        contactEmail.addValidator((Validator) value -> {
            try {
                if(!userService.isEmailUnique((String) value)) {
                    throw new Validator.InvalidValueException("Email already in use");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        contactEmail.setRequired(true);
        contactEmail.setRequiredError("Email must be filled in");
        contactEmail.addValueChangeListener(event1 -> createContactButton.setEnabled(checkValidFieldsContact()));
        grid.addRow(contactName.getValue(), contactEmail.getValue(), contactPhone.getValue());
        saveContact(contactName.getValue(), contactEmail.getValue(), contactPhone.getValue());
        contactName.setValue("");
        contactEmail.setValue("");
        contactPhone.setValue("");
    }

    public void saveContact(String contactName,String contactEmail,String contactPhone)
    {
        Contact newContact = new Contact();
        newContact.setName(contactName);
        newContact.setEmail(contactEmail);
        newContact.getPhones().add(new NamedEntity(contactPhone));
        contactList.add(newContact);
    }

}
