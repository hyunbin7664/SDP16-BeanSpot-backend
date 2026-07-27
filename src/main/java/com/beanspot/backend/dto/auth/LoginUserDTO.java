package com.beanspot.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LoginUserDTO {
    @Getter
    public static class Req {
        @NotBlank
        private String userId;
        @NotBlank
        private String password;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private String accessToken;
        private String refreshToken;
        private Long id;
        private String nickname;
        private String role;
        private Boolean isProfileComplete;
    }
}
