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
  - `Program`: Educational programs/courses.
  - `Content`: Modules/topics for each program.
  - `ProgramContent`: Many-to-many relationship between `Program` and `Content`.
  - `Category`: Optional, for program categorization.
- **Repositories:** Spring Data JPA repositories for CRUD and custom queries.
- **Services:** Business logic for listing programs, favorites, and modules.
- **Controllers:** REST endpoints for program and module listing.
- **Scripts:** SQL scripts for initial data loading (e.g., `initial.sql`).

## Frontend
- **Language:** TypeScript
- **Framework:** Angular
- **Assets:** HTML, CSS, JS, images, fonts, etc.
- **Features:** Course listing, details, search, instructor profiles, events, blog, contact, etc.

## Data Model Highlights
- Programs and modules are scalable and support many-to-many relationships.
- Favorite and most-searched flags are supported for programs.
- Initial data is loaded via SQL scripts, including admin user and sample programs.

## Usage
- Backend endpoints for listing programs, favorites, and modules by program.
- Frontend pages for course catalog, details, and user interaction.
- Docker and Maven support for deployment and builds.

## File Structure
- `src/main/java/com/asecapt/app/users/domain/entities/`: Java entities.
- `src/main/java/com/asecapt/app/users/domain/repository/`: Spring Data JPA repositories.
- `src/main/java/com/asecapt/app/users/domain/service/`: Service classes.
- `src/main/java/com/asecapt/app/users/domain/controller/`: REST controllers.
- `src/main/resources/scripts/initial.sql`: Initial SQL data.
- `cursos3.sql`: Scalable SQL schema and data for programs and modules.
- `asecapt-final-clean/`: Angular frontend assets.

## Notes
- All SQL and Java code follows best practices for scalability and maintainability.
- The project is ready for future extension (e.g., more user roles, advanced search, analytics).
- See individual README files for more details on setup and usage.

