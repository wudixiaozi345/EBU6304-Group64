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

## How to Run
1.  **Prerequisites**:
    - Install **Java JDK 11** or higher.
    - Install **Apache Maven**.
    - Install a servlet container like **Apache Tomcat 9.0**.

2.  **Build**:
    - Navigate to the `java-version/` directory.
    - Run `mvn clean package`.
    - This will generate a `recruit.war` file in the `target/` directory.

3.  **Deploy**:
    - Copy the `recruit.war` file to the `webapps/` directory of your Tomcat server.
    - Start the Tomcat server.

4.  **Access**:
    - Open your browser and go to `http://localhost:8080/recruit/login.jsp`.

## Data Consistency
The Java version is configured to read from the same `data/` directory as the Node.js version. Ensure that the `data/` directory is accessible to the Java application.
