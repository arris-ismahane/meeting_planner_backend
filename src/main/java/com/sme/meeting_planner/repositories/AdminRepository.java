package com.sme.meeting_planner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sme.meeting_planner.model.auth.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findOneByUsername(String username);
}