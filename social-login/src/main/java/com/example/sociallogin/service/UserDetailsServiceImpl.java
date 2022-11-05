package com.example.sociallogin.service;

import com.example.sociallogin.enums.AuthenticationType;
import com.example.sociallogin.model.User;
import com.example.sociallogin.model.UserPrincipal;
import com.example.sociallogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }

        return new UserPrincipal(user);
    }

    public void saveOauth2User(String username, String oauth2ClientName) {
        User oauthUser = new User();
        oauthUser.setUsername(username);
        oauthUser.setAuthType(AuthenticationType.valueOf(oauth2ClientName.toUpperCase()));
        oauthUser.setAuthorities(new String[]{"read"});
        userRepository.save(oauthUser);
    }

    public boolean userExist(String username) {
        return !Objects.isNull(userRepository.getUserByUsername(username));
    }

    @Transactional
    public void updateAuthenticationType(String username, String oauth2ClientName) {
        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        user.setAuthType(AuthenticationType.valueOf(oauth2ClientName.toUpperCase()));
        userRepository.save(user);
        System.out.println("Updated user's authentication type to " + oauth2ClientName);
    }

}
