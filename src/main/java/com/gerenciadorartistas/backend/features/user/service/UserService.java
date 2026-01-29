package com.gerenciadorartistas.backend.features.user.service;

import com.gerenciadorartistas.backend.features.user.dto.UserCreateDTO;
import com.gerenciadorartistas.backend.features.user.dto.UserPresenterDTO;
import com.gerenciadorartistas.backend.features.user.entity.UserEntity;
import com.gerenciadorartistas.backend.features.user.mapper.UserMapper;
import com.gerenciadorartistas.backend.features.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserPresenterDTO> findAll() {
        return userRepository
            .findAll()
            .stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    }

    public Page<UserPresenterDTO> findAllPaginated(Pageable pageable) {
        Page<UserEntity> usersList = userRepository.findAll(pageable);

        return new PageImpl<>(
            usersList
                .getContent()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList()),
            usersList.getPageable(),
            usersList.getTotalElements()
        );
    }

    public UserPresenterDTO findById(Long id) {
        UserEntity usuario = userRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("Usuário não encontrado com id: " + id)
            );

        return userMapper.toDto(usuario);
    }

    @Transactional
    public UserPresenterDTO create(UserCreateDTO usuarioDTO) {
        if (userRepository.existsByUsername(usuarioDTO.getUsername())) {
            log.warn("Username já em uso.", usuarioDTO.getUsername());

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Dados inválidos ou inexistentes, tente novamente."
            );
        }

        if (userRepository.existsByEmail(usuarioDTO.getEmail())) {
            log.warn("E-mail já em uso.", usuarioDTO.getEmail());

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Esse endereço de e-mail não está disponível."
            );
        }

        if (usuarioDTO.getPassword().length() < 6) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Senha deve ter ao menos 6 caracteres."
            );
        }

        UserEntity usuario = new UserEntity();
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setFullname(usuarioDTO.getFullname());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));

        UserEntity saved = userRepository.save(usuario);
        return userMapper.toDto(saved);
    }

    @Transactional
    public UserPresenterDTO update(Long id, UserCreateDTO usuarioDTO) {
        UserEntity updatedEntity = userRepository
            .findById(id)
            .map(existing -> {
                if (
                    usuarioDTO.getEmail() != null &&
                    !usuarioDTO.getEmail().equals(existing.getEmail())
                ) {
                    if (userRepository.existsByEmail(usuarioDTO.getEmail())) {
                        throw new RuntimeException("Email já em uso");
                    }
                    existing.setEmail(usuarioDTO.getEmail());
                }

                if (
                    usuarioDTO.getUsername() != null &&
                    !usuarioDTO.getUsername().equals(existing.getUsername())
                ) {
                    if (
                        userRepository.existsByUsername(
                            usuarioDTO.getUsername()
                        )
                    ) {
                        throw new RuntimeException("Username já em uso");
                    }
                    existing.setUsername(usuarioDTO.getUsername());
                }

                if (usuarioDTO.getFullname() != null) {
                    existing.setFullname(usuarioDTO.getFullname());
                }

                return userRepository.save(existing);
            })
            .orElseThrow(() ->
                new RuntimeException("Usuário não encontrado com id: " + id)
            );

        return userMapper.toDto(updatedEntity);
    }

    @Transactional
    public void delete(Long id) {
        UserEntity usuario = userRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("Usuário não encontrado com id: " + id)
            );
        userRepository.delete(usuario);
    }

    public List<UserPresenterDTO> searchByUsernameOrEmail(String term) {
        if (term == null || term.isBlank()) {
            return findAll();
        }
        String lower = term.toLowerCase();
        return userRepository
            .findAll()
            .stream()
            .filter(
                u ->
                    (u.getUsername() != null &&
                        u.getUsername().toLowerCase().contains(lower)) ||
                    (u.getEmail() != null &&
                        u.getEmail().toLowerCase().contains(lower))
            )
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    }
}
