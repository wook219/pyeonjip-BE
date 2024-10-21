package com.team5.pyeonjip.user.dto;

import com.team5.pyeonjip.user.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserResponseDto
{
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private Grade grade;
}
