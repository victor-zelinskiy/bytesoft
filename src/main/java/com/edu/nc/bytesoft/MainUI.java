package com.edu.nc.bytesoft;

import com.edu.nc.bytesoft.view.MainView;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;

@Theme("valo")
@SpringUI()
public class MainUI extends UI {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    SpringViewProvider viewProvider;

    @Override
    protected void init(VaadinRequest request) {
        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);

        navigator.navigateTo(MainView.NAME);
    }
}
