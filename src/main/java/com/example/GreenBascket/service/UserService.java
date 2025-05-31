package com.example.GreenBascket.service;

import com.example.GreenBascket.dto.UserProfileRequestDTO;
import com.example.GreenBascket.dto.UserProfileResponseDTO;
import com.example.GreenBascket.model.User;
import com.example.GreenBascket.repository.UserRepo;
import com.example.GreenBascket.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            // Consider throwing a custom exception like UserAlreadyExistsException
            throw new RuntimeException("Email already exists!"); // GlobalExceptionHandler will catch this
        }

        String userId = String.format("%08d", Math.abs(UUID.randomUUID().hashCode()) % 100000000);
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email)); // Use specific exception

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password for user: " + email); // Use specific exception
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new UserPrincipal(
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    /**
     * Fetches user profile data by userId.
     * @param userId The ID of the user to fetch.
     * @return UserProfileResponseDTO containing relevant user data.
     */
    public UserProfileResponseDTO getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        return new UserProfileResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getLocation(),
                user.getRole()
        );
    }

    /**
     * Updates user profile data.
     * @param userId The ID of the user to update.
     * @param requestDTO UserProfileRequestDTO containing updated fields.
     * @return UserProfileResponseDTO with the updated user data.
     */
    public UserProfileResponseDTO updateUserProfile(String userId, UserProfileRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        // Update fields that are editable
        user.setName(requestDTO.getName());
        user.setPhone(requestDTO.getPhone());
        user.setLocation(requestDTO.getLocation());
        // Do NOT update email or password directly here without re-authentication/separate flows

        User updatedUser = userRepository.save(user);

        return new UserProfileResponseDTO(
                updatedUser.getUserId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getLocation(),
                updatedUser.getRole()
        );
    }
}