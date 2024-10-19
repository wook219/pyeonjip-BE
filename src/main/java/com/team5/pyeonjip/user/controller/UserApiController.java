package com.team5.pyeonjip.user.controller;

import com.team5.pyeonjip.user.dto.*;
import com.team5.pyeonjip.user.entity.User;
import com.team5.pyeonjip.user.service.SendEmailService;
import com.team5.pyeonjip.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserApiController {

    private final UserService userService;
    private final SendEmailService sendEmailService;


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto dto) {

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
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email) {

        return ResponseEntity.ok(userService.findUserByEmail(email));
    }


    // 유저 정보 업데이트
    @PutMapping("/{email}")
    public ResponseEntity<Void> updateUserInfo(@PathVariable("email") String email, @RequestBody UserUpdateDto dto) {

        userService.updateUserInfo(email, dto);
        return ResponseEntity.noContent().build();
    }


    // 유저 삭제
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable("email") String email) {

        userService.deleteUser(email);
        return ResponseEntity.ok().build();
    }


    // 계정 찾기
    @GetMapping("/find")
    public ResponseEntity<String> findAccount(@RequestParam String name, @RequestParam String phoneNumber) {

        UserFindAccountDto dto = new UserFindAccountDto(name, phoneNumber);
        User user = userService.findAccount(dto);

        return ResponseEntity.ok(user.getEmail());
    }


    // 비밀번호 재설정
//  1. DB에서 이메일과 이름이 일치하는지 확인하는 컨트롤러.
    @PostMapping("/check/reset")
    public ResponseEntity<ApiResponse<Boolean>> checkForResetPassword(@RequestBody ResetPasswordRequest dto) {

        boolean isCorrect = userService.checkUserForReset(dto.getName(), dto.getEmail());
        return ResponseEntity.ok(new ApiResponse<>(isCorrect));
    }


//  2. 등록된 이메일로 임시 비밀번호를 발송하고, 사용자의 비밀번호도 새로 업데이트한다.
    @PostMapping("/check/reset/sendEmail")
    public ResponseEntity<Void> sendEmail(@RequestBody ResetPasswordRequest dto) {

        MailDto mailDto = sendEmailService.createMailAndChangePassword(dto.getEmail(), dto.getName());
        sendEmailService.mailSend(mailDto);

        return ResponseEntity.ok().build();
    }

}
