package com.robxx.springbootapp.repositories;

import com.robxx.springbootapp.domain.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Integer>{
}
