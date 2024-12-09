package com.competitionmathacademy.mathapi.repository;

import com.competitionmathacademy.mathapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>{
    Optional<Product> findByName(String name);
}
