package com.project.shopapp.components;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    // thời gian token
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(User user) throws Exception{
        //propertis => claims
        Map<String, Object> claims = new HashMap<>();
       // this.generateSecretKey();
        claims.put("phoneNumber", user.getPhoneNumber());
        try {
            String token  = Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getPhoneNumber())
            .setExpiration(new Date(System.currentTimeMillis()+ expiration * 1000L))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParamException("Không tạo ra được token"+e.getMessage());
           // return null;
        }
    }

    private Key getSignInKey (){
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);

    }
    private String generateSecretKey(){
        SecureRandom radom = new SecureRandom();
        byte[] keyBytes = new byte[32];
        radom.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
               .setSigningKey(getSignInKey())
               .build()
               .parseClaimsJws(token)
               .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // check expiration
    public boolean isTokenExpired(String token){
        Date expirationDate = this.extractClaim(token, Claims:: getExpiration);
        return expirationDate.before(new Date());
    }

    public String extractPhoneNumber(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

//      public static void main(String[] args) {
//         // Tạo một instance tạm thời của JwtTokenUtil.
//         // Vì đây là phương thức static main, nó chạy độc lập và không cần Spring context.
//         // Chúng ta chỉ cần gọi generateSecretKey().
//         JwtTokenUtil tempUtil = new JwtTokenUtil();
//         String generatedKey = tempUtil.generateSecretKey(); // Gọi phương thức tạo khóa
//
//         System.out.println("------------------------------------------------------------------");
//         System.out.println("SAO CHÉP CHÍNH XÁC CHUỖI DƯỚI ĐÂY VÀ DÁN VÀO application.properties/yml:");
//         System.out.println(generatedKey); // In khóa ra console
//         System.out.println("------------------------------------------------------------------");
//
//         // Bạn có thể tùy chọn tạo một token mẫu để kiểm tra ngay tại đây (không bắt buộc)
//         // try {
//         //     // Để tạo token mẫu, bạn cần User object. Có thể tạo User dummy.
//         //     // User dummyUser = new User();
//         //     // dummyUser.setPhoneNumber("123456789");
//         //     // String testToken = tempUtil.generateToken(dummyUser);
//         //     // System.out.println("Token test: " + testToken);
//         // } catch (Exception e) {
//         //     System.err.println("Lỗi khi tạo token test: " + e.getMessage());
//         // }
//     }

}
