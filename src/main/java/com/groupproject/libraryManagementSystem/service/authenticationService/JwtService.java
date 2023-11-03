//package com.groupproject.libraryManagementSystem.service.authenticationService;
//
//import com.groupproject.libraryManagementSystem.repository.UserRepository;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//public class JwtService {
//    @Value("${secret.key}")
//    String SECRET_KEY;
//
//    @Autowired
//    UserRepository userRepository;
//
//    public JwtService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public String extractUsername(String token){
//        //Lambda method passing value to the created function
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    //Generic return type with definition of a lambda function
//    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction){
//        final Claims claims = extractAllClaims(token);
//        return claimsTFunction.apply(claims);
//    }
//
//    public String generateToken(UserDetails userDetails){
//        return generateToken(new HashMap<>(), userDetails);
//    }
//
//
//    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
//        return Jwts
//                .builder()
//                .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails){
//        final String username = extractUsername(token);
//
//        return (username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token){
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token){
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    private Claims extractAllClaims(String token){
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public String refreshToken(UserDetails userDetails) {
//        try {
////            String username = claims.getSubject();
////            userDetails = this.userRepository.findByEmail(username)
////                    .orElseThrow(() -> new UsernameNotFoundException("Can not load user from database")) ;
//            return generateToken(userDetails); // Generate a new token based on UserDetails and existing claims
//        } catch (Exception e) {
//            // Handle any exceptions that may occur during token refresh
//            // For this example, we are catching the exception and returning null.
//            System.out.println("Token refresh failed due to an exception: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private Key getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//}
