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
import java.util.ArrayList;
import java.util.List;

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
    private static final String QUERY_GET_NAMES_PROJECT_BY_USERID = "SELECT A.VALUE\n"+
            "FROM ATTRIBUTES A  WHERE A.ATTR_ID = 7 AND A.OBJECT_ID IN\n"+
            "(SELECT OBJECT_ID FROM OBJREFERENCE OBJ WHERE OBJ.REFERENCE = ?)";

    private static final String QUERY_GET_IDS_PROJECT_BY_USERID = "SELECT OBJECT_ID\n"+
           "FROM OBJREFERENCE OBJ WHERE OBJ.ATTR_ID = 8 AND OBJ.REFERENCE = ?";

    private static final String QUERY_GET_IDS_PROJECT_BY_PMID = "SELECT OBJECT_ID\n"+
            "FROM OBJREFERENCE OBJ WHERE OBJ.ATTR_ID = 17 AND OBJ.REFERENCE = ?";

    private static final String QUERY_GET_PROJECT_STATUS_BY_PRJID = "SELECT ST.NAME\n"+
            "FROM OBJECTS ST\n"+
            "WHERE ST.OBJECT_ID =\n" +
            "(SELECT REFERENCE FROM OBJREFERENCE WHERE ATTR_ID = 11 AND OBJECT_ID = ?)";

    private static final String QUERY_GET_PROJECT_NAME_BY_PRJID ="SELECT A.VALUE\n"+
             "FROM ATTRIBUTES A WHERE A.ATTR_ID = 7 AND A.OBJECT_ID =?";

    private static final String QUERY_GET_PROJECT_ID_BY_PRJNAME ="SELECT A.OBJECT_ID\n"+
            "FROM ATTRIBUTES A WHERE A.ATTR_ID = 7 AND A.VALUE = ?";

    public Project save(Project project) throws NoSuchObjectException, SQLException, NotUniqueLoginException {
        if (!isProjectnameUnique(project.getName()))
        throw new NotUniqueLoginException();

        return projectDao.save(project);
    }
    public Project getById(long id) throws NoSuchObjectException, SQLException, NotUniqueLoginException {
        return projectDao.getById(id);
    }

    public boolean isProjectnameUnique(String prjname) throws SQLException {
        Long proId = getProjectIdByName(prjname);
        return proId == null;
    }
    public long[] getAllProjectsIds(long id) throws SQLException {
        return projectDao.getSqlExecutor().execute(QUERY_GET_IDS_PROJECT_BY_USERID, long[].class, id);
    }
    public String getProjectStatusById(long id) throws SQLException {
        return projectDao.getSqlExecutor().execute(QUERY_GET_PROJECT_STATUS_BY_PRJID, String.class, id);
    }
    public String getProjectNameById(long id) throws SQLException {
        return projectDao.getSqlExecutor().execute(QUERY_GET_PROJECT_NAME_BY_PRJID, String.class, id);
    }
    public Long getProjectIdByName(String name) throws SQLException {
        return projectDao.getSqlExecutor().execute(QUERY_GET_PROJECT_ID_BY_PRJNAME, Long.class, name);
    }

    public Long getProjectNameByPM(String name) throws SQLException {
        return projectDao.getSqlExecutor().execute(QUERY_GET_IDS_PROJECT_BY_PMID, Long.class, name);
    }
    public List<Project> getAllProjectsByUser(long userId) throws SQLException {
        List<Project> result = new ArrayList<>();
        for (long id : getAllProjectsIds(userId)) {
            try {
                result.add(projectDao.getById(id));
            } catch (NoSuchObjectException e) {
                throw new SQLException( e );
            }
        }
        return result;
    }
    public boolean delete(long id) throws NoSuchObjectException, SQLException
    {
        return projectDao.delete(id);
    }

}
