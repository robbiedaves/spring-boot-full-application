package com.robxx.springbootapp.repositories;

import com.robxx.springbootapp.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer>{
    User findByUsername(String username);
}