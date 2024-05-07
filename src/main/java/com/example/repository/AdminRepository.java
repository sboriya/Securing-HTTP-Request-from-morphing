package com.example.repository;



import com.example.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity,Integer> {
    AdminEntity findByEmail(String email);

    AdminEntity findByUserName(String userName);
}

