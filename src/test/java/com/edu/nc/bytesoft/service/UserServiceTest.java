package com.edu.nc.bytesoft.service;

import com.edu.nc.bytesoft.Application;
import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.config.RegisterBeanFactoryInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = RegisterBeanFactoryInitializer.class)
@Component
public class UserServiceTest {

    private static final Log LOG = Log.get(UserServiceTest.class);

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void test() throws Exception {

    }

}