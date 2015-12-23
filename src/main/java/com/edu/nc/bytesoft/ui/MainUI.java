package com.edu.nc.bytesoft.ui;

import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.ui.component.LoginForm;
import com.edu.nc.bytesoft.ui.component.MainForm;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.security.util.SuccessfulLoginEvent;

@Theme("valo")
@SpringUI()
@Title("ByteSoft projects manager")
public class MainUI extends UI {

    private static final Log LOG = Log.get(MainUI.class);

    @Autowired
    private SpringViewProvider viewProvider;


    @Autowired
    private VaadinSecurity vaadinSecurity;

    @Autowired
    private EventBus.SessionEventBus eventBus;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    protected void init(VaadinRequest request) {
        if (vaadinSecurity.isAuthenticated()) {
            showMainForm();
        } else {
            showLoginForm();
        }
    }

    @Override
    public void attach() {
        super.attach();
        eventBus.subscribe(this);
    }

    @Override
    public void detach() {
        eventBus.unsubscribe(this);
        super.detach();
    }

    @EventBusListenerMethod
    void onLogin(SuccessfulLoginEvent loginEvent) {
        if (loginEvent.getSource().equals(this)) {
            LOG.debug("  !  " + loginEvent.getAuthentication().getAuthorities().toString());
            access(this::showMainForm);
        } else {
            getPage().reload();
        }
    }

    private void showLoginForm() {
        setContent(applicationContext.getBean(LoginForm.class));
    }

    private void showMainForm() {
        setContent(applicationContext.getBean(MainForm.class));
    }
}
