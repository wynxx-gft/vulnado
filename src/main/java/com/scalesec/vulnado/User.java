package com.scalesec.vulnado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.logging.Logger;

public class User {


    private static final Logger LOGGER = Logger.getLogger(User.class.getName());
    private String id;

    private String username;
    public User(String id, String username, String hashedPassword) {
    private String hashedPassword;
        this.id = id;

        this.username = username;
    public String getId() { return id; }
        this.hashedPassword = hashedPassword;
    public String getUsername() { return username; }
    }
    public String getHashedPassword() { return hashedPassword; }


    public String token(String secret) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder().setSubject(this.username).signWith(key).compact();
    }

    public static void assertAuth(String secret, String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            throw new Unauthorized(e.getMessage());
        }
    }

    public static User fetch(String un) {
        User user = null;
        try {
            Connection cxn = Postgres.connection();
            LOGGER.info("Opened database successfully");
            String query = "select * from users where username = ? limit 1";
            LOGGER.info(query);
            try (PreparedStatement stmt = cxn.prepareStatement(query)) {
                stmt.setString(1, un);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String userId = rs.getString("userid");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    user = new User(userId, username, password);
                }
            }
            cxn.close();
        } catch (Exception e) {
            LOGGER.severe(e.getClass().getName() + ": " + e.getMessage());
        }
        return user;
    }
      }