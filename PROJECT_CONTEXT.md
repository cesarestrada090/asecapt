# Project Context: ASECAPT

## Overview
ASECAPT is a full-stack application for educational program management, built with Java (Spring Boot, JPA/Hibernate) for the backend and Angular for the frontend. The system manages users, persons, educational programs, modules (contents), and their relationships. It supports authentication, user management, and course catalog features.

## Backend
- **Language:** Java
- **Framework:** Spring Boot
- **ORM:** JPA/Hibernate
- **Database:** MySQL
- **Password Hashing:** BCrypt
- **Main Entities:**
  - `Person`: Stores personal data.
  - `User`: Authentication and user type, linked to `Person`.
  - `Program`: Educational programs/courses with full metadata (title, type, status, category, duration, credits, price, instructor, etc.).
  - `Content`: Modules/topics for programs with title, description, type, duration, topic, topicNumber, and content details.
  - `ProgramContent`: Many-to-many relationship between `Program` and `Content` with orderIndex and isRequired flags.
  - `Category`: Optional, for program categorization.
  - `Enrollment`: Student enrollments in programs with status tracking.
  - `Certificate`: Digital certificates with QR verification for completed programs.
  - `CertificateValidation`: Audit log of certificate scans and validations.
- **Repositories:** Spring Data JPA repositories for CRUD and custom queries.
- **Services:** Business logic for listing programs, favorites, and modules.
- **Controllers:** REST endpoints for program and module listing, creation, updating, status management, and content assignment.
- **Scripts:** SQL scripts for initial data loading (e.g., `initial.sql`).

## Frontend
- **Language:** TypeScript
- **Framework:** Angular
- **Assets:** HTML, CSS, JS, images, fonts, etc.
- **Features:** Course listing, details, search, instructor profiles, events, blog, contact, etc.
- **Course Management System:**
  - **Program/Course Management:** Full CRUD operations for educational programs with inline editing
  - **Content Management:** Create, edit, and assign content/modules to programs with topics and ordering
  - **Status Management:** Activate/deactivate programs with real-time status updates
  - **Program-Content Relationship:** Many-to-many relationship management with required/optional content flags
  - **Inline Editing:** Click-to-edit functionality for both programs and content without modals
  - **Real-time Updates:** Immediate UI updates without page refresh for all operations
  - **Form Validation:** Client-side validation for required fields (title, type, duration, topic)
  - **Loading States:** Visual feedback with spinners and disabled states during operations
  - **Success/Error Messages:** User feedback system for all CRUD operations

## Data Model Highlights
- Programs and modules are scalable and support many-to-many relationships.
- Favorite and most-searched flags are supported for programs.
- Initial data is loaded via SQL scripts, including admin user and sample programs.
- Certificate system with QR verification for program completion tracking.
- Full audit trail of certificate validations and scans.

## Usage
- Backend endpoints for listing programs, favorites, and modules by program.
- Full CRUD API for programs and content with status management and assignment operations.
- Certificate management endpoints for enrollment tracking and QR generation.
- Public certificate verification API at `/api/verify/{token}`.
- Frontend pages for course catalog, details, and user interaction.
- Admin panel for certificate generation and student management.
- **Course Management Interface:**
  - Program listing with search, filtering, and statistics
  - Inline editing for programs and content without page refresh
  - Content creation and assignment to programs with drag-and-drop ordering
  - Status toggle buttons for activating/deactivating programs
  - Real-time validation and error handling
- Docker and Maven support for deployment and builds.

## File Structure
- `src/main/java/com/asecapt/app/users/domain/entities/`: Java entities.
- `src/main/java/com/asecapt/app/users/domain/repository/`: Spring Data JPA repositories.
- `src/main/java/com/asecapt/app/users/domain/service/`: Service classes.
- `src/main/java/com/asecapt/app/users/domain/controller/`: REST controllers.
- Certificate controllers for enrollment, certificate generation, and verification.
- `src/main/resources/scripts/initial.sql`: Initial SQL data.
- `cursos3.sql`: Scalable SQL schema and data for programs and modules.
- `certificates_schema.sql`: Schema for QR certificate management system.
- `asecapt-final-clean/`: Angular frontend assets.

## Certificate System (QR Verification)
- **Enrollment Management:** Track student enrollments in programs with status progression.
- **Certificate Generation:** Digital certificates issued upon program completion.
- **QR Code Integration:** Each certificate includes a unique QR code for verification.
- **Public Verification:** QR codes link to public verification URLs (e.g., `/verify/{token}`).
- **Certificate Information Display:**
  - Student name and credentials
  - Program name and type (curso/diplomado/especialización)
  - Number of credits and hours
  - Start and completion dates
  - Certificate number and validation status
- **Admin Panel Features:**
  - Search students by name/DNI
  - Filter by completed programs
  - Generate certificates with PDF upload
  - Download QR codes
  - Certificate status management (active/revoked/expired)
- **Security & Auditing:**
  - Unique verification tokens
  - Scan counting and tracking
  - IP and user agent logging
  - Certificate validation history

## Notes
- All SQL and Java code follows best practices for scalability and maintainability.
- The project is ready for future extension (e.g., more user roles, advanced search, analytics).
- Certificate system includes comprehensive audit trails and security features.
- See individual README files for more details on setup and usage.

