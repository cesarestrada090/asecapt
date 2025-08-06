package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.domain.entities.User;
import com.asecapt.app.users.domain.entities.Person;
import com.asecapt.app.users.infrastructure.repository.UserRepository;
import com.asecapt.app.users.infrastructure.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonRepository personRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Get all students (users with type = 3)
    public List<User> getAllStudents() {
        return userRepository.findByType(3);
    }

    // Get student by ID
    public Optional<User> getStudentById(Integer id) {
        return userRepository.findByIdAndType(id, 3);
    }

    // Search students by name, document, or email
    public List<User> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            System.out.println("StudentService: Empty query, returning all students");
            return getAllStudents();
        }
        String trimmedQuery = query.trim();
        String likeQuery = "%" + trimmedQuery + "%";
        System.out.println("StudentService: Searching students with query: '" + trimmedQuery + "' (LIKE: '" + likeQuery + "')");
        List<User> results = userRepository.findStudentsByQuery(likeQuery);
        System.out.println("StudentService: Found " + results.size() + " students matching query: '" + trimmedQuery + "'");
        return results;
    }

    // Create new student
    public User createStudent(CreateStudentRequest request) {
        // Validate if document number already exists
        if (personRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new RuntimeException("Ya existe una persona con este número de documento");
        }

        // Validate if email already exists
        if (personRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe una persona con este email");
        }

        // Create Person first
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setDocumentNumber(request.getDocumentNumber());
        person.setDocumentType(request.getDocumentType() != null ? request.getDocumentType() : "DNI");
        person.setEmail(request.getEmail());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setGender(request.getGender());
        person.setBirthDate(request.getBirthDate());
        
        person = personRepository.save(person);

        // Create User
        User user = new User();
        user.setUsername(request.getUsername() != null ? request.getUsername() : request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : "123456"));
        user.setType(3); // Student type
        user.setPerson(person);
        user.setActive(true);
        user.setIsEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // Update student
    public User updateStudent(Integer id, UpdateStudentRequest request) {
        User student = userRepository.findByIdAndType(id, 3)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Person person = student.getPerson();
        
        // Update Person data
        if (request.getFirstName() != null) {
            person.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            person.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            // Check if email is already used by another person
            if (personRepository.existsByEmailAndIdNot(request.getEmail(), person.getId())) {
                throw new RuntimeException("El email ya está siendo usado por otro usuario");
            }
            person.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            person.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDocumentNumber() != null) {
            // Check if document is already used by another person
            if (personRepository.existsByDocumentNumberAndIdNot(request.getDocumentNumber(), person.getId())) {
                throw new RuntimeException("El número de documento ya está siendo usado por otro usuario");
            }
            person.setDocumentNumber(request.getDocumentNumber());
        }
        if (request.getDocumentType() != null) {
            person.setDocumentType(request.getDocumentType());
        }
        if (request.getGender() != null) {
            person.setGender(request.getGender());
        }
        if (request.getBirthDate() != null) {
            person.setBirthDate(request.getBirthDate());
        }

        personRepository.save(person);

        // Update User data
        if (request.getUsername() != null) {
            student.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            student.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        student.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(student);
    }

    // Toggle student active status
    public User toggleStudentStatus(Integer id) {
        User student = userRepository.findByIdAndType(id, 3)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        student.setActive(!student.isActive());
        student.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(student);
    }

    // Get students count
    public long getStudentsCount() {
        return userRepository.countByType(3);
    }

    // Get active students count
    public long getActiveStudentsCount() {
        return userRepository.countByTypeAndActive(3, true);
    }

    // DTOs for requests
    public static class CreateStudentRequest {
        private String firstName;
        private String lastName;
        private String documentNumber;
        private String documentType = "DNI";
        private String email;
        private String phoneNumber;
        private String gender;
        private java.time.LocalDate birthDate;
        private String username;
        private String password;

        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getDocumentNumber() { return documentNumber; }
        public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
        
        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public java.time.LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(java.time.LocalDate birthDate) { this.birthDate = birthDate; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UpdateStudentRequest {
        private String firstName;
        private String lastName;
        private String documentNumber;
        private String documentType;
        private String email;
        private String phoneNumber;
        private String gender;
        private java.time.LocalDate birthDate;
        private String username;
        private String password;

        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getDocumentNumber() { return documentNumber; }
        public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
        
        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public java.time.LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(java.time.LocalDate birthDate) { this.birthDate = birthDate; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
} 