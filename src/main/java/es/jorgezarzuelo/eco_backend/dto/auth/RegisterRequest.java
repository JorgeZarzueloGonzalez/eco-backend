package es.jorgezarzuelo.eco_backend.dto.auth;

public record RegisterRequest(
        String username,
        String email,
        String password) {

}
