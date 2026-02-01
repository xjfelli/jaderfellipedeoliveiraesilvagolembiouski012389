package com.gerenciadorartistas.backend.features.auth.service;

import com.gerenciadorartistas.backend.features.user.entity.UserEntity;
import com.gerenciadorartistas.backend.features.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
        UserEntity user = userRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException(
                    "Usuário não encontrado: " + username
                )
            );

        return user;
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        UserEntity user = userRepository
            .findById(id)
            .orElseThrow(() ->
                new UsernameNotFoundException(
                    "Usuário não encontrado com id: " + id
                )
            );

        return user;
    }
}
