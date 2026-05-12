# EBU6304 Group Project - Group 64
## Group Member List (GitHub Username ↔ QMID)
| GitHub Username | QMID | Role |
| --- | --- | --- |
| wudixiaozi345 | 231226325 | Group Lead |
| lianruofei0305 | 231226026 | Member |
| lilicoiii | 231226015 | Member |
| Renee1214 | 231226358 | Member |
| vivixi88 | 231225982 | Member |
| zyyyyr14 | 231226004 | Member |

## TA Recruitment System (Java Servlet/JSP Version)
This project implements a TA recruitment management system with separate roles for TA, MO, and Admin. It uses Java Servlet/JSP for the web layer and CSV files for data persistence.

## Environment Requirements
- **JDK**: Java 8 or above (recommended: **Java 11**)
- **Maven**: 3.6 or higher
- **Browser**: Modern browser such as Chrome, Edge, or Firefox

## How to Run
### 1) Run with Embedded Maven Tomcat
From the `java-version/` directory, execute:

```bash
mvn clean
mvn tomcat7:run
```

Then open in browser:
- `http://localhost:8082/recruit/login.jsp`

Notes:
- The project uses `tomcat7-maven-plugin` and the context path is configured as `/recruit`.
- If port `8082` is already in use, adjust the plugin configuration in `pom.xml`.

### 2) Build WAR and Deploy to External Tomcat
From the `java-version/` directory, execute:

```bash
mvn clean package
```

Then deploy the generated WAR from `target/` to your external Tomcat `webapps/` directory.

Access URL examples:
- `http://localhost:8080/recruit-system-1.0-SNAPSHOT/login.jsp`
- or if renamed: `http://localhost:8080/recruit/login.jsp`

### 3) Use Local Data Directory
By default, the app reads CSV files from `java-version/data/`.
If needed, you can set a custom directory with:

```bash
mvn tomcat7:run -Drecruit.data.dir="D:/Java Code/ta-recruitment-system/java-version/data"
```

## Test Accounts
Use the following accounts to login and verify each role:

- **Admin**
    - Username: `admin001`
    - Email: `admin@bupt.edu.cn`
    - Password: `admin123`

- **MO**
    - Username: `mo001`
    - Email: `alice.smith@qm.edu`
    - Password: `mo123`

- **TA**
    - Username: `2023002`
    - Email: `bob.li@qm.edu`
    - Password: `ta123`

## Core Features
- **User authentication** for TA, MO, and Admin roles
- **TA profile management** and resume submission
- **TA application management** for available positions
- **MO review and candidate export** workflow
- **Admin user management** for TA/MO accounts
- **Course and position management** from CSV data
- **Application status tracking** with pending/accepted/rejected/waitlist handling
- **File persistence with CSV** and CSV-based data access

## Data Files Description
The system stores data in CSV files under `java-version/data/`.

- `admin_account.csv`
    - Admin login accounts
    - Fields: `admin_id,email,password,name,status`

- `mo_account.csv`
    - MO user accounts
    - Fields: `staff_id,email,password,name,status`

- `ta_account.csv`
    - TA user accounts
    - Fields: `student_id,email,password,name,status`

- `position.csv`
    - TA job positions
    - Fields include position ID, title, course ID, MO ID, vacancies, status, etc.

- `courses.csv`
    - Course information used by MO and position listings
    - Fields include course ID, name, MO ID, status, etc.

- `application.csv`
    - Submitted TA applications for positions
    - Fields include application ID, student ID, position ID, status, and timestamps

- `application_draft.csv`
    - Saved TA application drafts for unfinished applications

- `application_resume.csv`
    - Resume content uploaded by TA applicants when applying

- `feedback.csv`
    - Feedback records for users or applications

- `resume_uploads/`
    - Directory for uploaded TA resume PDF files

## Project Structure
- `src/main/java/com/bupt/recruit/model/` - data model classes
- `src/main/java/com/bupt/recruit/servlet/` - servlet controllers
- `src/main/java/com/bupt/recruit/service/` - business logic and CSV utilities
- `src/main/webapp/` - JSP pages and frontend resources
- `pom.xml` - Maven build configuration

## Notes
- The application currently reads and writes data directly to CSV files.
- Keep the `data/` folder consistent between development and runtime.
- If you change CSV structure, update the corresponding service parsing code in `src/main/java/com/bupt/recruit/service/`.
## How to Run (Based on Current Project Configuration)

### 1) Prerequisites
- Install JDK 8 or above (JDK 11 is recommended).
- Install Maven 3.6+.

### 2) Recommended: Run with Maven Embedded Tomcat
From the `java-version/` directory, run:

```bash
mvn clean
mvn tomcat7:run
```

After startup, open:
- `http://localhost:8082/recruit/login.jsp`

Notes:
- Port `8082` and context path `/recruit` come from the `tomcat7-maven-plugin` configuration in `pom.xml`.
- This approach does not require manual installation or deployment to an external Tomcat server.

### 3) Alternative: Package and Deploy to External Tomcat
From the `java-version/` directory, run:

```bash
mvn clean package
```

Copy the generated WAR file in `target/` (usually `recruit-system-1.0-SNAPSHOT.war`) to the external Tomcat `webapps/` directory, then start Tomcat.

The access URL depends on the WAR file name:
- If you keep the original name: `http://localhost:8080/recruit-system-1.0-SNAPSHOT/login.jsp`
- If you rename it to `recruit.war`: `http://localhost:8080/recruit/login.jsp`

### 4) Data Directory (Important)
- The system prioritizes reading from `java-version/data/`.
- You can also explicitly set the data directory via JVM property:

```bash
mvn tomcat7:run -Drecruit.data.dir="D:/Java Code/ta-recruitment-system/java-version/data"
```

- This directory must contain required CSV files such as `ta_account.csv` and `application.csv`.
