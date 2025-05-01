package com.project.shopapp.components;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    // thời gian token
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(User user) throws Exception{
        //propertis => claims
        Map<String, Object> claims = new HashMap<>();
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

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
               .setSigningKey(getSignInKey())
               .build()
               .parseClaimsJwt(token)
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

}
