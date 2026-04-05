package es.jorgezarzuelo.eco_backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.jorgezarzuelo.eco_backend.dto.auth.LoginRequest;
import es.jorgezarzuelo.eco_backend.dto.auth.RegisterRequest;
import es.jorgezarzuelo.eco_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        String msg = userService.register(request.username(), request.email(), request.password());
        return ResponseEntity.ok(Map.of("message", msg));
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        httpRequest.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context); // Create session if not exists

        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

}
