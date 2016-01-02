package com.edu.nc.bytesoft.dao;

import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.dao.impl.ObjectDaoReflect;
import com.edu.nc.bytesoft.model.NamedEntity;
import com.edu.nc.bytesoft.model.Task;
import com.edu.nc.bytesoft.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectDaoReflectTest {

    private final DataSource dataSource = new AutoconfiguratedDataSource();

    public ObjectDaoReflectTest() throws SQLException {
    }

    @Test
    public void inserTest() throws Exception {
        ObjectDao<User> userDao = new ObjectDaoReflect<>(User.class, dataSource.getConnection());
        User testUser1 = userDao.getById(22);
        testUser1.setId(null);
        User testUser2 = userDao.getById(userDao.save(testUser1).getId());
        System.out.println(testUser1);
        System.out.println(testUser2);
        assertThat(testUser1.equals(testUser2)).isTrue();
    }

    @Test
    public void test() throws Exception {
        ObjectDaoReflect<User> userDao = new ObjectDaoReflect<>(User.class, dataSource.getConnection());
        System.out.println(userDao.checkIfAttributeExist(32, 3, "OBJREFERENCE"));
    }

    @Test
    public void updateTest() throws Exception {
        ObjectDao<User> userDao = new ObjectDaoReflect<>(User.class, dataSource.getConnection());
        User testUser1 = userDao.getById(22);
        testUser1.setName(RandomStringUtils.randomAlphabetic(5));
        testUser1.getPhones().get(0).setName(RandomStringUtils.randomAlphabetic(5));
        testUser1.getPhones().add(new NamedEntity(RandomStringUtils.randomAlphabetic(5)));
        userDao.save(testUser1);
        User testUser2 = userDao.getById(22);

        System.out.println(testUser1);
        System.out.println(testUser2);
        assertThat(testUser1.equals(testUser2)).isTrue();
    }

    @Test(expected = NoSuchObjectException.class)
    public void deleteTest() throws Exception {
        ObjectDao<User> userDao = new ObjectDaoReflect<>(User.class, dataSource.getConnection());
        User testUser1 = userDao.getById(22);
        testUser1.setId(null);
        long newId;
        User testUser2 = userDao.getById(userDao.save(testUser1).getId());
        newId = testUser2.getId();
        boolean isDeleted = userDao.delete(testUser2);
        assertThat(isDeleted).isTrue();
        System.out.println(userDao.getById(newId));
    }

    @Test
    public void getUserTest() throws Exception {
        String user1Name = "Alexander Hunold";
        ObjectDao<User> userDao = new ObjectDaoReflect<>(User.class, dataSource.getConnection());
        User user1 = userDao.getById(24); //Alexander Hunold
        System.out.println(user1);
        assertThat(user1.getName()).isEqualTo(user1Name);
    }

    @Test
    public void getTaskTest() throws Exception {
        String taskName = "Task 2 Name. Project 1";
        ObjectDao<Task> taskDao = new ObjectDaoReflect<>(Task.class, dataSource.getConnection());
        Task task = taskDao.getById(78);
        System.out.println(task);
        assertThat(task.getName()).isEqualTo(taskName);
    }

    @Test
    public void lazyUsageExample() throws Exception {
        String taskName = "Task 2 Name. Project 1";
        ObjectDao<Task> taskDao = new ObjectDaoReflect<>(Task.class, dataSource.getConnection(), true);
        Task task = taskDao.getById(78);
        System.out.println(task);
        assertThat(task.getName()).isEqualTo(taskName);
    }


    @Test
    public void transactionalUsageExample() throws Exception {
        TransactionManager transactionManager = new TransactionManager();
        ObjectDao<Task> taskDao = new ObjectDaoReflect<>(Task.class, transactionManager);
        ObjectDao<User> userDao = new ObjectDaoReflect<>(User.class, transactionManager);

        try (TransactionManager.Transaction transaction = transactionManager.startTransaction()) {
            try {
                Task task = taskDao.getById(77);
                User user = userDao.getById(22);
                transaction.commit();
            } catch (SQLException | NoSuchObjectException e) {
                transaction.rollback();
            }
        }
    }


}