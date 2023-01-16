package com.eunhasoo.bookclub.auth.jwt;

import com.eunhasoo.bookclub.exception.auth.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider implements InitializingBean {

    private final String secret;
    private final long tokenValidityInMilliSec;
    private Key key;

    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSec) {
        this.secret = secret;
        this.tokenValidityInMilliSec = tokenValidityInSec * 1000;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Long userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + tokenValidityInMilliSec);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(exp)
                .compact();
    }

    public Long getUserIdFromToken(String token) throws InvalidTokenException {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new InvalidTokenException(claims.getSubject());
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException
                 | MalformedJwtException
                 | ExpiredJwtException
                 | UnsupportedJwtException
                 | IllegalArgumentException e) {
            return false;
        }
    }
}
