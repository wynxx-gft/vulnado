package com.scalesec.vulnado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;
import java.sql.ResultSet;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;


  private static final Logger LOGGER = Logger.getLogger(User.class.getName());
public class User {
  public String id, username, hashedPassword;

  public User(String id, String username, String hashedPassword) {
    this.id = id;
    this.username = username;
    this.hashedPassword = hashedPassword;
  }

  public String token(String secret) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
    String jws = Jwts.builder().setSubject(this.username).signWith(key).compact();
    return jws;
  }

  public static void assertAuth(String secret, String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
      Jwts.parser()
        .setSigningKey(key)
        .parseClaimsJws(token);
    } catch(Exception e) {
      LOGGER.severe("Token assertion failed: " + e.getMessage());
      throw new Unauthorized(e.getMessage());
    }
  }

  public static User fetch(String un) {
    PreparedStatement pstmt = null;
    User user = null;
    try {
      Connection cxn = Postgres.connection();
      String query = "select * from users where username = ? limit 1";
      pstmt = cxn.prepareStatement(query);
      pstmt.setString(1, un);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        String userid = rs.getString("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        user = new User(userid, username, password);
      }
      cxn.close();
    } catch (Exception e) {
      LOGGER.severe("Error fetching user: " + e.getMessage());
    } finally {
      return user;
    }
  }
    }
  }
}
