package com.marles.horarioappufps.controller;

import com.marles.horarioappufps.dto.response.UserDto;
import com.marles.horarioappufps.model.User;
import com.marles.horarioappufps.security.UserPrincipal;
import com.marles.horarioappufps.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> response = UserDto.parseFromDto(users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserByUid(@PathVariable String uid) {
        return ResponseEntity.ok(UserDto.parseFromDto(userService.getUserByUid(uid)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> getMyUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String uid = userPrincipal.getUsername();
        return ResponseEntity.ok(UserDto.parseFromDto(userService.getUserByUid(uid)));
    }
}
