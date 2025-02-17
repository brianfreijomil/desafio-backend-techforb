package com.challenge_techforb.desafio_backend.service;

import com.challenge_techforb.desafio_backend.controller.dto.request.AuthCreateUserIn;
import com.challenge_techforb.desafio_backend.controller.dto.request.AuthLoginIn;
import com.challenge_techforb.desafio_backend.controller.dto.response.AuthResponse;
import com.challenge_techforb.desafio_backend.controller.dto.response.UserInfoOut;
import com.challenge_techforb.desafio_backend.exception.ConflictExistException;
import com.challenge_techforb.desafio_backend.persistence.entity.RoleEntity;
import com.challenge_techforb.desafio_backend.persistence.entity.UserEntity;
import com.challenge_techforb.desafio_backend.persistence.repository.RoleRepository;
import com.challenge_techforb.desafio_backend.persistence.repository.UserRepository;
import com.challenge_techforb.desafio_backend.util.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * load a user by "email"
     *
     * @param email
     * @return userdetails
     */
    @Override
    public UserDetails loadUserByUsername(String email) {

        UserEntity userEntity = this.userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException(String.format("Usuario o contraseña no válidos.")));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        userEntity.getRoles().stream().flatMap(role -> role.getPermissionList().stream()).forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(userEntity.getUsername(), userEntity.getPassword(), authorityList);
    }

    /**
     * register a new user
     * @param request
     * @return status of registration
     */
    public AuthResponse createUser(AuthCreateUserIn request) {

        String email = request.getEmail();
        String username = request.getUsername();
        String password = request.getPassword();

        if (this.userRepository.existsByEmailIgnoreCase(email)) throw new ConflictExistException("El email ingresado ya esta en uso");
        if (this.userRepository.existsByUsernameIgnoreCase(username)) throw new ConflictExistException("El nombre de usuario ingresado ya esta en uso");

        //por ahora le doy a todos rol de admin
        List<RoleEntity> roleEntityList = roleRepository.findRoleEntitiesByRoleEnumIn(List.of("ADMIN")).stream().collect(Collectors.toList());

        if (roleEntityList.isEmpty()) {
            throw new IllegalArgumentException("The roles specified does not exist.");
        }

        UserEntity userEntity = new UserEntity(
                username,
                email,
                passwordEncoder.encode(password),
                roleEntityList,
                true
        );

        UserEntity userSaved = userRepository.save(userEntity);

        return AuthResponse.builder().message("Registro de usuario exitoso!").status(true).build();
    }

    /**
     * login a user by credentials
     *
     * @param authLoginRequest
     * @return login session info
     */
    public AuthResponse loginUser(AuthLoginIn authLoginRequest) {

        String email = authLoginRequest.getEmail();
        String password = authLoginRequest.getPassword();

        Authentication authentication = this.authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        AuthResponse authResponse = new AuthResponse(authentication.getPrincipal().toString(), "User loged succesfully", accessToken, true);
        return authResponse;
    }

    /**
     * authenticate a user by credentials
     *
     * @param email
     * @param password
     * @return authentication
     */
    public Authentication authenticate(String email, String password) {
        UserDetails userDetails = this.loadUserByUsername(email);

        if (userDetails == null) {
            throw new BadCredentialsException(String.format("Usuario o contraseña incorrectos."));
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos.");
        }

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), password, userDetails.getAuthorities());
    }

    /**
     * get a user info by username
     *
     * @param username
     * @return user info
     */
    public UserInfoOut getUserInfo(String username) {
        UserEntity user = this.userRepository.findByUsernameIgnoreCase(username).orElseThrow(()-> new EntityNotFoundException("el usuario."));
        return UserInfoOut.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}