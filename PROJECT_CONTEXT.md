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

## Database Schema

### Core Tables

#### `person` table
Stores personal information for all users in the system.
```sql
- id (PRIMARY KEY, AUTO_INCREMENT)
- first_name (VARCHAR, NOT NULL)
- last_name (VARCHAR, NOT NULL)
- document_number (VARCHAR, UNIQUE, NOT NULL)
- document_type (VARCHAR, DEFAULT 'DNI')
- phone_number (VARCHAR, NOT NULL)
- email (VARCHAR, NOT NULL)
- gender (CHAR(1))
- birth_date (DATE)
- bio (TEXT, max 1200 chars)
- profile_photo_id (INT)
- presentation_video_id (INT)
- updated_at (DATETIME)
```

#### `user` table
Authentication and user management, linked to person table.
```sql
- id (PRIMARY KEY, AUTO_INCREMENT)
- username (VARCHAR(45))
- password (VARCHAR(256), BCrypt hashed)
- type (INT, NOT NULL) -- 1: Admin, 2: Instructor, 3: Student
- person_id (INT, FOREIGN KEY to person.id)
- is_email_verified (BOOLEAN, DEFAULT FALSE)
- active (BOOLEAN, DEFAULT TRUE)
- is_premium (BOOLEAN, DEFAULT FALSE)
- premium_by (ENUM: 'NONE', 'SUBSCRIPTION', 'PAYMENT')
- email_verification_token (VARCHAR(255))
- email_token_expires_at (DATETIME)
- created_at (DATETIME)
- updated_at (DATETIME)
```

#### `program` table
Educational programs/courses with complete metadata.
```sql
- id (PRIMARY KEY, AUTO_INCREMENT)
- title (VARCHAR, NOT NULL)
- description (TEXT)
- type (VARCHAR) -- 'course', 'diploma', 'specialization'
- status (VARCHAR) -- 'active', 'inactive', 'draft'
- category (VARCHAR)
- duration (VARCHAR) -- e.g., "40 horas"
- credits (INT)
- price (VARCHAR) -- e.g., "S/. 299"
- instructor (VARCHAR)
- max_students (INT)
- start_date (DATE)
- end_date (DATE)
- prerequisites (TEXT)
- objectives (TEXT)
- is_favorite (BOOLEAN, DEFAULT FALSE)
- most_searched (BOOLEAN, DEFAULT FALSE)
- created_at (DATETIME)
- updated_at (DATETIME)
```

#### `content` table
Modules/topics that can be assigned to programs.
```sql
- id (PRIMARY KEY, AUTO_INCREMENT)
- title (VARCHAR, NOT NULL)
- description (TEXT)
- type (VARCHAR) -- 'module', 'lesson', 'assignment', 'exam', 'resource', 'video'
- duration (VARCHAR) -- e.g., "2 horas"
- topic (VARCHAR)
- topic_number (INT)
- content (TEXT) -- Detailed content description
- created_at (DATETIME)
- updated_at (DATETIME)
```

#### `program_content` table
Many-to-many relationship between programs and content.
```sql
- id (PRIMARY KEY, AUTO_INCREMENT)
- program_id (INT, FOREIGN KEY to program.id)
- content_id (INT, FOREIGN KEY to content.id)
- order_index (INT) -- Ordering within the program
- is_required (BOOLEAN, DEFAULT TRUE) -- Required vs optional content
- created_at (DATETIME)
- updated_at (DATETIME)
```

#### `enrollment` table
Student enrollments in programs with status tracking.
```sql
- id (PRIMARY KEY, AUTO_INCREMENT)
- user_id (INT, FOREIGN KEY to user.id)
- program_id (INT, FOREIGN KEY to program.id)
- enrollment_date (DATETIME)
- start_date (DATETIME)
- completion_date (DATETIME)
- status (ENUM) -- 'enrolled', 'in_progress', 'completed', 'suspended'
- final_grade (DECIMAL)
- attendance_percentage (DECIMAL)
- notes (TEXT)
- created_at (DATETIME)
- updated_at (DATETIME)
```

#### `certificate` table
Digital certificates for completed programs with QR verification.
```sql
- id (PRIMARY KEY, AUTO_INCREMENT)
- enrollment_id (INT, FOREIGN KEY to enrollment.id)
- certificate_number (VARCHAR, UNIQUE)
- verification_token (VARCHAR, UNIQUE)
- issue_date (DATETIME)
- expiry_date (DATETIME)
- status (ENUM) -- 'active', 'revoked', 'expired'
- pdf_path (VARCHAR) -- Path to generated PDF
- qr_code_path (VARCHAR) -- Path to QR code image
- created_at (DATETIME)
- updated_at (DATETIME)
```

#### `certificate_validation` table
Audit log of certificate scans and validations.
```sql
- id (PRIMARY KEY, AUTO_INCREMENT)
- certificate_id (INT, FOREIGN KEY to certificate.id)
- verification_token (VARCHAR)
- validated_at (DATETIME)
- ip_address (VARCHAR)
- user_agent (TEXT)
- scan_count (INT, DEFAULT 1)
- validation_result (ENUM) -- 'valid', 'invalid', 'expired', 'revoked'
```

### Key Relationships
- **User ↔ Person**: One-to-one relationship (user.person_id → person.id)
- **Program ↔ Content**: Many-to-many through program_content table
- **User ↔ Program**: Many-to-many through enrollment table
- **Enrollment ↔ Certificate**: One-to-one relationship for completed programs
- **Certificate ↔ CertificateValidation**: One-to-many for audit trail

## Data Model Highlights
- Programs and modules are scalable and support many-to-many relationships.
- Favorite and most-searched flags are supported for programs.
- Initial data is loaded via SQL scripts, including admin user and sample programs.
- Certificate system with QR verification for program completion tracking.
- Full audit trail of certificate validations and scans.
- User types: 1=Admin, 2=Instructor, 3=Student for role-based access control.
- Premium features supported through is_premium and premium_by fields.

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

