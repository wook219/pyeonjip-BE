package com.team5.pyeonjip.user.service;

import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import com.team5.pyeonjip.user.dto.*;
import com.team5.pyeonjip.user.mapper.UserMapper;
import com.team5.pyeonjip.user.entity.User;
import com.team5.pyeonjip.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public boolean signUpProcess(SignUpDto dto) {

//      1. 중복 이메일 검증
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new GlobalException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

//      2. 중복 이메일이 없으면 회원가입 절차 실행
        try {
//          2 - 1. 비밀번호 인코딩
            String encodedPassword = bCryptPasswordEncoder.encode(dto.getPassword());

//          2 - 2. dto 엔티티화
            User user = UserMapper.INSTANCE.toEntity(dto);

//          2 - 3. user 객체에 인코딩된 비밀번호 값을 설정
            user.setPassword(encodedPassword);

            userRepository.save(user);

            return true;
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.USER_SIGNUP_FAILED);
        }


    }


    // 마이페이지 조회
    public UserInfoDto getUserInfo(String email) {

        // 유저를 찾지 못할 경우 예외처리.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        return UserInfoDto.builder().name(user.getName())
                                    .email(user.getEmail())
                                    .phoneNumber(user.getPhoneNumber())
                                    .address(user.getAddress())
                                    .build();
    }


    // 개인정보 변경
    @Transactional
    public Boolean updateUserAddress(String email, UserUpdateAddressDto dto) {

        if (dto.getAddress() == null) {
            throw new GlobalException(ErrorCode.INVALID_USER_UPDATE);
        }

        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        String newAddress = dto.getAddress();

        foundUser.setAddress(newAddress);

        return true;
    }


    // 비밀번호 업데이트
    @Transactional
    public Boolean updateUserPassword(String email, UserUpdatePasswordDto dto) {

        if (dto.getPassword() == null) {
            throw new GlobalException(ErrorCode.INVALID_USER_UPDATE);
        }

        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        String newPassword = bCryptPasswordEncoder.encode(dto.getPassword());

        foundUser.setPassword(newPassword);

        return true;
    }


    // 유저 삭제
    public void deleteUser(String email) {

        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException((ErrorCode.ACCOUNT_NOT_FOUND)));

        try {
            userRepository.delete(foundUser);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.USER_DELETE_FAILED);
        }

    }


    // 계정 찾기
    public User findAccount(UserFindAccountDto dto) {

        if (!checkUserForAccount(dto)) {
            return null;
        }

        return userRepository.findByNameAndPhoneNumber(dto.getName(), dto.getPhoneNumber())
                .orElseThrow(() -> new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));
    }


    // 계정을 찾기 위한 본인 확인 메서드
    private Boolean checkUserForAccount(UserFindAccountDto dto) {

        return userRepository.existsByNameAndPhoneNumber(dto.getName(), dto.getPhoneNumber());
    }


    // 비밀번호 재설정 시 이름과 이메일에 해당되는 유저가 존재하는지 확인
    public Boolean checkUserForReset(String name, String email) {

        return userRepository.existsByNameAndEmail(name, email);
    }


    public List<User> findAllUsers() {

        return userRepository.findAll();
    }

    public User findUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }


    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }


    public String getTempPassword() {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String tempPassword = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            tempPassword += charSet[idx];
        }
        return tempPassword;
    }
}
