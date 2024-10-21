package com.team5.pyeonjip.user.controller;

import com.team5.pyeonjip.user.dto.*;
import com.team5.pyeonjip.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserApiController {

    private final UserService userService;


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpDto dto) {

        boolean isSignUpSuccessful = userService.signUpProcess(dto);
        if (isSignUpSuccessful) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 마이페이지
    @GetMapping("/mypage")
    public ResponseEntity<UserInfoDto> mypage(@RequestParam String email) {

        return ResponseEntity.ok(userService.getUserInfo(email));
    }


    // 단일 유저 조회
//    @GetMapping("/{userId}")
//    public ResponseEntity<User> getUser(@PathVariable("userId") Long userId) {
//
//        return ResponseEntity.ok(userService.findUser(userId));
//    }


    // 단일 유저 조회(이메일)
    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable("email") String email) {
        UserResponseDto userResponseDto = userService.findUserByEmail(email);
        return ResponseEntity.ok(userResponseDto);
    }


    // 유저 주소 변경
    @PutMapping("/address/{email}")
    public ResponseEntity<Boolean> updateUserInfo(@PathVariable("email") String email, @RequestBody UserUpdateAddressDto addressDto) {

        boolean updateResult = userService.updateUserAddress(email, addressDto);
        return ResponseEntity.ok(updateResult);
    }


    // 유저 비밀번호 변경
    @PutMapping("/password/{email}")
    public ResponseEntity<Boolean> updateUserPassword(@PathVariable("email") String email, @RequestBody UserUpdatePasswordDto passwordDto) {

        return ResponseEntity.ok(userService.updateUserPassword(email, passwordDto));
    }


    // 유저 삭제
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable("email") String email) {

        userService.deleteUser(email);
        return ResponseEntity.ok().build();
    }

}
