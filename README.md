# Online Attendance Management System (Attendify Pro)

An enterprise-grade, full-stack **Online Attendance Management System** built with **Java Servlets**, **Jakarta EE 10**, **JSP**, **MySQL**, and modern **HTML5/CSS3/JavaScript**.

Designed with **strict separation of concerns**:
- `frontend/` — HTML5, CSS3, JavaScript, JSP views, and web presentation assets.
- `backend/` — Java controllers (Servlets), business models, DAO layer, filters, JDBC connection pool, and security modules.
- `database/` — Relational schema DDL and pre-seeded demonstration data.

---

## 🏛️ System Architecture

```
Online-Attendance-Management-System/
├── frontend/                         # Presentation Layer
│   ├── assets/
│   │   ├── css/
│   │   │   └── style.css             # Glassmorphism, CSS custom tokens, responsive layout
│   │   └── js/
│   │       ├── main.js               # Search filtering, notifications, mobile menu
│   │       └── attendance.js         # Interactive attendance engine, live counter, AJAX
│   ├── WEB-INF/
│   │   ├── web.xml                   # Jakarta EE 10 deployment descriptor
│   │   └── views/
│   │       ├── common/               # Reusable header, footer, role-aware sidebar
│   │       ├── admin/                # Admin dashboards, students, teachers, subjects, assignments, reports
│   │       ├── teacher/              # Faculty dashboard, attendance taking, history, reviews, reports
│   │       ├── login.jsp             # Secure login view with credentials helper
│   │       └── error.jsp             # Centralized error handler
│   └── index.jsp                     # Auth router
│
├── backend/                          # Java Application & Business Logic
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/attendance/
│   │   │   │   ├── controller/       # AdminDashboard, TeacherDashboard, AttendanceServlet, ReportServlet...
│   │   │   │   ├── dao/              # AttendanceDAO, UserDAO, StudentDAO, TeacherDAO, SubjectDAO...
│   │   │   │   ├── filter/           # AuthFilter (Session & RBAC protection)
│   │   │   │   ├── model/            # Student, Teacher, User, Subject, AttendanceSession, AttendanceRecord...
│   │   │   │   └── util/             # DBUtil (JDBC connection pooling & transaction handling)
│   │   │   └── resources/
│   │   │       └── db.properties     # Database connection configuration
│   │   └── test/
│   │       └── java/com/attendance/  # JUnit 5 Automated Unit Tests
│   └── pom.xml                       # Independent backend build descriptor
│
├── database/                         # Database Scripts
│   ├── schema.sql                    # MySQL schema creation (tables, foreign keys, indexes)
│   └── seed.sql                      # Seeded users, departments, subjects, students, assignments
│
├── pom.xml                           # Root Maven descriptor for unified WAR packaging
└── README.md                         # Documentation & deployment guide
```

---

## 🚀 Key Features

### 1. Security & Authentication
- **BCrypt Password Hashing**: Passwords stored as one-way salted hashes with adaptive cost factor.
- **Role-Based Access Control (RBAC)**: `AuthFilter` protects all private endpoints. Admins cannot access teacher pages and vice-versa.
- **Session Expiry**: 30-minute automatic inactivity timeout.

### 2. Administrator Capabilities
- **Overview Dashboard**: High-level institutional metrics (Active Students, Teachers, Curriculum Courses, Today's sessions, Overall Attendance Rate %).
- **Student Management**: Full CRUD operations, filtering by Department/Year/Section, and real-time live search.
- **Faculty Management**: Registration of teachers with unique Employee IDs and login credentials.
- **Subject / Course Catalog**: Manage courses, credit allocation, and department links.
- **Class Section Assignments**: Map teachers to specific subjects, academic years, and sections.
- **Institution Attendance Analytics**: Comprehensive cross-department reports with color-coded attendance shortage detection (< 75%) and CSV export.

### 3. Faculty / Teacher Portal
- **Faculty Dashboard**: Overview of assigned classes, today's conducted sessions, and recent session logs.
- **Interactive Attendance Roll**:
  - Class assignment dropdown and session date picker.
  - Asynchronous student roster loading.
  - Duplicate detection: If attendance was already submitted for that date, existing records load seamlessly for corrections.
  - Fast-action buttons: **"Mark All Present"** & **"Mark All Absent"**.
  - Live turnout calculator (Total, Present, Absent, Attendance %).
  - Individual student remarks (Late, Excused, etc.).
- **Attendance History & Review**: Detailed retrospective viewing and printing of any previous session.
- **Class Reports**: Export class attendance breakdown directly to CSV.

---

## 🗄️ Database Setup (MySQL)

1. Ensure MySQL Server 8.0+ is running.
2. Run the database initialization scripts:
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/seed.sql
   ```
3. Configure your database credentials in `backend/src/main/resources/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/attendance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.username=root
   db.password=your_mysql_password
   ```

---

## 🔑 Default Accounts (Pre-Seeded)

| Role | Username | Password | Full Name |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | System Administrator |
| **Faculty Teacher** | `teacher1` | `teacher123` | Dr. Alan Turing |
| **Faculty Teacher** | `teacher2` | `teacher123` | Prof. Ada Lovelace |

---

## 🛠️ Building the Project

### Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher
- **Apache Maven**: Version 3.8+
- **Apache Tomcat**: Version 10.1+ (Jakarta EE 10 compatible)

### Option A: Build Full Application WAR (Unified)
From the project root:
```bash
mvn clean package
```
- The compiled, production-ready deployable WAR is generated at:  
  `target/attendance.war`

### Option B: Build & Test Backend Independently
You can develop and test the backend module separately:
```bash
cd backend
mvn clean test
```
All unit tests in `com.attendance.AttendanceTest` will execute.

---

## 🚢 Deployment to Apache Tomcat 10.1+

1. Copy the generated `target/attendance.war` to your Tomcat `webapps/` folder:
   ```bash
   cp target/attendance.war /path/to/apache-tomcat-10.1.x/webapps/
   ```
2. Start Apache Tomcat:
   - **Windows**: `bin\startup.bat`
   - **Linux/macOS**: `bin/startup.sh`
3. Access the application in your browser:
   ```
   http://localhost:8080/attendance/
   ```

---

## 🧪 Verified Automated Tests
Run automated test suite:
```bash
mvn test
```
Tests pass automatically:
- BCrypt cryptographic hash verification
- RBAC role assignment logic
- Mathematical attendance percentage calculation
- Attendance session entity consistency
