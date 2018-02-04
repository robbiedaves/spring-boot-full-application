package com.robxx.springbootapp.services;

import com.robxx.springbootapp.domain.User;

public interface UserService extends CRUDService<User> {

    User findByUsername(String username);
}