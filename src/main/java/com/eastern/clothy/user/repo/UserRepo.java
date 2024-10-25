package com.eastern.clothy.user.repo;

import com.eastern.clothy.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<AppUser,Long> {
    public AppUser getUserByUsername(String username);
}
