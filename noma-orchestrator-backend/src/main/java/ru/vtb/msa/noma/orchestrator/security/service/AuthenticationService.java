package ru.vtb.msa.noma.orchestrator.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vtb.msa.noma.orchestrator.db.entity.SecurityUser;
import ru.vtb.msa.noma.orchestrator.db.repository.SecurityUserRepository;
import ru.vtb.msa.noma.orchestrator.enums.Role;
import ru.vtb.msa.noma.orchestrator.exception.SecurityUserNotFoundException;
import ru.vtb.msa.noma.orchestrator.security.dto.AuthenticationRequest;
import ru.vtb.msa.noma.orchestrator.security.dto.AuthenticationResponse;
import ru.vtb.msa.noma.orchestrator.security.dto.RegistrationRequest;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecurityUserRepository securityUserRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegistrationRequest request) {
        SecurityUser securityUser = SecurityUser.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        securityUserRepository.save(securityUser);
        String token = jwtService.generateToken(securityUser);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password()));
        SecurityUser securityUser = securityUserRepository.findByEmail(authenticationRequest.email()).orElseThrow(() -> new SecurityUserNotFoundException("User not found"));
        String token = jwtService.generateToken(securityUser);
        return new AuthenticationResponse(token);
    }
}
