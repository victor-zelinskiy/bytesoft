package com.edu.nc.bytesoft.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;

@SpringView(name= MainView.NAME)
public class MainView extends VerticalLayout implements View {
    public static final String NAME = "main";

    @PostConstruct
    void init() {
        setMargin(true);
        setSpacing(true);
        addComponent(new Label("Main view"));
        Button btnTest = new Button("Test");
        btnTest.addClickListener(event -> Notification.show("hello"));
        addComponent(btnTest);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
