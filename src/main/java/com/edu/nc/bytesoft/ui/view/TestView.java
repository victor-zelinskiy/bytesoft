package com.edu.nc.bytesoft.ui.view;

import com.edu.nc.bytesoft.ui.component.SideBar;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.annotation.PostConstruct;

@SpringView(name = TestView.NAME)
@SideBarItem(sectionId = SideBar.MENU, caption = "Test page", order = 1)
@FontAwesomeIcon(FontAwesome.BOOKMARK)
public class TestView extends VerticalLayout implements View {
    public static final String NAME = "test";

    @PostConstruct
    void init() {
        Label header = new Label("Test page");
        addComponent(header);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
