package com.gerenciadorartistas.backend.features.user.controller;

import com.gerenciadorartistas.backend.features.user.dto.UserCreateDTO;
import com.gerenciadorartistas.backend.features.user.dto.UserPresenterDTO;
import com.gerenciadorartistas.backend.features.user.service.UserService;
import com.gerenciadorartistas.backend.shared.dto.ApiResponse;
import com.gerenciadorartistas.backend.shared.dto.ApiResponse.PaginationInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(
    name = "Usuários",
    description = "Endpoints para gerenciamento de usuários"
)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar todos os usuários",
        description = "Retorna uma lista de todos os usuários cadastrados. Requer permissão de ADMIN."
    )
    public ResponseEntity<ApiResponse<List<UserPresenterDTO>>> findAll() {
        List<UserPresenterDTO> usersList = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success(usersList));
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar usuários com paginação",
        description = "Retorna uma lista paginada de usuários. Requer permissão de ADMIN."
    )
    public ResponseEntity<ApiResponse<List<UserPresenterDTO>>> findAllPaginated(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(
            required = false,
            defaultValue = "asc"
        ) String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(direction, sortBy)
        );

        Page<UserPresenterDTO> usersPage = userService.findAllPaginated(
            pageable
        );
        PaginationInfo pagination = PaginationInfo.from(usersPage);

        return ResponseEntity.ok(
            ApiResponse.success(usersPage.getContent(), pagination)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Buscar usuário por ID",
        description = "Retorna um usuário específico pelo seu ID. Requer permissão de ADMIN."
    )
    public ResponseEntity<ApiResponse<UserPresenterDTO>> findById(
        @PathVariable Long id
    ) {
        try {
            UserPresenterDTO user = userService.findById(id);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(
                    "USER_NOT_FOUND",
                    "Usuário não encontrado com id: " + id
                )
            );
        }
    }

    @PostMapping
    @Operation(
        summary = "Criar novo usuário",
        description = "Cria um novo usuário no sistema"
    )
    public ResponseEntity<ApiResponse<UserPresenterDTO>> create(
        @Valid @RequestBody UserCreateDTO usuarioDTO
    ) {
        UserPresenterDTO created = userService.create(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(created)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Atualizar usuário",
        description = "Atualiza os dados de um usuário existente"
    )
    public ResponseEntity<ApiResponse<UserPresenterDTO>> update(
        @PathVariable Long id,
        @Valid @RequestBody UserCreateDTO usuarioDTO
    ) {
        try {
            UserPresenterDTO updated = userService.update(id, usuarioDTO);
            return ResponseEntity.ok(ApiResponse.success(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("USER_NOT_FOUND", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Deletar usuário",
        description = "Remove um usuário do sistema"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(
                    "USER_NOT_FOUND",
                    "Usuário não encontrado com id: " + id
                )
            );
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Buscar usuários por username ou email",
        description = "Busca usuários cujo username ou email contenha o termo informado"
    )
    public ResponseEntity<ApiResponse<List<UserPresenterDTO>>> searchByTerm(
        @RequestParam String term
    ) {
        List<UserPresenterDTO> usersList = userService.searchByUsernameOrEmail(
            term
        );
        return ResponseEntity.ok(ApiResponse.success(usersList));
    }
}
