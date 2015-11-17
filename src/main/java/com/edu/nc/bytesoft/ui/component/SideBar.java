package com.edu.nc.bytesoft.ui.component;

import org.springframework.stereotype.Component;
import org.vaadin.spring.sidebar.annotation.SideBarSection;
import org.vaadin.spring.sidebar.annotation.SideBarSections;


@Component
@SideBarSections({
        @SideBarSection(id = SideBar.MENU, caption = "Menu"),
        @SideBarSection(id = SideBar.OPERATIONS, caption = "")
})
public class SideBar {

    public static final String MENU = "menu";
    public static final String OPERATIONS = "operations";
}
