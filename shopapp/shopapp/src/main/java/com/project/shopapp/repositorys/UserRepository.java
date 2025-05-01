package com.project.shopapp.repositorys;

import com.project.shopapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhoneNumber(String phoneNumber); // kiểm tra sđr
    Optional<User> findByPhoneNumber(String phoneNumber); // select * from users where phoneNumber ?
}
