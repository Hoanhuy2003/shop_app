package com.project.shopapp.cotrollers;

import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.LoginResponse;
import com.project.shopapp.responses.UserResponse;
import com.project.shopapp.services.IUserService;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;


    @PostMapping("/register") // bảo mật
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList(); // lấy ra những file lỗi
                return ResponseEntity.badRequest().body(errorMessage);
            }
            //kiểm tra mật khẩu và gõ lại mậu khẩu
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                return ResponseEntity.badRequest().body(MessageKeys.PASSWORD_NOT_MATCH);
            }
           User user = userService.createUser(userDTO);
           // return ResponseEntity.ok("đăng nhập thành công");
           return ResponseEntity.ok(user);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PostMapping("/register-admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }

            // kiểm tra mật khẩu và nhập lại mật khẩu
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body("Mật khẩu nhập lại không khớp!");
            }

            // ép role về ADMIN
            userDTO.setRoleId(2L);

            User user = userService.createUser(userDTO);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody
                                                   UserLoginDTO userLoginDTO

                                               ) {
    try {
        String token = userService.login(
                userLoginDTO.getPhoneNumber(),
                userLoginDTO.getPassword(),
                userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId());


        return ResponseEntity.ok(LoginResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                        .token(token)
                .build());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                LoginResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED,e.getMessage()))
                        .build()
        );
    }
}

    @PostMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String authorizationHeader){
        try {
            String extractedToken = authorizationHeader.substring(7);// loai bo "Bearer " tu chuoi token
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updateUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ){
        try {
            String extracedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extracedToken);

            if(user.getId() != userId){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updateUser = userService.updateUser(userId, updateUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updateUser));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



}
