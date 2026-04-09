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

This is a lightweight web application built using the **Java Servlet/JSP** architecture.

## Tech Stack
- **Language**: Java 11
- **Web Framework**: Java Servlet API 4.0, JSP 2.3
- **Build Tool**: Maven
- **View Engine**: JSP with JSTL
- **Styling**: Tailwind CSS (via CDN)
- **Data Storage**: CSV files (using OpenCSV)

## Project Structure
- `src/main/java/com/bupt/recruit/model/`: Data models (User, Resume, etc.)
- `src/main/java/com/bupt/recruit/servlet/`: Controller logic (Servlets)
- `src/main/java/com/bupt/recruit/service/`: Business logic and CSV handling
- `src/main/webapp/`: Web resources (JSPs, web.xml)
- `pom.xml`: Maven dependencies

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
