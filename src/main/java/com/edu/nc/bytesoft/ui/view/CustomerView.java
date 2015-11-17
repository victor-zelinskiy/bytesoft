package com.edu.nc.bytesoft.ui.view;

import com.edu.nc.bytesoft.ui.component.SideBar;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.annotation.PostConstruct;

@Secured("ROLE_CUSTOMER")
@SpringView(name = CustomerView.NAME)
@SideBarItem(sectionId = SideBar.MENU, caption = "Customer page", order = 4)
@FontAwesomeIcon(FontAwesome.BANK)
public class CustomerView extends VerticalLayout implements View {
    public static final String NAME = "customer";

    @PostConstruct
    void init() {
        setSpacing(true);
        setMargin(true);
        Label header = new Label("Customer page");
        header.addStyleName(ValoTheme.LABEL_H2);
        addComponent(header);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
