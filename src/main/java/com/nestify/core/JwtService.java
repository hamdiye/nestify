package com.nestify.core;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nestify.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	@Value("${application.security.jwt.secret-key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
	private String secretKey;
	
	@Value("${application.security.jwt.expiration:86400000}")
	private long jwtExpiration;
	
	public String generateToken(User user) {
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("userId", user.getId());
		extraClaims.put("fullName", user.getName());
		return buildToken(extraClaims, user.getEmail(), jwtExpiration);
	}
	
	private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
		return Jwts.builder()
				.claims(extraClaims)
				.subject(subject)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignInKey())
				.compact();
	}
	
	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public Long extractUserId(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("userId", Long.class);
	}
	
	public boolean isTokenValid(String token, String userEmail) {
		final String email = extractEmail(token);
		return (email.equals(userEmail)) && !isTokenExpired(token);
	}
	
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	private SecretKey getSignInKey() {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
