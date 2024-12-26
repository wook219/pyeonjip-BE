package com.team5.pyeonjip.user.mapper;

import com.team5.pyeonjip.user.dto.SignUpDto;
import com.team5.pyeonjip.user.entity.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-26T22:48:38+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 21 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(SignUpDto dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( dto.getEmail() );
        user.setName( dto.getName() );
        user.setPhoneNumber( dto.getPhoneNumber() );
        user.setPassword( dto.getPassword() );
        user.setAddress( dto.getAddress() );

        return user;
    }
}
