package com.team5.pyeonjip.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpDto {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String name;

    @NotNull
    @Pattern(regexp = "(010)[0-9]{4}[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @NotNull
    private String password;

    @NotNull
    private String address;

    @NotNull
    private String passwordHint;
}
