package ua.mate.team3.carsharingapp.controller;

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

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public void updateUserRole(@PathVariable Long id,
                               @RequestBody UpdateUserRoleRequestDto requestDto) {
        userService.updateUserRole(id, requestDto);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserInfoResponseDto getUserInfo() {
        return userService.getUserInfo();
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserInfoResponseDto updateUserInfo(@RequestBody UpdateUserInfoRequestDto requestDto) {
        return userService.updateUserInfo(requestDto);
    }
}
