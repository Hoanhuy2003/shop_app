package com.project.shopapp.repositorys;

import com.project.shopapp.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query("SELECT o FROM Order o " +
            "WHERE (:keyword IS NULL OR :keyword = '' " +
            "OR o.fullName LIKE CONCAT('%', :keyword, '%') " +
            "OR o.address LIKE CONCAT('%', :keyword, '%') " +
            "OR o.note LIKE CONCAT('%', :keyword, '%') " +
            "OR o.email LIKE CONCAT('%', :keyword, '%'))")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


}

