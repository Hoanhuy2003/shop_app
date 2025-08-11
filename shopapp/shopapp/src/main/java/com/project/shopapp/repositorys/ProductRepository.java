package com.project.shopapp.repositorys;

import com.project.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
      boolean existsByName(String name); // xem sp tên đó tồn tại hay không

    @Override
    Page<Product> findAll(Pageable pageable);// Phân trang

    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId) " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR p.name LIKE CONCAT('%', :keyword, '%') " +
            "OR p.description LIKE CONCAT('%', :keyword, '%'))")
    Page<Product> searchProducts(@Param("categoryId") Long categoryId,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

}
