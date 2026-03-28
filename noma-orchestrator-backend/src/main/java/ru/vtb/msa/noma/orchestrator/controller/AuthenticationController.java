package ru.vtb.msa.noma.orchestrator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vtb.msa.noma.orchestrator.security.dto.AuthenticationRequest;
import ru.vtb.msa.noma.orchestrator.security.dto.AuthenticationResponse;
import ru.vtb.msa.noma.orchestrator.security.dto.RegistrationRequest;
import ru.vtb.msa.noma.orchestrator.security.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/authentication")   // задаём базовый путь
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")               // теперь полный путь /api/v1/authentication/register
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")           // полный путь /api/v1/authentication/authenticate
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}