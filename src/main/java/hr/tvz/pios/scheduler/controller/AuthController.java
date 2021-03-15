package hr.tvz.pios.scheduler.controller;

import hr.tvz.pios.scheduler.config.JwtTokenUtil;
import hr.tvz.pios.scheduler.dto.ApiResponse;
import hr.tvz.pios.scheduler.dto.JwtRequest;
import hr.tvz.pios.scheduler.dto.JwtResponse;
import hr.tvz.pios.scheduler.dto.RegisterUserDto;
import hr.tvz.pios.scheduler.service.JwtUserDetailsService;
import hr.tvz.pios.scheduler.service.UserService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> createAuthenticationToken(@RequestBody final JwtRequest authenticationRequest) {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new ApiResponse(new JwtResponse(token)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerNewUser(@Valid @RequestBody final RegisterUserDto registerRequest) {
        userService.save(registerRequest);
        return ResponseEntity.noContent().build();
    }

    private void authenticate(String username, String password) throws DisabledException, BadCredentialsException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}