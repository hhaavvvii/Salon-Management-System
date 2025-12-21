package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.UserDao;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;

import java.util.Optional;

public class UserDaoImpl implements UserDao {

    @Override
    public Optional<User> findByLogin(String login) {

        // TEST ADMIN
        if ("admin".equals(login)) {
            User admin = new User();
            admin.setId(1L);
            admin.setUsername("admin");
            admin.setPasswordHash("admin"); // временно без хеша
            admin.setRole(Role.ADMIN);
            admin.setEmployeeId(null);

            return Optional.of(admin);
        }

        // TEST MASTER
        if ("master".equals(login)) {
            User master = new User();
            master.setId(2L);
            master.setUsername("master");
            master.setPasswordHash("master"); // временно без хеша
            master.setRole(Role.MASTER);
            master.setEmployeeId(1001L);

            return Optional.of(master);
        }

        return Optional.empty();
    }
}
