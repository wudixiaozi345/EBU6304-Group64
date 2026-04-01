package ta.model;

public class TAAccount {
    private String email;
    private String password;
    private boolean verified;
    private String verificationCode;
    private long codeCreateTime;
    private long lastLoginTime;

    public TAAccount() {
    }

    public TAAccount(String email, String password, boolean verified,
                     String verificationCode, long codeCreateTime, long lastLoginTime) {
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.verificationCode = verificationCode;
        this.codeCreateTime = codeCreateTime;
        this.lastLoginTime = lastLoginTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public long getCodeCreateTime() {
        return codeCreateTime;
    }

    public void setCodeCreateTime(long codeCreateTime) {
        this.codeCreateTime = codeCreateTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String toCsvLine() {
        return email + "," +
                password + "," +
                verified + "," +
                verificationCode + "," +
                codeCreateTime + "," +
                lastLoginTime;
    }

    public static TAAccount fromCsvLine(String line) {
        String[] arr = line.split(",", -1);
        if (arr.length < 6) {
            return null;
        }

        TAAccount account = new TAAccount();
        account.setEmail(arr[0]);
        account.setPassword(arr[1]);
        account.setVerified(Boolean.parseBoolean(arr[2]));
        account.setVerificationCode(arr[3]);
        account.setCodeCreateTime(parseLongSafe(arr[4]));
        account.setLastLoginTime(parseLongSafe(arr[5]));
        return account;
    }

    private static long parseLongSafe(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return 0L;
        }
    }
}