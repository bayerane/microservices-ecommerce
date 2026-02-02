package com.microservices.auth.security;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.auth.entity.User;
import com.microservices.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation de UserDetailsService pour Spring Security
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;

    // Charge un utilisateur par son email(username)
    @Override
    @Transactional(readOnly =  true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Tentative de chargement de l'utilisateur avec email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé avec email: {}", email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec email: " + email);
                });

        log.debug("Utilisateur trouvé: {} avec rôle: {}", user.getEmail(), user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(!user.isEnabled())
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }

    // Récupère les autorités (rôles) d'un utilisateur
    private List<GrantedAuthority> getAuthorities(User user) {
        String role = "ROLE_" + user.getRole().toString();
        log.debug("Attribution de l'autorité: {}", role);
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
