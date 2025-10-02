package com.marles.horarioappufps.controller;

import com.marles.horarioappufps.dto.request.UserUpdateDto;
import com.marles.horarioappufps.dto.response.UserDto;
import com.marles.horarioappufps.exception.UserException;
import com.marles.horarioappufps.model.User;
import com.marles.horarioappufps.security.UserPrincipal;
import com.marles.horarioappufps.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> response = UserDto.parseFromUser(users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserByUid(@PathVariable String uid) {
        return ResponseEntity.ok(UserDto.parseFromUser(userService.getUserByUid(uid)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> getMyUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String uid = userPrincipal.getUsername();
        return ResponseEntity.ok(UserDto.parseFromUser(userService.getUserByUid(uid)));
    }

    @PutMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserUpdateDto userDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String uid = userPrincipal.getUsername();
        return ResponseEntity.ok(UserDto.parseFromUser(userService.updateUser(uid, userDto)));
    }

    @PutMapping("/toggle/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> toggleUser(@PathVariable String uid, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String myUid = userPrincipal.getUsername();
        if(uid.equals(myUid)) {
            throw new UserException("No se puede degradar a sí mismo");
        }
        return ResponseEntity.ok(UserDto.parseFromUser(userService.toggleUserRole(uid)));
    }
}
