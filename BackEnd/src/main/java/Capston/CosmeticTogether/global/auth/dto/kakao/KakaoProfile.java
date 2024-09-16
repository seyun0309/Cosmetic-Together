package Capston.CosmeticTogether.global.auth.dto.kakao;

import lombok.Data;

@Data
public class KakaoProfile {
    private Long id;
    private String connected_at;
    private Properties properties;
    private Kakao_account kakao_account;
    private String email;

    @Data
    public static class Kakao_account {
        private Boolean profile_nickname_needs_agreement;
        private Boolean profile_image_needs_agreement;
        private Profile profile;

        @Data
        public static class Profile {
            private String nickname;
            private String thumbnail_image_url;
            private String profile_image_url;
            private Boolean is_default_image;
            private Boolean is_default_nickname;  // 추가된 필드
        }
    }

    @Data
    public static class Properties {
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }
}

