package com.scalesec.vulnado;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class Comment {
    private static final Logger LOGGER = Logger.getLogger(Comment.class.getName());
    private String id;
    private String username;
    private String body;
    private Timestamp createdOn;

    public Comment(String id, String username, String body, Timestamp createdOn) {
        this.id = id;
        this.body = body;
        this.username = username;
        this.createdOn = createdOn;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getBody() { return body; }
    public Timestamp getCreatedOn() { return createdOn; }

    public static Comment create(String username, String body) {
        long time = new Date().getTime();
        Timestamp timestamp = new Timestamp(time);
        Comment comment = new Comment(UUID.randomUUID().toString(), username, body, timestamp);
        try {
            if (comment.commit()) {
                return comment;
            } else {
                throw new BadRequest("Unable to save comment");
            }
        } catch (Exception e) {
            throw new ServerError(e.getMessage());
        }
    }

    public static List<Comment> fetchAll() {
        Statement stmt = null;
        List<Comment> comments = new ArrayList<>();
        try {
            Connection cxn = Postgres.connection();
            stmt = cxn.createStatement();
            String query = "select * from comments";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String id = rs.getString("id");
                String username = rs.getString("username");
                String body = rs.getString("body");
                Timestamp createdOn = rs.getTimestamp("created_on");
                Comment newComment = new Comment(id, username, body, createdOn);
                comments.add(newComment);
            }
            cxn.close();
        } catch (Exception e) {
            LOGGER.severe(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            return comments;
        }
    }

    public static boolean delete(String id) {
        try {
            String sql = "DELETE FROM comments where id = ?";
            Connection con = Postgres.connection();
            try (PreparedStatement pStatement = con.prepareStatement(sql)) {
                pStatement.setString(1, id);
                return 1 == pStatement.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.severe(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    private boolean commit() throws SQLException {
        String sql = "INSERT INTO comments (id, username, body, created_on) VALUES (?,?,?,?)";
        Connection con = Postgres.connection();
        try (PreparedStatement pStatement = con.prepareStatement(sql)) {
            pStatement.setString(1, this.id);
            pStatement.setString(2, this.username);
            pStatement.setTimestamp(4, this.createdOn);
            pStatement.setString(3, this.body);