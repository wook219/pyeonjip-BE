package com.team5.pyeonjip.category.dto;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryCreateRequest {

    private String name;

    @Setter
    private Integer sort;

}
