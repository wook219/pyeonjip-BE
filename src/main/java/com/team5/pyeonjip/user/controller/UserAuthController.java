package com.team5.pyeonjip.user.controller;

import com.team5.pyeonjip.user.dto.*;
import com.team5.pyeonjip.user.entity.User;
import com.team5.pyeonjip.user.service.ReissueService;
import com.team5.pyeonjip.user.service.SendEmailService;
import com.team5.pyeonjip.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final SendEmailService sendEmailService;
    private final UserService userService;
    private final ReissueService reissueService;


    /* 로그인, 로그아웃은 각 필터에서 경로 설정하였음.*/

    // 계정 찾기
    @GetMapping("/find")
    public ResponseEntity<UserFoundAccountDto> findAccount(@RequestParam String name, @RequestParam String phoneNumber) {

        UserFindAccountDto findDto = new UserFindAccountDto(name, phoneNumber);
        UserFoundAccountDto foundDto = userService.findAccount(findDto);
        return ResponseEntity.ok(foundDto);
    }


    // 비밀번호 재설정
//  1. DB에서 이메일과 이름이 일치하는지 확인하는 컨트롤러.
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkForResetPassword(@RequestBody ResetPasswordRequest dto) {

        boolean isCorrect = userService.checkUserForReset(dto.getName(), dto.getEmail());
        return ResponseEntity.ok(new ApiResponse<>(isCorrect));
    }


    //  2. 등록된 이메일로 임시 비밀번호를 발송하고, 사용자의 비밀번호도 새로 업데이트한다.
    @PostMapping("/check/reset")
    public ResponseEntity<Void> sendEmail(@RequestBody ResetPasswordRequest dto) {

        MailDto mailDto = sendEmailService.createMailAndChangePassword(dto.getEmail(), dto.getName());
        sendEmailService.mailSend(mailDto);

        return ResponseEntity.ok().build();
    }


    // Access 토큰 재발급을 위한 컨트롤러
    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {

        reissueService.reissueRefreshToken(request, response);
        return ResponseEntity.ok().build();
    }

}
