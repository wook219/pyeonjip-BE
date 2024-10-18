package com.team5.pyeonjip.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MailDto {

    @NotNull
    private String address;

    @NotNull
    private String title;

    @NotNull
    private String message;
}
