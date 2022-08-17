package com.example.bms.repository;

import com.example.bms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String roleName);
}
