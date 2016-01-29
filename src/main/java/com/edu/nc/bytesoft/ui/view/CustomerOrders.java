package com.edu.nc.bytesoft.ui.view;

import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.model.Project;
import com.edu.nc.bytesoft.model.User;
import com.edu.nc.bytesoft.service.ProjectService;
import com.edu.nc.bytesoft.service.exception.NotUniqueLoginException;
import com.edu.nc.bytesoft.ui.component.SideBar;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Secured("ROLE_CUSTOMER")
@SpringView(name = CustomerOrders.NAME)
@SideBarItem(sectionId = SideBar.MENU, caption = "Orders page", order = 6)
@FontAwesomeIcon(FontAwesome.DOLLAR)

public class CustomerOrders extends AbsoluteLayout implements View {

    public static final String NAME = "customerOrders";

    @Autowired
    private ProjectService projectService;

    private AbsoluteLayout orderForm = new AbsoluteLayout();
    private TextField projectName = new TextField("Project name:");
    private TextArea  projectDescription = new TextArea("Description:");
    private TextField projectStatus = new TextField("Status:");
    private TextField projectPrice = new TextField("Price:");
    private TextField creationDate = new TextField("Date:");

    private TreeTable prjtable = new TreeTable();
    Grid grid = new Grid();
    User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    long[] projectIds = new long[]{};

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
    @PostConstruct
    void init() {

        Label header = new Label("Orders");
        header.addStyleName(ValoTheme.LABEL_H2);
        addComponent(header,"left: 20px;");
        tableOfProjects();
        grid.addColumn("Name");
        addComponent(prjtable,"left: 20px; top: 100px;");
        Component order = addOrderForm();
        addComponent(order,"left: "+(prjtable.getWidth()+50)+"px;");
        prjtable.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick())    {
                    completeOrderForm(String.valueOf(event.getItem()));
                    orderForm.setVisible(true);
                }
            }
        });
    }
    public void tableOfProjects()  {

        getIdsProjects();
        prjtable.addContainerProperty("Order name",String.class,null);
        Object[]item1;
        if(projectIds!=null) {
            String[] projectsName = new String[projectIds.length];

            for (int i = 0; i < projectIds.length; i++) {
                try {
                    projectsName[i] = projectService.getProjectNameById(projectIds[i]);
                } catch (SQLException e) {
                    Notification.show("Project not found", e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
                item1 = new Object[]{String.valueOf(projectsName[i])};
                prjtable.addItem(item1, i);
                prjtable.setChildrenAllowed(i,false);
            }
        }
        prjtable.setWidth("400px");
    }
    public void getIdsProjects()
    {
        try {
            projectIds = projectService.getAllProjectsIds(currentUser.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private Component addOrderForm() {
        orderForm.addComponent(projectName,"left: 20px; top: 100px;");

        projectDescription.setWidth("650px");
        orderForm.addComponent(projectDescription,"left: 20px; top: 175px;");

        orderForm.addComponent(projectStatus,"left: 250px; top: 100px;");
        orderForm.addComponent(creationDate,"left: 485px; top: 100px;");
        orderForm.addComponent(projectPrice,"left: 20px; top:"+(projectDescription.getHeight()+335)+"px;");

        orderForm.setVisible(false);
        return orderForm;
    }
    private void completeOrderForm(String prName)
    {
        Long id = null;
        try {
            id = projectService.getProjectIdByName(prName);
            Project project = projectService.getById(id);
            projectDescription.setValue(project.getDescription());
            projectName.setValue(project.getName());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy hh:mm:ss", Locale.ENGLISH);
            String strDate = simpleDateFormat.format(project.getCreatedDate());
            creationDate.setValue(strDate);
            projectPrice.setValue(String.valueOf(project.getPrice()));
            projectStatus.setValue(String.valueOf(project.getStatus()));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        } catch (NotUniqueLoginException e) {
            e.printStackTrace();
        }
    }
}
