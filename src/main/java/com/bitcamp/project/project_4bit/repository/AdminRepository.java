package com.bitcamp.project.project_4bit.repository;

import com.bitcamp.project.project_4bit.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUser_UserId(Long userId);
}
