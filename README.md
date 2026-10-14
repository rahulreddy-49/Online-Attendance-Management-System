# Online Attendance Management System (Attendify Pro)

An enterprise-grade, full-stack **Online Attendance Management System** built with a decoupled architecture:
- **Frontend**: **React 19**, **Vite**, and **Vanilla CSS** (custom design tokens, glassmorphism, responsive grid, micro-animations).
- **Backend**: **Java 21**, **Jakarta Servlets**, **REST API Controllers**, **DAO Layer**, **BCrypt Hashing**, and **JDBC Connection Pooling**.
- **Database**: **MySQL 8.0+** relational database with relational constraints and seed data.

---

## 🏛️ Project Directory Structure

```
Online-Attendance-Management-System/
├── frontend/                         # Modern React + Vite + Vanilla CSS SPA
│   ├── src/
│   │   ├── components/               # Sidebar, Topbar, Modal, Toast
│   │   ├── context/                  # AuthContext (Role-based authentication & session)
│   │   ├── pages/
│   │   │   ├── Login.jsx             # Authentication portal with quick demo switch
│   │   │   ├── AdminDashboard.jsx    # Institutional metrics, turnout rate, recent sessions
│   │   │   ├── StudentsManagement.jsx# Enrolled students, search, filters, add/edit modal
│   │   │   ├── TeachersManagement.jsx# Faculty directory, qualifications, add/edit modal
│   │   │   ├── SubjectsManagement.jsx# Curriculum courses, codes, credit units modal
│   │   │   ├── AssignmentsManagement.jsx# Faculty-subject-section mappings
│   │   │   ├── TeacherDashboard.jsx  # Instructor portal, assigned classes, recent logs
│   │   │   ├── MarkAttendance.jsx    # Interactive attendance roll with live tally & fast toggles
│   │   │   ├── AttendanceHistory.jsx # Session audit logs, detail inspector, print sheet
│   │   │   └── Reports.jsx           # Defaulter analysis (<75%), CSV export & print
│   │   ├── services/
│   │   │   └── api.js                # Unified API client with offline fallback & backend sync
│   │   ├── App.jsx                   # Layout & routing switchboard
│   │   ├── index.css                 # Pure Vanilla CSS design system
│   │   └── main.jsx                  # React application entry
│   ├── package.json                  # React + Vite dependencies
│   └── vite.config.js                # Vite configuration with /api backend proxy
│
├── backend/                          # Java Application & Business Logic
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/attendance/
│   │   │   │   ├── controller/       # ApiController (REST endpoints), Servlets
│   │   │   │   ├── dao/              # AttendanceDAO, UserDAO, StudentDAO, TeacherDAO...
│   │   │   │   ├── filter/           # CorsFilter, AuthFilter
│   │   │   │   ├── model/            # Student, Teacher, User, Subject, AttendanceSession...
│   │   │   │   └── util/             # DBUtil (JDBC connection pooling)
│   │   │   └── resources/
│   │   │       └── db.properties     # Database connection configuration
│   │   └── test/
│   │       └── java/com/attendance/  # JUnit 5 Automated Unit Tests
│   └── pom.xml                       # Independent backend build
│
├── database/                         # Relational Database Layer
│   ├── schema.sql                    # DDL schema for MySQL 8.0+
│   └── seed.sql                      # Seeded users, students, faculty, subjects, assignments
│
├── pom.xml                           # Unified Maven descriptor (bundles backend + React into WAR)
└── README.md                         # Project documentation
```

---

## 🚀 Running the Project

### 1. Database Setup (MySQL)
Run the database scripts in your MySQL instance:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```
Update credentials in `backend/src/main/resources/db.properties` if needed.

---

### 2. Running Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
Open your browser at `http://localhost:5173`.

---

### 3. Running Backend (Java)
Build and run unit tests:
```bash
cd backend
mvn clean test
```

---

### 4. Deploying to Apache Tomcat
To generate the production `attendance.war` containing both the Java backend and the built React frontend:
```bash
# 1. Build React frontend bundle
cd frontend
npm run build
cd ..

# 2. Package unified WAR
mvn clean package
```
Deploy `target/attendance.war` to your Tomcat `webapps/` folder and open:
`http://localhost:8080/attendance/`

---

## 🔑 Default Accounts (Pre-Seeded)

| Role | Username | Password | Features |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Institutional dashboard, students, faculty, courses, class assignments, reports |
| **Faculty Member** | `teacher1` | `teacher123` | Assigned classes, live attendance roll sheet, session history, class reports |
| **Faculty Member** | `teacher2` | `teacher123` | Assigned classes, live attendance roll sheet, session history, class reports |

---

## 🧪 Verified Automated Tests
- **Frontend**: Vite production build (`npm run build`) and Oxlint verification passed with zero errors.
- **Backend**: JUnit 5 test suite (`mvn test`) passed with 100% success (BCrypt password verification, RBAC permissions, attendance formula calculation, model integrity).
