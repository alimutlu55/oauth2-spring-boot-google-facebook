package com.example.sociallogin.service;

import com.example.sociallogin.enums.AuthenticationType;
import com.example.sociallogin.model.CustomOAuth2User;
import com.example.sociallogin.model.User;
import com.example.sociallogin.repository.UserRepository;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        AuthenticationType providerType = AuthenticationType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(user, providerType.name());
        User savedUser = userRepository.getUserByUsername(customOAuth2User.getEmail());

        if (savedUser != null) {
//            if (providerType != savedUser.getAuthType()) {
//                throw new OAuthProviderMissMatchException(
//                        "Looks like you're signed up with " + providerType +
//                                " account. Please use your " + savedUser.getAuthType() + " account to login."
//                );
//            }
            updateAuthenticationType(customOAuth2User);
        } else {
            savedUser = saveOauth2User(customOAuth2User);
        }

        return new CustomOAuth2User(user, userRequest.getClientRegistration().getClientName());
    }

    public User saveOauth2User(CustomOAuth2User user) {
        User oauthUser = new User();
        oauthUser.setUsername(user.getEmail());
        oauthUser.setAuthType(AuthenticationType.valueOf(user.getOauth2ClientName()));
        oauthUser.setAuthorities(new String[]{"read"});
        return userRepository.save(oauthUser);
    }

    @Transactional
    public void updateAuthenticationType(CustomOAuth2User oAuth2User) {
        User user = userRepository.getUserByUsername(oAuth2User.getEmail());

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        user.setAuthType(AuthenticationType.valueOf(oAuth2User.getOauth2ClientName()));
        userRepository.save(user);
        System.out.println("Updated user's authentication type to " + oAuth2User.getOauth2ClientName());
    }

}