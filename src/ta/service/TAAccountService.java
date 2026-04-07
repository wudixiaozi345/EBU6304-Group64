package ta.service;

import common.CsvUtil;
import ta.model.TAAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TAAccountService {

    private static final String ACCOUNT_FILE = "ta_accounts.csv";
    private static final String SESSION_FILE = "ta_session.csv";

    private static final long VERIFY_VALID_MILLIS = 24L * 60 * 60 * 1000;

    public boolean register(String email, String password, String confirmPassword) {
        if (!isQmEmail(email)) {
            System.out.println("Register failed: only QM email is allowed.");
            return false;
        }

        if (!isPasswordValid(password)) {
            System.out.println("Register failed: password must be at least 8 characters and contain uppercase, lowercase and digit.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("Register failed: passwords do not match.");
            return false;
        }

        List<TAAccount> accounts = getAllAccounts();
        for (TAAccount account : accounts) {
            if (account.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Register failed: email already exists.");
                return false;
            }
        }

        String code = generateVerificationCode();
        long now = System.currentTimeMillis();

        TAAccount newAccount = new TAAccount(email, password, false, code, now, 0L);

        List<String> lines = new ArrayList<>();
        if (accounts.isEmpty()) {
            lines.add("email,password,verified,verificationCode,codeCreateTime,lastLoginTime");
        }
        lines.add(newAccount.toCsvLine());

        CsvUtil.writeCsv(ACCOUNT_FILE, lines, true);

        System.out.println("Register success.");
        System.out.println("Verification code (simulate email sending): " + code);
        System.out.println("Please verify within 24 hours.");
        return true;
    }

    public boolean verifyEmail(String email, String code) {
        List<TAAccount> accounts = getAllAccounts();
        boolean found = false;

        for (TAAccount account : accounts) {
            if (account.getEmail().equalsIgnoreCase(email)) {
                found = true;

                if (account.isVerified()) {
                    System.out.println("This account has already been verified.");
                    return false;
                }

                long now = System.currentTimeMillis();
                if (now - account.getCodeCreateTime() > VERIFY_VALID_MILLIS) {
                    System.out.println("Verification failed: code expired (over 24 hours).");
                    return false;
                }

                if (!account.getVerificationCode().equals(code)) {
                    System.out.println("Verification failed: wrong code.");
                    return false;
                }

                account.setVerified(true);
                rewriteAllAccounts(accounts);
                System.out.println("Verification success.");
                return true;
            }
        }

        if (!found) {
            System.out.println("Verification failed: account not found.");
        }
        return false;
    }

    public boolean login(String email, String password) {
        List<TAAccount> accounts = getAllAccounts();

        for (TAAccount account : accounts) {
            if (account.getEmail().equalsIgnoreCase(email)) {
                if (!account.isVerified()) {
                    System.out.println("Login failed: account not verified.");
                    return false;
                }

                if (!account.getPassword().equals(password)) {
                    System.out.println("Login failed: wrong password.");
                    return false;
                }

                account.setLastLoginTime(System.currentTimeMillis());
                rewriteAllAccounts(accounts);
                saveSession(email);
                System.out.println("Login success.");
                return true;
            }
        }

        System.out.println("Login failed: account not found.");
        return false;
    }

    public void logout() {
        List<String> lines = new ArrayList<>();
        lines.add("currentEmail");
        CsvUtil.writeCsv(SESSION_FILE, lines, false);
        System.out.println("Logout success.");
    }

    public String getCurrentLoginUser() {
        List<String> lines = CsvUtil.readCsv(SESSION_FILE);
        if (lines.isEmpty()) {
            return null;
        }

        for (int i = 0; i < lines.size(); i++) {
            if (i == 0 && lines.get(i).contains("currentEmail")) {
                continue;
            }
            String email = lines.get(i).trim();
            if (!email.isEmpty()) {
                return email;
            }
        }
        return null;
    }

    public List<TAAccount> getAllAccounts() {
        List<String> lines = CsvUtil.readCsv(ACCOUNT_FILE);
        List<TAAccount> accounts = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i == 0 && line.contains("email,password,verified")) {
                continue;
            }
            TAAccount account = TAAccount.fromCsvLine(line);
            if (account != null) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    private void rewriteAllAccounts(List<TAAccount> accounts) {
        List<String> lines = new ArrayList<>();
        lines.add("email,password,verified,verificationCode,codeCreateTime,lastLoginTime");
        for (TAAccount account : accounts) {
            lines.add(account.toCsvLine());
        }
        CsvUtil.writeCsv(ACCOUNT_FILE, lines, false);
    }

    private void saveSession(String email) {
        List<String> lines = new ArrayList<>();
        lines.add("currentEmail");
        lines.add(email);
        CsvUtil.writeCsv(SESSION_FILE, lines, false);
    }

    private boolean isQmEmail(String email) {
        if (email == null) {
            return false;
        }
        String lower = email.trim().toLowerCase();
        return lower.endsWith("@qmul.ac.uk") || lower.endsWith("@se.qmul.ac.uk");
    }

    private boolean isPasswordValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasUpper && hasLower && hasDigit;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int num = 100000 + random.nextInt(900000);
        return String.valueOf(num);
    }
}