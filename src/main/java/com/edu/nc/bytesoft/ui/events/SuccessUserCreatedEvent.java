package com.edu.nc.bytesoft.ui.events;

import com.edu.nc.bytesoft.model.User;
import com.vaadin.ui.UI;
import org.springframework.context.ApplicationEvent;

public class SuccessUserCreatedEvent extends ApplicationEvent {

    private final User user;

    public SuccessUserCreatedEvent(UI source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public UI getSource() {
        return (UI) super.getSource();
    }
}
