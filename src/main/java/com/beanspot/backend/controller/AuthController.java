package com.beanspot.backend.controller;

import com.beanspot.backend.common.exception.CustomException;
import com.beanspot.backend.common.exception.ErrorCode;
import com.beanspot.backend.common.response.ApiResponse;
import com.beanspot.backend.dto.auth.CheckAvailabilityResponseDTO;
import com.beanspot.backend.dto.auth.LoginUserDTO;
import com.beanspot.backend.dto.auth.SignUpSocialUserDTO;
import com.beanspot.backend.dto.auth.SignUpUserDTO;
import com.beanspot.backend.security.CurrentUserSocialId;
import com.beanspot.backend.service.KakaoLoginService;
import com.beanspot.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@Slf4j
@Tag(name="인증 API", description = "회원가입 및 로그인 관련 API입니다.")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private KakaoLoginService kakaoLoginService;

    @Value("${oauth.kakao.redirect-url}")
    private String redirectUrl;


    @PostMapping("/signup")
    public ApiResponse<SignUpUserDTO.Res> signup(@RequestBody SignUpUserDTO.Req userDTO) {

        if (userDTO == null || userDTO.getPassword() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        SignUpUserDTO.Res responseUserDTO = userService.createUser(userDTO);
        return ApiResponse.ok(responseUserDTO);

    }


    @PostMapping("/login")
    public ApiResponse<LoginUserDTO.Res> login(@RequestBody LoginUserDTO.Req userDTO) {

        LoginUserDTO.Res reponseUserDTO = userService.login(
                userDTO,
                passwordEncoder
        );

        return ApiResponse.ok(reponseUserDTO);

    }

    @PostMapping("/oauth/kakao/signup")
    public ApiResponse<SignUpSocialUserDTO.Res> kakaoSignup(@CurrentUserSocialId String socialId, @RequestBody SignUpSocialUserDTO.Req userDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(socialId == null) return ApiResponse.fail(new CustomException(ErrorCode.AUTH_VERIFICATION_CODE_EXPIRED));
        log.info("socialId: {}", socialId);
        SignUpSocialUserDTO.Res responseUserDTO = userService.createSocialUser(socialId, userDTO);
        return ApiResponse.ok(responseUserDTO);

    }

    @GetMapping("/oauth/kakao/login")
    public ApiResponse<LoginUserDTO.Res> kakaoLogin(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String socialId = kakaoLoginService.kakaoLogin(code,redirectUrl).getId();
        LoginUserDTO.Res responseUserDTO = userService.loginBySocialId("kakao", socialId);

        log.info("accessToken: {}", responseUserDTO.getAccessToken());

        return ApiResponse.ok(responseUserDTO);

    }

    @GetMapping("/check-nickname")
    public ApiResponse<CheckAvailabilityResponseDTO> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        String message = isAvailable ?
                "사용 가능한 닉네임입니다." :
                "이미 사용 중인 닉네임입니다.";
        CheckAvailabilityResponseDTO responseDTO = CheckAvailabilityResponseDTO.builder()
                                                    .available(isAvailable)
                                                    .value(nickname)
                                                    .type("nickname")
                                                    .message(message)
                                                    .build();
        return ApiResponse.ok(responseDTO);
    }

    @GetMapping("/check-id")
    public ApiResponse<CheckAvailabilityResponseDTO> checkId(@RequestParam String userId) {
        if (userId == null || userId.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        boolean isAvailable = userService.isUserIdAvailable(userId);
        String message = isAvailable ?
                "사용 가능한 아이디입니다." :
                "이미 사용 중인 아이디입니다.";
        CheckAvailabilityResponseDTO responseDTO = CheckAvailabilityResponseDTO.builder()
                .available(isAvailable)
                .value(userId)
                .type("userId")
                .message(message)
                .build();
        return ApiResponse.ok(responseDTO);
    }

}
