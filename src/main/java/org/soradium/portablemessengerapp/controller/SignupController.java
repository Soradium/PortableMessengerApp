package org.soradium.portablemessengerapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.soradium.portablemessengerapp.configurations.auth.AuthenticationProcess;
import org.soradium.portablemessengerapp.configurations.auth.UserCredentials;
import org.soradium.portablemessengerapp.configurations.jwt.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sec")
public class SignupController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextHolderStrategy
            securityContextHolderStrategy
            = SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContextRepository repository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationProcess process;
    private final PasswordEncoder encoder;

    public SignupController(
            @Autowired AuthenticationManager authenticationManager,
            @Autowired DelegatingSecurityContextRepository
                    delegatingSecurityContextRepository,
            @Autowired AuthenticationProcess process,
            @Autowired PasswordEncoder encoder,
            @Autowired UserDetailsService service) {
        this.authenticationManager = authenticationManager;
        this.repository = delegatingSecurityContextRepository;
        this.process = process;
        this.encoder = encoder;
        this.userDetailsService = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signUp(
            @RequestBody UserCredentials userCredentials,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response) {
        UserDetails newUser = User.builder()
                .username(userCredentials.username())
                .password(this.encoder.encode(
                        userCredentials.password()))
                .roles("USER")
                .build();
        JdbcUserDetailsManager service
                = (JdbcUserDetailsManager) userDetailsService;
        service.createUser(newUser);
        return process.doAuth(
                userCredentials,
                this.securityContextHolderStrategy,
                this.repository,
                this.authenticationManager,
                request,
                response);
    }

}
