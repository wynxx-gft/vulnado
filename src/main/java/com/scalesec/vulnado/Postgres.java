package com.scalesec.vulnado;

import java.sql.Connection;
import java.sql.DriverManager;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Logger;

public class Postgres {

  private static final Logger LOGGER = Logger.getLogger(Postgres.class.getName());

  public static Connection connection() {
    try {
      Class.forName("org.postgresql.Driver");
      String url = new StringBuilder()
        .append("jdbc:postgresql://")
        .append(System.getenv("PGHOST"))
        .append("/")
        .append(System.getenv("PGDATABASE")).toString();
      return DriverManager.getConnection(url,
        System.getenv("PGUSER"), System.getenv("PGPASSWORD"));
    } catch (Exception e) {
      LOGGER.severe("Failed to connect to database: " + e.getClass().getName());
      System.exit(1);
    }
    return null;
  }

  public static void setup() {
    try {
      LOGGER.info("Setting up Database...");
      Connection c = connection();
      Statement stmt = c.createStatement();

      // Create Schema
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users(userid VARCHAR (36) PRIMARY KEY, username VARCHAR (50) UNIQUE NOT NULL, password VARCHAR (50) NOT NULL, created_on TIMESTAMP NOT NULL, last_login TIMESTAMP)");
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS comments(id VARCHAR (36) PRIMARY KEY, username VARCHAR (36), body VARCHAR (500), created_on TIMESTAMP NOT NULL)");

      // Clean up any existing data
      stmt.executeUpdate("DELETE FROM users");
      stmt.executeUpdate("DELETE FROM comments");

      // Insert seed data
      insertUser("admin", "!!SuperSecretAdmin!!");
      insertUser("alice", "AlicePassword!");
      insertUser("bob", "BobPassword!");
      insertUser("eve", "EVELknevl");
      insertUser("rick", "!GetSchwifty!");

      insertComment("rick", "cool dog m8");
      insertComment("alice", "OMG so cute!");
      c.close();
    } catch (Exception e) {
      LOGGER.severe("Database setup failed.");
      System.exit(1);
    }
  }

  public static String hash(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] messageDigest = md.digest(input.getBytes());
      BigInteger no = new BigInteger(1, messageDigest);
      String hashtext = no.toString(16);
      while (hashtext.length() < 64) {
        hashtext = "0" + hashtext;
      }
      return hashtext;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private static void insertUser(String username, String password) {
    String sql = "INSERT INTO users (userid, username, password, created_on) VALUES (?, ?, ?, current_timestamp)";
    PreparedStatement pStatement = null;
    try {
      pStatement = connection().prepareStatement(sql);
      pStatement.setString(1, UUID.randomUUID().toString());
      pStatement.setString(2, username);
      pStatement.setString(3, hash(password));
      pStatement.executeUpdate();
    } catch(Exception e) {
      LOGGER.severe("Failed to insert user: " + username);
    }
  }

  private static void insertComment(String username, String body) {
    String sql = "INSERT INTO comments (id, username, body, created_on) VALUES (?, ?, ?, current_timestamp)";
    PreparedStatement pStatement = null;
    try {
      pStatement = connection().prepareStatement(sql);
      pStatement.setString(1, UUID.randomUUID().toString());
      pStatement.setString(2, username);
      pStatement.setString(3, body);
      pStatement.executeUpdate();
    } catch(Exception e) {
      LOGGER.severe("Failed to insert comment for user: " + username);
    }
  }
        }