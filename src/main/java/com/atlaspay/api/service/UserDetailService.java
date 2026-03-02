package com.atlaspay.api.service;

import com.atlaspay.api.model.User;
import com.atlaspay.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    /**
     * Load user by email or phone no
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username)
                .orElse(userRepo.findByPhone(username)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "User not found with email or phone: " + username)));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true, true, true, true,
                getAuthorities());
    }

    /**
     * get user authorities/roles
     * @return
     */
    private Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }
}
