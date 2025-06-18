package com.example.demo.repository.basicAuth;

import com.example.demo.data.entityUser.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);

}
