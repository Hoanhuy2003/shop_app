package com.project.shopapp.repositorys;

import com.project.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
      boolean existsByName(String name); // xem sp tên đó tồn tại hay không

    @Override
    Page<Product> findAll(Pageable pageable);// Phân trang
}
