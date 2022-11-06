package com.example.sociallogin.oauth.service;

import com.example.sociallogin.oauth.enums.AuthenticationType;
import com.example.sociallogin.oauth.info.OAuth2UserFactory;
import com.example.sociallogin.oauth.model.OAuth2UserInfo;
import com.example.sociallogin.entity.User;
import com.example.sociallogin.oauth.model.UserPrincipal;
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
        AuthenticationType authenticationType = AuthenticationType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserFactory.getOAuth2User(authenticationType, user.getAttributes());

        User savedUser = userRepository.getUserByUsername(oAuth2UserInfo.getEmail());

        if (savedUser != null) {
//            if (providerType != savedUser.getAuthType()) {
//                throw new OAuthProviderMissMatchException(
//                        "Looks like you're signed up with " + providerType +
//                                " account. Please use your " + savedUser.getAuthType() + " account to login."
//                );
//            }
            updateAuthenticationType(oAuth2UserInfo, authenticationType);
        } else {
            savedUser = saveOauth2User(oAuth2UserInfo, authenticationType);
        }

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    public User saveOauth2User(OAuth2UserInfo user, AuthenticationType authenticationType) {
        User oauthUser = new User();
        oauthUser.setUsername(user.getEmail());
        oauthUser.setAuthType(authenticationType);
        oauthUser.setAuthorities(new String[]{"read"});
        return userRepository.save(oauthUser);
    }

    @Transactional
    public void updateAuthenticationType(OAuth2UserInfo oAuth2UserInfo, AuthenticationType authenticationType) {
        User user = userRepository.getUserByUsername(oAuth2UserInfo.getEmail());

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        user.setAuthType(authenticationType);
        userRepository.save(user);
        System.out.println("Updated user's authentication type to " + authenticationType);
    }

}