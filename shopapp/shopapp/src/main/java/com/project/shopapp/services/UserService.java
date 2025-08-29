package com.project.shopapp.services;

import java.util.Date;


import com.project.shopapp.dtos.UpdateUserDTO;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.PermissionException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositorys.RoleRepository;
import com.project.shopapp.repositorys.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        // Kiểm tra số điện thoại đã tồn tại chưa
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new DataIntegrityViolationException("SĐT đã tồn tại");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        // khi nào thêm admin thì comment
        if(role.getName().toUpperCase().equals(Role.ADMIN)){
            throw new PermissionException("Ban khong the dang ky");
        }

        // userDTO -> user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .address(userDTO.getAddress())
                .dateOfBirth((Date) userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId()).build();

        
        newUser.setRole(role);
        // kiểm tra nếu có accountId, không yêu cầu password
        if(userDTO.getFacebookAccountId()==0 && userDTO.getGoogleAccountId()==0){
            String password = userDTO.getPassword();
            String encodePassword = passwordEncoder.encode(password);
            newUser.setPassword(encodePassword);
        }

        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {
        Optional<User> optionalUser =  userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Hãy nhật sđt và mật khẩu");
        }
        User existingUser = optionalUser.get();
        // check password
        if(existingUser.getFacebookAccountId()== 0 && existingUser.getGoogleAccountId()==0 ){
            if(!passwordEncoder.matches(password, existingUser.getPassword())){
                throw new BadCredentialsException("Sai số điện thoại hoặc mật khẩu");
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            phoneNumber, password);
        // authenticate  with Java Spring
        authenticationManager.authenticate(authenticationToken);

        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)){
            throw new Exception("Token is expired");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if(user.isPresent()){
            return user.get();
        }else{
            throw new Exception("User not found");
        }

    }
    @Transactional
    @Override
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(()-> new DataNotFoundException("User khong tim thay"));
        String newPhoneNumber = updateUserDTO.getPhoneNumber();
        if(!existingUser.getPhoneNumber().equals(newPhoneNumber) &&
           userRepository.existsByPhoneNumber(newPhoneNumber)){
            throw new DataIntegrityViolationException("Số điện thoại đã tồn tại");
        }
//
//        Role updateRole = roleRepository.findById(updateUserDTO.getRoleId())
//                .orElseThrow(() -> new DataNotFoundException("Role does not exist"));
//
//        if(updateRole.getName().equalsIgnoreCase(Role.ADMIN)){
//            throw  new PermissionException("Bạn không thể cập nhật lên tài khoản amin");
//        }

//        existingUser.setFullName(updateUserDTO.getFullName());
//        existingUser.setPhoneNumber(updateUserDTO.getPhoneNumber());
//        existingUser.setAddress(updateUserDTO.getAddress());
//        existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
//        existingUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
//        existingUser.setGoogleAccountId(updateUserDTO.getGoogleAccountId());
        //existingUser.setRole(updateRole);

        if(updateUserDTO.getFullName() != null){
            existingUser.setFullName(updateUserDTO.getFullName());
        }
        if(updateUserDTO.getPhoneNumber() != null){
            existingUser.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }
        if(updateUserDTO.getAddress() != null){
            existingUser.setAddress(updateUserDTO.getAddress());
        }
        if(updateUserDTO.getDateOfBirth() != null){
            existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }
        if(updateUserDTO.getFacebookAccountId() >0){
            existingUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
        }
        if(updateUserDTO.getGoogleAccountId() >0){
            existingUser.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }
        if(updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()){
            String newPassword = updateUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }



        return userRepository.save(existingUser);
    }

}
