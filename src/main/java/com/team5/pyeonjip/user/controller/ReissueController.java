package com.team5.pyeonjip.user.controller;


import com.team5.pyeonjip.user.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
public class ReissueController {

    private final ReissueService reissueService;


    // Access 토큰 재발급을 위한 컨트롤러
    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {

        reissueService.reissueRefreshToken(request, response);
        return ResponseEntity.ok().build();
    }

}
