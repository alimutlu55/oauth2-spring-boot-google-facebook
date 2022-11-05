package com.example.sociallogin.service;

import com.example.sociallogin.model.CustomOAuth2User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.AuthProvider;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserDetailsServiceImpl userDetailsService;

    public CustomOAuth2UserService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String clientName = userRequest.getClientRegistration().getClientName();
        OAuth2User user = super.loadUser(userRequest);
        return new CustomOAuth2User(user, clientName);
    }
}