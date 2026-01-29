package com.gerenciadorartistas.backend.features.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserCreateDTO {

    @NotBlank(message = "Username é obrigatório")
    @Size(
        min = 3,
        max = 50,
        message = "Username deve ter entre 3 e 50 caracteres"
    )
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "Username só pode ter letras, números e underscore"
    )
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(
        min = 6,
        max = 100,
        message = "Senha deve ter entre 6 e 100 caracteres"
    )
    private String password;

    @Size(
        max = 255,
        message = "Nome completo deve ter no máximo 255 caracteres"
    )
    private String fullname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String nomeCompleto) {
        this.fullname = nomeCompleto;
    }
}
