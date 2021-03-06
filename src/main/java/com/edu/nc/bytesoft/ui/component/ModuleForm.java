package com.edu.nc.bytesoft.ui.component;

/*import com.edu.nc.bytesoft.ui.MainUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;*/
import com.edu.nc.bytesoft.ui.MainUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.security.VaadinSecurity;

import java.util.Arrays;
import java.util.List;

import static com.vaadin.server.FontAwesome.*;

@PrototypeScope
@SpringComponent
public class ModuleForm extends HorizontalLayout{
    @Autowired
    MainUI mainUI;

    private final VaadinSecurity vaadinSecurity;
    private final EventBus.SessionEventBus eventBus;

    TextField name =new TextField("Name:");
    DateField creationDate = new DateField("Creation date:");
    DateField deadlineDate = new DateField("Deadline date:");
    DateField complDate = new DateField("Completion date:");
    TextArea description = new TextArea("Description:");

    @Autowired
    public ModuleForm(VaadinSecurity vaadinSecurity, EventBus.SessionEventBus eventBus) {
        this.vaadinSecurity = vaadinSecurity;
        this.eventBus = eventBus;
        initLayout();
    }

    private void initLayout() {
        setSizeFull();
        Component moduleForm = moduleForm();
        addComponent(moduleForm);
        setComponentAlignment(moduleForm, Alignment.TOP_LEFT);

    }
    private Component moduleForm() {
        final FormLayout mdlPanel = new FormLayout();
        // mdlPanel.setSizeUndefined();
        // mdlPanel.setSpacing(true);
        Responsive.makeResponsive(mdlPanel);
        mdlPanel.addComponent(buildFields());
        return mdlPanel;
    }
    private Component buildFields() {

        FormLayout fields = new FormLayout();

        fields.addComponent(name);
        fields.addComponent(creationDate);
        fields.addComponent(deadlineDate);
        fields.addComponent(complDate);
        fields.addComponent(description);
/*
        ListSelect status = new ListSelect("Status");
        status.setWidth(180, Sizeable.UNITS_PIXELS);
        status.addItems("Active", "Resolved", "Closed");
        status.setMultiSelect(false);
        status.setNewItemsAllowed(true);
        status.setNullSelectionAllowed(false);
        status.setRows(3);
        fields.addComponent(status);
*/
        ListSelect teamleads = new ListSelect("Teamlead");
        teamleads.setWidth(180, Sizeable.UNITS_PIXELS);
        teamleads.addItems("");
        teamleads.setMultiSelect(false);
        teamleads.setNullSelectionAllowed(false);
        teamleads.setRows(1);
        fields.addComponent(teamleads);

        ListSelect priority = new ListSelect("Priority");
        priority.setWidth(180, Sizeable.UNITS_PIXELS);
        priority.addItems("1", "2", "3", "4", "5");
        priority.setMultiSelect(false);
        priority.setNullSelectionAllowed(false);
        priority.setRows(1);
        fields.addComponent(priority);

        Component buttonCreate = new Button("Create module");
        buttonCreate.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonCreate.setWidth(180, Sizeable.UNITS_PIXELS);
        fields.addComponent(buttonCreate);
        buttonCreate.addListener((Listener) event -> MainUI.getCurrent().setContent(mainUI));

        return fields;
    }

}
