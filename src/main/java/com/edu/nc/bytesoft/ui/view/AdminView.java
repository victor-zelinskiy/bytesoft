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

@Secured("ROLE_ADMIN")
@SpringView(name = AdminView.NAME)
@SideBarItem(sectionId = SideBar.MENU, caption = "Admin page", order = 2)
@FontAwesomeIcon(FontAwesome.COGS)
public class AdminView extends VerticalLayout implements View {
    public static final String NAME = "admin";

    @PostConstruct
    void init() {
        setSpacing(true);
        setMargin(true);
        Label header = new Label("Admin page");
        header.addStyleName(ValoTheme.LABEL_H2);
        addComponent(header);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
