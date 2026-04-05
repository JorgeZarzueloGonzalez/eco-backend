package es.jorgezarzuelo.eco_backend.dto.auth;

public record LoginRequest(
        String usernameOrEmail,
        String password) {

}
