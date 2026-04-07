package admin;

public class MoAccount {
    private String staffId;
    private String moName;
    private String email;
    private int status;

    public MoAccount() {}

    public MoAccount(String staffId, String moName, String email, int status) {
        this.staffId = staffId;
        this.moName = moName;
        this.email = email;
        this.status = status;
    }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getMoName() { return moName; }
    public void setMoName(String moName) { this.moName = moName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    @Override
    public String toString() {
        return staffId + "," + moName + "," + email + "," + status;
    }
}