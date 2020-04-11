package com.dream.jwt;

import com.dream.dtos.UserDTO;
import com.dream.models.Authority;
import com.dream.models.User;
import com.dream.repositories.UserRepository;
import com.dream.utils.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Authority authority : user.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(authority.getRole().name()));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isEnabled(), true, true, true, authorities);
    }

    public User save(UserDTO user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setEnabled(true);
        newUser.setAuthorities(Arrays.asList(new Authority(Role.ROLE_ADMIN)));
        return userRepository.save(newUser);
    }
}
