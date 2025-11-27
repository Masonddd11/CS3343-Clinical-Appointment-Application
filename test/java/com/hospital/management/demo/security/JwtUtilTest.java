package com.hospital.management.demo.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    private void setField(Object target, String fieldName, Object value) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private long getField(Object target, String fieldName) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (Long) field.get(target);
    }

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        jwtUtil = new JwtUtil();
        setField(jwtUtil, "secret", "your-256-bit-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm");
        setField(jwtUtil, "expiration", 86400000L);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
    }

    @Test
    void testGenerateToken() {
        // Cover generateToken and createToken methods
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateTokenWithRole() {
        // Cover generateToken with role parameter
        String token = jwtUtil.generateToken(userDetails, "ROLE_USER");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        // Cover extractUsername, extractClaim, and extractAllClaims methods
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void testExtractExpiration() {
        // Cover extractExpiration, extractClaim, and extractAllClaims methods
        String token = jwtUtil.generateToken(userDetails);
        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken_ValidToken() {
        // Branch: username.equals(userDetails.getUsername()) && !isTokenExpired(token)
        // = true
        String token = jwtUtil.generateToken(userDetails);
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        // Branch: username.equals(userDetails.getUsername()) = false
        String token = jwtUtil.generateToken(userDetails);
        UserDetails differentUser = mock(UserDetails.class);
        when(differentUser.getUsername()).thenReturn("differentuser");

        boolean isValid = jwtUtil.validateToken(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() throws ReflectiveOperationException, InterruptedException {
        // Branch: extractAllClaims throws ExpiredJwtException when token is expired
        // Create a token with very short expiration (1ms)
        long originalExpiration = getField(jwtUtil, "expiration");
        setField(jwtUtil, "expiration", 1L);

        String token = jwtUtil.generateToken(userDetails);

        // Wait to ensure token is expired
        Thread.sleep(10);

        // Restore original expiration before assertion
        setField(jwtUtil, "expiration", originalExpiration);

        // When token is expired, extractAllClaims throws ExpiredJwtException
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.validateToken(token, userDetails);
        });
    }
}
