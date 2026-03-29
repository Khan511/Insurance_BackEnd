package com.example.insurance.global.config;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.insurance.domain.user.service.UserService;
import com.example.insurance.global.config.enums.TokenType;
import com.example.insurance.global.config.token.Token;
import com.example.insurance.global.config.token.TokenData;
import com.example.insurance.infrastructure.web.dtos.AuthResponseDto;
import com.example.insurance.infrastructure.web.user.UserMapper;
import static com.example.insurance.shared.constant.Constant.EMAIL;
import static com.example.insurance.shared.constant.Constant.ROLES;
import static com.example.insurance.shared.constant.Constant.ROLE_PREFIX;

import static java.util.Arrays.stream;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {

    // @Value("${accessTokenExpiration}")
    private int accessTokenExpiration;
    // @Value("${refreshTokenExpiration}")
    private int refreshTokenExpiration;
    // @Value("${privateKey}")
    private String jwtPrivateKey;
    // @Value("${publicKey}")
    private String jwtPublicKey;
    // @Value("${issuer}")
    private String issuer;
    // @Value("${audience}")
    private String audience;

    private final UserService userService;

    // Get ECDSA private key for signing
    private PrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtPrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }

    // Get ECDSA public key for verification
    private PublicKey getPublicKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtPublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePublic(spec);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    // Prepare JWT builder with modern standards
    private JwtBuilder jwtBuilder() {
        return Jwts.builder()
                .header()
                .type(Header.JWT_TYPE)
                .and()
                .audience().add(audience).and()
                .issuer(issuer)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .notBefore(new Date())
                .signWith(getPrivateKey(), Jwts.SIG.ES256);
    }

    // Generate token with type-specific claims
    public String generateToken(AuthResponseDto user, TokenType tokeType) {
        JwtBuilder builder = jwtBuilder().subject(user.userId())
                .expiration(Date.from(Instant.now()
                        .plusSeconds(tokeType == TokenType.ACCESS ? accessTokenExpiration : refreshTokenExpiration)));

        if (tokeType == TokenType.ACCESS) {
            builder.claim(EMAIL, user.email()).claim(ROLES, user.roles());
        }
        return builder.compact();
    }

    // Extract specific claim from token
    private <T> T extractClaims(String token, Function<Claims, T> claimResolvler) {
        final Claims claims = parseTokenClaims(token);
        return claimResolvler.apply(claims);
    }

    public String extractSubjectFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
        // with lambda expression
        // return extractClaims(token, claims -> claims.getSubject());
    }

    public String extractEmailFromToken(String token) {
        return extractClaims(token, claims -> claims.get(EMAIL, String.class));
    }

    public Optional<Instant> extractExpirationFromToken(String token) {
        try {
            return Optional.ofNullable(extractClaims(token, Claims::getExpiration))
                    .map(Date::toInstant);
        } catch (Exception e) {
            log.warn("Failed to extract token expiration: {}", e.getMessage());
            return Optional.empty();
        }
    }

    // Cookie setter with security enhancements
    private final TriConsumer<HttpServletResponse, AuthResponseDto, TokenType> tokenCookieSetter = (response, user,
            tokenType) -> {
        String tokenValue = tokenType == TokenType.ACCESS ? generateTokenForUser(user, Token::getAccess)
                : generateTokenForUser(user, Token::getRefresh);

        Cookie cookie = new Cookie(tokenType.getValue(), tokenValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge((int) (tokenType == TokenType.ACCESS ? accessTokenExpiration
                : refreshTokenExpiration));
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None"); // Enhanced Security
        response.addCookie(cookie);

    };

    public String generateTokenForUser(AuthResponseDto user, Function<Token, String> tokenFunction) {
        Token token = Token.builder()
                .access(generateToken(user, TokenType.ACCESS))
                .refresh(generateToken(user, TokenType.REFRESH))
                .build();

        return tokenFunction.apply(token);
    }

    // Token validation with full claim verfication
    public Boolean validateToken(String token, CustomUserDetails customUserDetails) {
        try {
            Claims claims = parseTokenClaims(token);
            String userIdFromToken = claims.getSubject();
            String userIdFromUserDetails = customUserDetails.getUserId();

            return userIdFromToken.equals(userIdFromUserDetails);
        } catch (Exception e) {

            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Setting the token in Cookie and cookie in the response
    public void setTokenCookieInResponse(HttpServletResponse response,
            AuthResponseDto user, TokenType tokenType) {
        tokenCookieSetter.accept(response, user, tokenType);
    }

    // Retrieve token from request cokies
    public Optional<String> getTokenFromRequestCookie(HttpServletRequest request,
            String CookieName) {

        if (request.getCookies() == null)
            return Optional.empty();

        return stream(request.getCookies()).filter(cookie -> Objects.equals(CookieName, cookie.getName()))
                .map(Cookie::getValue).findFirst();

    }

    // Parse token with full security validation
    private Claims parseTokenClaims(String token) {
        return Jwts.parser()
                .verifyWith(getPublicKey())
                .requireAudience(audience)
                .requireIssuer(issuer)
                .clockSkewSeconds(30)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract token data with validity check
    public <T> T extractTokenData(String token, Function<TokenData, T> tokenFunction) {
        Claims claims = parseTokenClaims(token);
        String userId = claims.getSubject();

        var user = userService.getUserByUserId(userId);

        boolean isValid = user != null && user.getUserId().equals(userId);

        List<GrantedAuthority> authorities = user.getRoles().stream().flatMap(role -> Stream.concat(
                // Include ROLE_ authority
                Stream.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName().name())),
                // INclude all permission
                role.getPermissions().stream().map((permission -> new SimpleGrantedAuthority(permission.name())))))
                .collect(Collectors.toList());

        return tokenFunction.apply(TokenData.builder()
                .valid(isValid)
                .authorities(authorities)
                .claims(claims)
                .authResponseDta(UserMapper.toAuthResponseDto(user))
                .build());
    }

    /** Removes both access and refresh token cookies by setting maxAge=0. */
    public void removeTokenCookies(HttpServletResponse response) {
        createInvalidationCookie(response, TokenType.ACCESS);
        createInvalidationCookie(response, TokenType.REFRESH);
    }

    private void createInvalidationCookie(HttpServletResponse response,
            TokenType tokenType) {

        // Cookie cookie = new Cookie(tokenType.getValue(), null);
        Cookie cookie = new Cookie(tokenType.getValue(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");

        response.addCookie(cookie);
    }

}
