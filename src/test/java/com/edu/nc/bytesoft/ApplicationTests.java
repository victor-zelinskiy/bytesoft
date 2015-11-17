package com.edu.nc.bytesoft;

import com.edu.nc.bytesoft.ui.MainUI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

    private static final Log LOG = Log.get(MainUI.class);

    private static final String sqlProjectName = "select attr.value " +
            "from attributes attr, objects obj " +
            "where obj.object_id = attr.object_id " +
            " and obj.OBJECT_TYPE_ID = 3 and attr.attr_id = 7";

    @Test
    public void dbConnectTest() throws SQLException {
        LOG.debug("dbConnectTest");
        String[] expectedProjectNames = new String[]{"Project 1 Name", "Project 2 Name", "Project UMC 1 Name"};
        List<String> result = new ArrayList<>();
        try (Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "bytesoft_db", "2430");
             Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlProjectName)) {
            while (resultSet.next()) {
                String projectName = resultSet.getString(1);
                result.add(projectName);
            }
            LOG.debug(result.toString());
        }

        assertThat(result.toArray()).isEqualTo(expectedProjectNames);
    }
}
