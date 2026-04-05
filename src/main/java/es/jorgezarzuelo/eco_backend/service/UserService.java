package es.jorgezarzuelo.eco_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.jorgezarzuelo.eco_backend.enums.AuthProvider;
import es.jorgezarzuelo.eco_backend.enums.Role;
import es.jorgezarzuelo.eco_backend.model.User;
import es.jorgezarzuelo.eco_backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(String username, String email, String password) {
        String usernameClean = username == null ? "" : username.trim().toLowerCase();
        String emailClean = email == null ? "" : email.trim().toLowerCase();
        String passwordClean = password == null ? "" : password;

        if (usernameClean.isBlank() || emailClean.isBlank() || passwordClean.isBlank()) {
            throw new IllegalArgumentException("Username, email and password cannot be blank");
        }
        if (passwordClean.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (userRepository.existsByUsernameIgnoreCase(usernameClean)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmailIgnoreCase(emailClean)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(usernameClean);
        user.setEmail(emailClean);
        user.setPasswordHash(passwordEncoder.encode(passwordClean));
        user.setRole(Role.USER);
        user.setAuthProvider(AuthProvider.LOCAL);

        userRepository.save(user);

        return "User registered successfully";
    }

    public User login(String usernameOrEmail, String password) {
        String identifierClean = usernameOrEmail == null ? "" : usernameOrEmail.trim().toLowerCase();
        String passwordClean = password == null ? "" : password;

        if (identifierClean.isBlank() || passwordClean.isBlank()) {
            throw new IllegalArgumentException("Username/email and password cannot be blank");
        }

        User user = userRepository.findByUsernameIgnoreCase(identifierClean)
                .or(() -> userRepository.findByEmailIgnoreCase(identifierClean))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username/email or password"));

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new IllegalArgumentException("This account uses external login ");
        }
        if (user.getPasswordHash() == null || !passwordEncoder.matches(passwordClean, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        return user;
    }

}
