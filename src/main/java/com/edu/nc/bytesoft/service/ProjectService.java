package com.edu.nc.bytesoft.service;


import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.dao.ObjectDao;
import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.dao.sql.Queries;
import com.edu.nc.bytesoft.model.Project;
import com.edu.nc.bytesoft.model.User;
import com.edu.nc.bytesoft.service.exception.NotUniqueEmailException;
import com.edu.nc.bytesoft.service.exception.NotUniqueLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
@Service
public class ProjectService {

    private static final Log LOG = Log.get(ProjectService.class);

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectDao<Project> projectDao;
/*
    private static final String QUERY_GET_PROJECTS_BY_USERNAME = "SELECT OBJECT_ID\n" +
            "FROM ATTRIBUTES\n" +
            "WHERE ATTR_ID = 1\n" +
            "AND VALUE = ? \n" +
            "AND ROWNUM = 1";
*/
    private static final String QUERY_GET_NAMES_PROJECT_BY_USERID = "SELECT A.VALUE\n" +
            "FROM ATTRIBUTES A\n" +
            "WHERE A.ATTR_ID = 7 AND A.OBJECT_ID IN(\n"+
            "SELECT OBJECTS.OBJECT_ID\n"+
            "FROM OBJECTS JOIN ATTRIBUTES ON OBJECTS.OBJECT_ID = ATTRIBUTES.OBJECT_ID\n"+
            "WHERE OBJECTS.OBJECT_TYPE_ID = 3 AND ATTRIBUTES.ATTR_ID = 8\n"+
            "AND ATTRIBUTES.VALUE = ?)";

    public Project save(Project project) throws NoSuchObjectException, SQLException, NotUniqueLoginException {
        if (!isProjectnameUnique(project.getName()))
        throw new NotUniqueLoginException();

        return projectDao.save(project);
    }

    public boolean isProjectnameUnique(String prjname) throws SQLException {
        Long proId = getPrjIdByProjectname(prjname);
        return proId == null;
    }
    public Long getPrjIdByProjectname(String prjname) throws SQLException {
   //     return projectDao.getSqlExecutor().execute(QUERY_GET_USER_ID_BY_USERNAME, Long.class, username);
        return null;
    }
    public String[] getAllProjectsName(long id) throws SQLException {
        return projectDao.getSqlExecutor().execute(QUERY_GET_NAMES_PROJECT_BY_USERID, String[].class, id);
    }
}
