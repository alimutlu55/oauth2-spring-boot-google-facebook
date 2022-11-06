package com.example.sociallogin.oauth.info;

import com.example.sociallogin.oauth.enums.AuthenticationType;
import com.example.sociallogin.oauth.model.FacebookOAuth2UserInfo;
import com.example.sociallogin.oauth.model.GoogleOAuth2UserInfo;
import com.example.sociallogin.oauth.model.OAuth2UserInfo;


import java.util.Map;

public class OAuth2UserFactory {

    public static OAuth2UserInfo getOAuth2User(AuthenticationType authenticationType, Map<String, Object> attributes) {
        switch (authenticationType) {
            case GOOGLE:
                return new GoogleOAuth2UserInfo(attributes);
            case FACEBOOK:
                return new FacebookOAuth2UserInfo(attributes);
            default:
                throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
