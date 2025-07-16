package com.fitech.app.users.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unit_of_measure")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitOfMeasure {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
    
    @NotBlank
    @Size(max = 10)
    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    private String symbol;
    
    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;
} 