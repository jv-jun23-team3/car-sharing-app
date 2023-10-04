package ua.mate.team3.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mate.team3.carsharingapp.dto.user.profile.UpdateUserInfoRequestDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UpdateUserRoleRequestDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UserInfoResponseDto;
import ua.mate.team3.carsharingapp.service.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update user role by ID", description = "The manager can change role of users,"
            + " and vice versa")
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public void updateUserRole(@PathVariable Long id,
                               @RequestBody UpdateUserRoleRequestDto requestDto) {
        userService.updateUserRole(id, requestDto);
    }

    @Operation(summary = "Get user profile info", description = "The customers can get profile"
            + " information about themselves")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserInfoResponseDto getUserInfo() {
        return userService.getUserInfo();
    }

    @Operation(summary = "Update user profile information", description = "The customer can change"
            + "information about themselves")
    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserInfoResponseDto updateUserInfo(@RequestBody UpdateUserInfoRequestDto requestDto) {
        return userService.updateUserInfo(requestDto);
    }
}
