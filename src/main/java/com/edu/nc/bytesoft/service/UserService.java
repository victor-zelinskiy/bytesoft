package com.edu.nc.bytesoft.service;

import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.dao.ObjectDao;
import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.dao.sql.Queries;
import com.edu.nc.bytesoft.model.User;
import com.edu.nc.bytesoft.service.exception.NotUniqueEmailException;
import com.edu.nc.bytesoft.service.exception.NotUniqueLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Objects;

@Service
public class UserService implements UserDetailsService {

    private static final Log LOG = Log.get(UserService.class);

    @Autowired
    private ObjectDao<User> userDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    private static final String QUERY_GET_USER_ID_BY_USERNAME = "SELECT OBJECT_ID\n" +
            "FROM ATTRIBUTES\n" +
            "WHERE ATTR_ID = 1\n" +
            "AND VALUE = ? \n" +
            "AND ROWNUM = 1";

    private static final String QUERY_GET_USER_ID_BY_EMAIL = "SELECT OBJECT_ID\n" +
            "FROM ATTRIBUTES\n" +
            "WHERE ATTR_ID = 66\n" +
            "AND VALUE = ?\n" +
            "AND ROWNUM = 1";

    private static final String QUERY_GET_USERNAME_BY_USER_ID = "SELECT VALUE\n" +
            "FROM ATTRIBUTES\n" +
            "WHERE OBJECT_ID = ?\n" +
            "AND ATTR_ID = 1";

    private static final String QUERY_GET_EMAIL_BY_USER_ID = "SELECT VALUE\n" +
            "FROM ATTRIBUTES\n" +
            "WHERE OBJECT_ID = ?\n" +
            "AND ATTR_ID = 66";


    public User save(User user) throws NoSuchObjectException, SQLException, NotUniqueLoginException, NotUniqueEmailException {
        if (!isUserValidUsername(user)) {
            throw new NotUniqueLoginException();
        }
        if (!isUserValidEmail(user)) {
            throw new NotUniqueEmailException();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.save(user);
    }

    public boolean isUsernameUnique(String username) throws SQLException {
        Long userId = getUserIdByUsername(username);
        return userId == null;
    }

    public boolean isEmailUnique(String email) throws SQLException {
        Long userId = getUserIdByEmail(email);
        return userId == null;
    }


    public boolean isUserValidUsername(User user) throws SQLException {
        if (!isUsernameUnique(user.getUsername())) {
            if (user.isNew()) {
                return false;
            } else {
                String oldUserName = getUsernameById(user.getId());
                if (!Objects.equals(user.getUsername(), oldUserName)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isUserValidEmail(User user) throws SQLException {
        if (!isEmailUnique(user.getEmail())) {
            if (user.isNew()) {
                return false;
            } else {
                String oldUserEmail = getEmailById(user.getId());
                if (!Objects.equals(user.getEmail(), oldUserEmail)) {
                    return false;
                }
            }
        }
        return true;
    }


    public Long getUserIdByUsername(String username) throws SQLException {
        return userDao.getSqlExecutor().execute(QUERY_GET_USER_ID_BY_USERNAME, Long.class, username);
    }

    public Long getUserIdByEmail(String email) throws SQLException {
        return userDao.getSqlExecutor().execute(QUERY_GET_USER_ID_BY_EMAIL, Long.class, email);
    }

    public String getUsernameById(long id) throws SQLException {
        return userDao.getSqlExecutor().execute(QUERY_GET_USERNAME_BY_USER_ID, String.class, id);
    }

    public String getEmailById(long id) throws SQLException {
        return userDao.getSqlExecutor().execute(QUERY_GET_EMAIL_BY_USER_ID, String.class, id);
    }

    public User getUserByUsername(String username) throws SQLException, NoSuchObjectException {
        Long userId = getUserIdByUsername(username);
        if (userId == null) {
            throw new NoSuchObjectException();
        }
        return userDao.getById(userId);
    }

    public User getUserById(long id) throws NoSuchObjectException, SQLException {
        return userDao.getById(id);
    }

    public long[] getAllUsersIds() throws SQLException {
        return userDao.getSqlExecutor().execute(Queries.QUERY_GET_OBJECT_IDS_BY_TYPE_CODE, long[].class, User.TYPE_CODE);
    }

    public void deleteAllUsers() throws NoSuchObjectException, SQLException {
        for (long id : getAllUsersIds()) {
            userDao.delete(id);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return getUserByUsername(username);
        } catch (SQLException | NoSuchObjectException e) {
            throw new UsernameNotFoundException(e.getMessage(),e);
        }
    }
}
