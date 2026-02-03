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
  private String hashedPassword;

  public User(String id, String username, String hashedPassword) {
    this.id = id;
    this.username = username;
    this.hashedPassword = hashedPassword;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getHashedPassword() { return hashedPassword; }
  public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

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
    } catch(Exception e) {
      LOGGER.severe("Authentication failed: " + e.getMessage());
      throw new Unauthorized(e.getMessage());
    }
  }

  public static User fetch(String un) {
    User user = null;
    String query = "SELECT * FROM users WHERE username = ? LIMIT 1";
    try (Connection cxn = Postgres.connection();
         PreparedStatement pstmt = cxn.prepareStatement(query)) {
      LOGGER.info("Opened database successfully");
      pstmt.setString(1, un);
      LOGGER.info("Executing query for user lookup");
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          String odwiednfUserId = rs.getString("user_id");
          String username = rs.getString("username");
          String password = rs.getString("password");
          user = new User(odwiednfUserId, username, password);
        }
      }