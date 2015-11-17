package com.edu.nc.bytesoft.ui.view;

import com.edu.nc.bytesoft.ui.component.SideBar;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.annotation.PostConstruct;

@SpringView(name = HomeView.NAME)
@SideBarItem(sectionId = SideBar.MENU, caption = "Home page", order = 0)
@FontAwesomeIcon(FontAwesome.HOME)
public class HomeView extends VerticalLayout implements View {
    public static final String NAME = "home";

    @PostConstruct
    void init() {
        setSpacing(true);
        setMargin(true);
        Label header = new Label("Home page");
        header.addStyleName(ValoTheme.LABEL_H2);
        addComponent(header);
        addComponent(new Button());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
