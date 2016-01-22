package com.edu.nc.bytesoft.ui.view;

import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.model.Project;
import com.edu.nc.bytesoft.model.User;
import com.edu.nc.bytesoft.service.ProjectService;
import com.edu.nc.bytesoft.service.UserService;
import com.edu.nc.bytesoft.service.exception.NotUniqueEmailException;
import com.edu.nc.bytesoft.service.exception.NotUniqueLoginException;
import com.edu.nc.bytesoft.ui.component.SideBar;
import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

@Secured("ROLE_CUSTOMER")
@SpringView(name = CustomerView.NAME)
@SideBarItem(sectionId = SideBar.MENU, caption = "Customer page", order = 4)
@FontAwesomeIcon(FontAwesome.BANK)
public class CustomerView extends AbsoluteLayout implements View,Upload.Receiver, Upload.SucceededListener,Upload.FailedListener {

    public static final String NAME = "customer";


    @Autowired
    private ProjectService projectService;

    Button addOrder = new Button("+Add order", this::addOrder);

    private AbsoluteLayout addProjectForm = new AbsoluteLayout();

    private TextField projectName = new TextField("Project name:");
    private TextArea  projectDescription = new TextArea("Description:");
    ListSelect contactList = new ListSelect("Contacts ");
    Button addDocument = new Button("+Add document", this::addDocument);
    Button createOrder = new Button("Create order",event -> saveOrder());
    private Grid grid = new Grid();

    File file;

    String[] projects = new String[]{};
    @PostConstruct
    void init() {
        Label header = new Label("Customer page");

        addComponent(addOrder,"left: 20px;top: 35px;");
        Component tableOfProjects =tableOfProjects();
        addComponent(tableOfProjects,"left: 20px; top: 100px;");

        Component addProject = addProjectForm();
        addComponent(addProject,"left: "+(tableOfProjects.getWidth()+50)+"px;");

    }
    private Component getUpload()
    {
        final Upload upload = new Upload("Upload the file here", this);
        Locale locale = Locale.CANADA;
        upload.setLocale(locale);
        // Listen for events regarding the success of upload.
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);
        return upload;
    }
    private Component addProjectForm() {
        addProjectForm.addComponent(projectName,"left: 20px; top: 100px;");

        projectDescription.setWidth("650px");
        addProjectForm.addComponent(projectDescription,"left: 20px; top: 175px;");

        addProjectForm.addComponent(getUpload(),"left: 270px; top: 100px;");

        createContactGrid();
        addProjectForm.addComponent(grid,"left:  270px; top: 335px;");

        contactList.setWidth("200px");
        addProjectForm.addComponent(contactList,"left: 20px; top: 335px;");

        createOrder.addStyleName(ValoTheme.BUTTON_PRIMARY);
        createOrder.setWidth("200px");
        addProjectForm.addComponent(createOrder,"left: 20px; top: 570px;");

        addProjectForm.setVisible(false);
        return addProjectForm;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
    public void addOrder(Button.ClickEvent event)
    {
        addProjectForm.setVisible(true);
    }
    public void addDocument(Button.ClickEvent event)
    {
        getUpload();
    }
    public User getCurrentUser()
    {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser;
    }
    public void saveOrder()
    {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Project newProject = new Project();
        newProject.setName(projectName.getValue());
        newProject.setAuthor(currentUser);
        newProject.setDescription(projectDescription.getValue());
        newProject.setCreatedDate(new Date());
        try {
            projectService.save(newProject);
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NotUniqueLoginException e) {
            e.printStackTrace();
        }

    }
    public Component tableOfProjects()
    {
        // getProjects();
        TreeTable ttable = new TreeTable();
        ttable.addContainerProperty("Order name",String.class,null);
        ttable.addContainerProperty("Status",String.class,null);

        ttable.addItem(new Object[]{"",null}, 0);
        Object[] item1 =new Object[]{"","Active"};
        ttable.addItem(item1, 3);
        ttable.setChildrenAllowed(3,false);
        ttable.setWidth("400px");
        ttable.setParent(3, 0);
        return ttable;
    }
    private void createContactGrid() {
        grid.setWidth("400px");
        grid.setHeight("210px");
        grid.addColumn("Name");
        grid.addColumn("Phone");
        grid.setVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    }
    public OutputStream receiveUpload(String filename,
                                      String MIMEType) {
        FileOutputStream fos = null; // Output stream to write to
        file = new File("/Downloads/" + filename);
        try {
            // Open the file for writing.
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            // Error while opening the file. Not reported here.
            e.printStackTrace();
            return null;
        }

        return fos; // Return the output stream to write to
    }
    public void getProjects()
    {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            projects = projectService.getAllProjectsName(currentUser.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // return projects;
    }
    @Override
    public void uploadFailed(Upload.FailedEvent event) {

    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent event) {

    }
}
