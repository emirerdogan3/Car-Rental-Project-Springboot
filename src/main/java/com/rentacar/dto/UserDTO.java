package com.rentacar.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Data
public class UserDTO {
    private Integer userID;
    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username;
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi girin")
    private String email;
    private String phone;
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;
    private boolean enabled = true;
    private RoleDTO role;
} 