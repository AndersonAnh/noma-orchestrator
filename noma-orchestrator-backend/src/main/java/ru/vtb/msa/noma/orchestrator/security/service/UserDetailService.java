package ru.vtb.msa.noma.orchestrator.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vtb.msa.noma.orchestrator.db.repository.SecurityUserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailService implements UserDetailsService {
    private final SecurityUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
