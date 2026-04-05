package es.jorgezarzuelo.eco_backend.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.jorgezarzuelo.eco_backend.enums.AuthProvider;
import es.jorgezarzuelo.eco_backend.model.User;
import es.jorgezarzuelo.eco_backend.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        String identifier = usernameOrEmail == null ? "" : usernameOrEmail.trim().toLowerCase();

        User user = userRepository.findByUsernameIgnoreCase(identifier)
                .or(() -> userRepository.findByEmailIgnoreCase(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new UsernameNotFoundException("This account uses external login");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }

}
