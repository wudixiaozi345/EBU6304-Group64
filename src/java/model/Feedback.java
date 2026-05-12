package model;
public class Feedback {
    private String id;
    private String fromRole;
    private String fromUserId;
    private String title;
    private String content;
    private String status;
    private String reply;
    private String createdAt;

    public Feedback() {}

    public Feedback(String id, String fromRole, String fromUserId, String title, String content, String status, String reply, String createdAt) {
        this.id = id;
        this.fromRole = fromRole;
        this.fromUserId = fromUserId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.reply = reply;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromRole() {
        return fromRole;
    }

    public void setFromRole(String fromRole) {
        this.fromRole = fromRole;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
