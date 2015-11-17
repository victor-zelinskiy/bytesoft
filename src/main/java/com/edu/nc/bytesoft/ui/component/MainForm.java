package com.edu.nc.bytesoft.ui.component;

import com.edu.nc.bytesoft.LoggerWrapper;
import com.edu.nc.bytesoft.ui.view.HomeView;
import com.vaadin.navigator.Navigator;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.sidebar.components.ValoSideBar;
import org.vaadin.spring.sidebar.security.VaadinSecurityItemFilter;

@UIScope
@SpringComponent
public class MainForm extends CustomComponent {

    private static final LoggerWrapper LOG = LoggerWrapper.get(MainForm.class);

    @Autowired
    public MainForm(final VaadinSecurity vaadinSecurity, SpringViewProvider springViewProvider, ValoSideBar sideBar) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        setCompositionRoot(layout);
        setSizeFull();

        sideBar.setItemFilter(new VaadinSecurityItemFilter(vaadinSecurity));
        layout.addComponent(sideBar);

        CssLayout viewContainer = new CssLayout();
        viewContainer.setSizeFull();
        layout.addComponent(viewContainer);
        layout.setExpandRatio(viewContainer, 1f);


        Navigator navigator = new Navigator(UI.getCurrent(), viewContainer);
        navigator.addProvider(springViewProvider);
        navigator.navigateTo(HomeView.NAME);
    }
}
