package com.edu.nc.bytesoft.ui.view;

import com.edu.nc.bytesoft.model.Contact;
import com.edu.nc.bytesoft.model.Role;
import com.edu.nc.bytesoft.model.User;
import com.edu.nc.bytesoft.service.UserService;
import com.edu.nc.bytesoft.ui.component.SideBar;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SpringView(name = HomeView.NAME)
@SideBarItem(sectionId = SideBar.MENU, caption = "Home page", order = 0)
@FontAwesomeIcon(FontAwesome.HOME)
public class HomeView extends AbsoluteLayout implements View {

    public static final String NAME = "home";

    User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Grid contacts = new Grid();


    @PostConstruct
    void init() {

        Label header = new Label("Home page");
        header.addStyleName(ValoTheme.LABEL_H2);
        addComponent(header,"left: 20px;");

        if(currentUser.getRoles().contains(Role.ROLE_CUSTOMER))
            createCustomerView();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
    public void createCustomerView()
    {

        Label companyName = new Label("Company name : ");
        companyName.addStyleName(ValoTheme.LABEL_H2);
        addComponent(companyName,"left: 20px; top: 70px;");

        Label companyNameValue = new Label(currentUser.getCompanyName());
        companyNameValue.addStyleName(ValoTheme.LABEL_H2);
        companyNameValue.addStyleName(ValoTheme.LABEL_COLORED);
        addComponent(companyNameValue,"left: 225px; top: 70px;");

        Label dateOfReg = new Label("Date of registration : " );
        dateOfReg.addStyleName(ValoTheme.LABEL_H2);
        addComponent(dateOfReg,"left: 425px; top: 70px;");

        Date date = currentUser.getRegistered();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy hh:mm:ss", Locale.ENGLISH);
        String strDate = simpleDateFormat.format(date);
        Label dateOfRegValue = new Label(String.valueOf(strDate));
        dateOfRegValue.addStyleName(ValoTheme.LABEL_H2);
        addComponent(dateOfRegValue,"left: 700px; top: 70px;");

        Label user = new Label("User : ");
        Label email = new Label("Email : ");
        Label phone = new Label("Phone : ");
        Label contactslabel = new Label("Contacts : ");

        user.addStyleName(ValoTheme.LABEL_H2);
        email.addStyleName(ValoTheme.LABEL_H2);
        phone.addStyleName(ValoTheme.LABEL_H2);
        contactslabel.addStyleName(ValoTheme.LABEL_H2);

        addComponent(user,"left: 20px; top: 150px;");
        addComponent(email,"left: 20px; top: 200px;");
        addComponent(phone,"left: 20px; top: 250px;");
        addComponent(contactslabel,"left: 20px; top: 300px;");

        TextField userValue = new TextField();
        TextField emailValue = new TextField();
        TextField phoneValue = new TextField();
        userValue.setValue(currentUser.getUsername());
        emailValue.setValue(currentUser.getEmail());

        if(currentUser.getPhones().size()==0)
        {
            phoneValue.setValue("");
        }
        else
        {
            phoneValue.setValue(currentUser.getPhones().get(0).getName());
        }

        userValue.setReadOnly(true);
        emailValue.setReadOnly(true);
        phoneValue.setReadOnly(true);
        userValue.addStyleName(ValoTheme.LABEL_H2);
        emailValue.addStyleName(ValoTheme.LABEL_H2);
        phoneValue.addStyleName(ValoTheme.LABEL_H2);

        addComponent(userValue,"left: 225px; top:  200px;");
        addComponent(emailValue,"left: 225px; top: 250px;");
        addComponent(phoneValue,"left: 225px; top: 300px;");

        createContactGrid();
        addComponent(contacts,"left: 20px; top: 390px;");
        int size = currentUser.getContacts().size();
        for(int i=0; i<size; i++)
        {
            contacts.addRow(currentUser.getContacts().get(i).getName(),currentUser.getContacts().get(i).getEmail());
        }
    }
    private void createContactGrid() {
        contacts.setWidth("390px");
        contacts.setHeight("210px");
        contacts.addColumn("Name");
        contacts.addColumn("Email");
        contacts.setVisible(true);
        contacts.setSelectionMode(Grid.SelectionMode.SINGLE);
    }

}
