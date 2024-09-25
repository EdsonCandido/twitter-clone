package com.edson.apispring.config;

import com.edson.apispring.entitis.Role;
import com.edson.apispring.entitis.User;
import com.edson.apispring.repository.RoleRepository;
import com.edson.apispring.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        var roleAdmin = this.roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = this.userRepository.findByLogin("admin");

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("usuário ADMIN já cadastrado");
                },
                () -> {
                    var newUser = new User();
                    newUser.setLogin("admin");
                    newUser.setUsername("Administrador");
                    newUser.setPassword(passwordEncoder.encode("admin"));
                    newUser.setRoles(Set.of(roleAdmin));

                    this.userRepository.save(newUser);
                }
        );


    }
}
