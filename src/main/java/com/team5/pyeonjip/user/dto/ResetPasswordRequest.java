package com.team5.pyeonjip.user.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotNull
    private String email;

    @NotNull
    private String name;
}
