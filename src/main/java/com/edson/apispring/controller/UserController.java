package com.edson.apispring.controller;

import com.edson.apispring.controller.dto.CreateUserDto;
import com.edson.apispring.entitis.Role;
import com.edson.apispring.entitis.User;
import com.edson.apispring.repository.RoleRepository;
import com.edson.apispring.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @Transactional
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDto dto) {

        var basicRole = this.roleRepository.findByName(Role.Values.BASIC.name());

        var userExist = this.userRepository.findByLogin(dto.login());

        if (userExist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
//            throw new BadCredentialsException("Login exist!");
        }

        var newUser = new User();
        newUser.setUsername(dto.username());
        newUser.setLogin(dto.login());
        newUser.setPassword(passwordEncoder.encode(dto.password()));
        newUser.setRoles(Set.of(basicRole));

        this.userRepository.save(newUser);


        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listUsers(){
        var users = this.userRepository.findAll();
        return ResponseEntity.ok(users);
    }

}
