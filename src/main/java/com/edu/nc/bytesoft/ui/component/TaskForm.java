package com.edu.nc.bytesoft.ui.component;

import com.edu.nc.bytesoft.ui.MainUI;
import com.vaadin.server.Responsive;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.security.VaadinSecurity;

@PrototypeScope
@SpringComponent
public class TaskForm extends HorizontalLayout {
    @Autowired
    ApplicationContext applicationContext;

    private final VaadinSecurity security;
    private final EventBus eventBus;

    @Autowired
    public TaskForm(VaadinSecurity security, EventBus.SessionEventBus eventBus) {
        this.security = security;
        this.eventBus = eventBus;
        initLayout();
    }

    private void initLayout() {
        setSizeFull();
        Component taskForm = taskForm();
        addComponent(taskForm);
        setComponentAlignment(taskForm, Alignment.MIDDLE_CENTER);
    }

    private Component taskForm() {
        final FormLayout taskForm = new FormLayout();
        taskForm.setSizeUndefined();
        taskForm.setSpacing(true);
        Responsive.makeResponsive(taskForm);
        taskForm.addComponent(buildFields());
        return taskForm;
    }

    private Component buildFields() {
        FormLayout fields = new FormLayout();
        ListSelect projects = new ListSelect("Project: ");
        projects.setRows(1);
        projects.setMultiSelect(false);
        projects.setNullSelectionAllowed(false);
        fields.addComponent(projects);
        /*Тут получаем список всех проектов и выбираем один
        * после выбора будет доступен список модулей этого проекта*/
        ListSelect modules = new ListSelect("Module: ");
        modules.setRows(1);
        modules.setMultiSelect(false);
        modules.setNullSelectionAllowed(false);
        fields.addComponent(modules);
        /*Выбираем модуль к которому относится таск или его родитель*/
        ListSelect allTasks = new ListSelect("Parent task: ");
        allTasks.setRows(1);
        allTasks.setMultiSelect(false);
        allTasks.setNullSelectionAllowed(true);/*таск может быть связан с другим, а может и не быть*/
        fields.addComponent(allTasks);

        fields.addComponent(new TextField("Assigned to: "));

        fields.addComponent(new DateField("Deadline date: "));
        fields.addComponent(new TextArea("Description: "));

        ListSelect priority = new ListSelect("Priority: ");
        priority.addItems("Critical");
        priority.addItems("Highest");
        priority.addItems("High");
        priority.addItems("Medium");
        priority.addItems("Low");
        priority.addItems("Lowest");
        priority.addItems("Future");
        priority.setRows(1);
        priority.setMultiSelect(false);
        priority.setNullSelectionAllowed(false);
        fields.addComponent(priority);

        Component button = new Button("Create task");
        button.setStyleName(ValoTheme.BUTTON_PRIMARY);
        fields.addComponent(button);
        button.addListener((Listener) event -> MainUI.getCurrent().setContent(applicationContext.getBean(MainUI.class)));
        return fields;
    }
}
