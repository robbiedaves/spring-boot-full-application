package com.robxx.springbootapp.repositories;

import com.robxx.springbootapp.domain.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}
