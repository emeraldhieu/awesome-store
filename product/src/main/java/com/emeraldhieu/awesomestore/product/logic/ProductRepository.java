package com.emeraldhieu.awesomestore.product.logic;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends ListCrudRepository<Product, Long>,
    PagingAndSortingRepository<Product, Long>,
    JpaSpecificationExecutor<Product> {

    Optional<Product> findByExternalId(String externalId);

    void deleteByExternalId(String externalId);
}
