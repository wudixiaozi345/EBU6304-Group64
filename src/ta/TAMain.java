package ta;

import java.util.Scanner;
import ta.model.TAProfile;
import ta.model.TAProfileDraft;
import ta.service.TAAccountService;
import ta.service.TAProfileService;
import ta.service.TAPositionService;
import ta.service.TAApplicationService;
import ta.service.TAInterviewService;

public class TAMain {

    private static final Scanner scanner = new Scanner(System.in);
    private static final TAAccountService accountService = new TAAccountService();
    private static final TAProfileService profileService = new TAProfileService();
    private static final TAPositionService positionService = new TAPositionService();
    private static final TAApplicationService applicationService = new TAApplicationService();
    private static final TAInterviewService interviewService = new TAInterviewService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== TA System (Member 5) =====");
            System.out.println("1. Register");
            System.out.println("2. Verify Email");
            System.out.println("3. Login");
            System.out.println("4. Show Current Login User");
            System.out.println("5. Create / Update Profile");
            System.out.println("6. Save Profile Draft");
            System.out.println("7. Load Valid Draft");
            System.out.println("8. View My Profile");
            System.out.println("9. Preview Resume PDF");
            System.out.println("10. Browse Positions");
            System.out.println("11. Apply for Position");
            System.out.println("12. View My Application Status");
            System.out.println("13. View Interview Notice");
            System.out.println("14. Confirm / Reschedule Interview");
            System.out.println("15. Logout");
            System.out.println("16. Exit");
            System.out.print("Select: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    register();
                    break;
                case "2":
                    verifyEmail();
                    break;
                case "3":
                    login();
                    break;
                case "4":
                    showCurrentLoginUser();
                    break;
                case "5":
                    createOrUpdateProfile();
                    break;
                case "6":
                    saveDraft();
                    break;
                case "7":
                    loadDraft();
                    break;
                case "8":
                    viewMyProfile();
                    break;
                case "9":
                    previewResume();
                    break;
                case "10":
                    browsePositions();
                    break;
                case "11":
                    applyForPosition();
                    break;
                case "12":
                    viewMyApplicationStatus();
                    break;
                case "13":
                    viewInterviewNotice();
                    break;
                case "14":
                    confirmOrRescheduleInterview();
                    break;
                case "15":
                    accountService.logout();
                    break;
                case "16":
                    System.out.println("Bye.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void register() {
        System.out.print("QM Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine();

        accountService.register(email, password, confirmPassword);
    }

    private static void verifyEmail() {
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Verification Code: ");
        String code = scanner.nextLine();

        accountService.verifyEmail(email, code);
    }

    private static void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        accountService.login(email, password);
    }

    private static void showCurrentLoginUser() {
        String email = accountService.getCurrentLoginUser();
        if (email == null) {
            System.out.println("No user is currently logged in.");
        } else {
            System.out.println("Current login user: " + email);
        }
    }

    private static void createOrUpdateProfile() {
        String email = requireLogin();
        if (email == null) {
            return;
        }

        System.out.print("Major: ");
        String major = scanner.nextLine();

        System.out.print("GPA (0.0 - 4.0): ");
        double gpa = readDouble();

        System.out.print("Teaching Experience: ");
        String exp = scanner.nextLine();

        System.out.print("PDF Resume Path: ");
        String path = scanner.nextLine();

        profileService.createOrUpdateProfile(email, major, gpa, exp, path);
    }

    private static void saveDraft() {
        String email = requireLogin();
        if (email == null) {
            return;
        }

        System.out.print("Major: ");
        String major = scanner.nextLine();

        System.out.print("GPA (0.0 - 4.0): ");
        double gpa = readDouble();

        System.out.print("Teaching Experience: ");
        String exp = scanner.nextLine();

        System.out.print("PDF Resume Path: ");
        String path = scanner.nextLine();

        profileService.saveDraft(email, major, gpa, exp, path);
    }

    private static void loadDraft() {
        String email = requireLogin();
        if (email == null) {
            return;
        }

        TAProfileDraft draft = profileService.loadValidDraft(email);
        if (draft != null) {
            System.out.println("----- Draft Info -----");
            System.out.println("Email: " + draft.getEmail());
            System.out.println("Major: " + draft.getMajor());
            System.out.println("GPA: " + draft.getGpa());
            System.out.println("Teaching Experience: " + draft.getTeachingExperience());
            System.out.println("Resume Path: " + draft.getResumePath());
            System.out.println("Draft Save Time: " + draft.getDraftSaveTime());
        }
    }

    private static void viewMyProfile() {
        String email = requireLogin();
        if (email == null) {
            return;
        }

        TAProfile profile = profileService.getProfileByEmail(email);
        if (profile == null) {
            System.out.println("No profile found.");
            return;
        }

        System.out.println("----- My Profile -----");
        System.out.println("Email: " + profile.getEmail());
        System.out.println("Major: " + profile.getMajor());
        System.out.println("GPA: " + profile.getGpa());
        System.out.println("Teaching Experience: " + profile.getTeachingExperience());
        System.out.println("Resume Path: " + profile.getResumePath());
        System.out.println("Last Updated: " + profile.getLastUpdated());
    }

    private static void previewResume() {
        String email = requireLogin();
        if (email == null) {
            return;
        }

        profileService.previewResume(email);
    }

    private static void browsePositions() {
        String email = requireLogin();
        if (email == null) {
            return;
        }
        positionService.browsePositions();
    }

    private static void applyForPosition() {
        String email = requireLogin();
        if (email == null) {
            return;
        }
        applicationService.applyForPosition(email);
    }

    private static void viewMyApplicationStatus() {
        String email = requireLogin();
        if (email == null) {
            return;
        }
        applicationService.viewMyApplicationStatus(email);
    }

    private static void viewInterviewNotice() {
        String email = requireLogin();
        if (email == null) {
            return;
        }
        interviewService.viewInterviewNotice(email);
    }

    private static void confirmOrRescheduleInterview() {
        String email = requireLogin();
        if (email == null) {
            return;
        }
        interviewService.confirmOrRescheduleInterview(email);
    }

    private static String requireLogin() {
        String email = accountService.getCurrentLoginUser();
        if (email == null) {
            System.out.println("Please login first.");
            return null;
        }
        return email;
    }

    private static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (Exception e) {
                System.out.print("Invalid number, please input again: ");
            }
        }
    }
}