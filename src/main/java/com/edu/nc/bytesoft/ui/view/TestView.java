package com.edu.nc.bytesoft.ui.view;

import com.edu.nc.bytesoft.ui.component.SideBar;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.annotation.PostConstruct;

@SpringView(name = TestView.NAME)
@SideBarItem(sectionId = SideBar.MENU, caption = "Test page", order = 1)
@FontAwesomeIcon(FontAwesome.BOOKMARK)
public class TestView extends VerticalLayout implements View {
    public static final String NAME = "test";
    Grid grid = new Grid();
    @PostConstruct
    void init() {
        Label header = new Label("Test page");
        addComponent(header);
        grid.addColumn("Name");
        addComponent(grid);
        TreeTable t = new TreeTable();
        t.addContainerProperty("Name", String.class, null);
        t.addItem(new Object[]{"GO"},null);
        addComponent(t);
        t.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick())    {
                    grid.addRow(String.valueOf(event.getItem()));
                }
            }
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
