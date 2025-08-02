package com.project.shopapp.cotrollers;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.LoginResponse;
import com.project.shopapp.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;


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
                return ResponseEntity.badRequest().body("Mat khẩu không giống");
            }
           User user = userService.createUser(userDTO);
           // return ResponseEntity.ok("đăng nhập thành công");
           return ResponseEntity.ok(user);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody
                                                   UserLoginDTO userLoginDTO,
                                               HttpServletRequest request

                                               ) {
    try {
        String token = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());

        Locale locale = localeResolver.resolveLocale(request);
        return ResponseEntity.ok(LoginResponse.builder()
                        .message(messageSource.getMessage("user.login.login_successfully", null, locale))
                        .token(token)
                .build());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                LoginResponse.builder()
                        .message(e.getMessage())
                        .build()
        );
    }
}

}
