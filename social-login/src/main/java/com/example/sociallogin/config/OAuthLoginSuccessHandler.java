package com.example.sociallogin.config;

import com.example.sociallogin.model.CustomOAuth2User;
import com.example.sociallogin.service.UserDetailsServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserDetailsServiceImpl userDetailsService;

    public OAuthLoginSuccessHandler(UserDetailsServiceImpl userRepository) {
        this.userDetailsService = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        String oauth2ClientName = oauth2User.getOauth2ClientName();
        String username = oauth2User.getEmail();

        if (userDetailsService.userExist(username)) {
            userDetailsService.updateAuthenticationType(username, oauth2ClientName);
        } else {
            userDetailsService.saveOauth2User(username, oauth2ClientName);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
