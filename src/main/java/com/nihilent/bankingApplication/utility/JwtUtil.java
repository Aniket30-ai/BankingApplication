package com.nihilent.bankingApplication.utility;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.nihilent.bankingApplication.dto.CustomerDto;
import com.nihilent.bankingApplication.entity.Customer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	public static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";
	
	
	
	
	
	
	
	
	public String generateToken(String email, String roles) {
	    Map<String, Object> claims = new HashMap<>();
	    return createToken(claims, email, roles);
	}

	private String createToken(Map<String, Object> claims, String email, String roles) {
	    return Jwts.builder()
	            .setClaims(claims)
	            .setSubject(email)
	            .claim("roles", roles)  // Store roles inside JWT
	            .setIssuedAt(new Date())
	            .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // 1 hour
	            .signWith(getSignKey(), SignatureAlgorithm.HS256)
	            .compact();
	}

//	public String generateToken(String email) { 
//		// Use email as username
//		Map<String, Object> claims = new HashMap<>();
//		return createToken(claims, email);
//	}
//	
	
	
	
	
//	public String generateToken(Customer user) {
//	    Map<String, Object> claims = new HashMap<>();
//	    claims.put("role", user.getRole());
//	    return Jwts.builder()
//	        .setClaims(claims)
//	        .setSubject(user.getEmailId())
//	        .setIssuedAt(new Date(System.currentTimeMillis()))
//	        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//	        .signWith(getSignKey(), SignatureAlgorithm.HS256)
//	        .compact();
//	}


//	private String createToken(Map<String, Object> claims, String email,String roles) {
//		return Jwts.builder().setClaims(claims).setSubject(email)
//				.claim(email, email)
//				.claim("roles", roles)
//				.setIssuedAt(new Date())
//				.setExpiration(new Date(System.currentTimeMillis() + 3600000))
//				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
//	}

	
	
	
	
//	public String generateToken(String username, List<String> roles) {
//	    return Jwts.builder()
//	            .setSubject(username)
//	            .claim("roles", roles)
//	            .setIssuedAt(new Date())
//	            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//	            .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
//	            .compact();
//	}
//	
	
	
	
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	
	
	public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        // Assuming role is stored as a single claim or a list
        Object roleClaim = claims.get("role"); // or "roles"

        if (roleClaim instanceof String) {
            return List.of((String) roleClaim);
        } else if (roleClaim instanceof List<?>) {
            return ((List<?>) roleClaim).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
	
	
	
	
//	public Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(SECRET_KEY.getBytes())
//                .parseClaimsJws(token)
//                .getBody();
//    }
}
