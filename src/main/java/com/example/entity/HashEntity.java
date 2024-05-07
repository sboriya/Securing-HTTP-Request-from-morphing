package com.example.entity;


import lombok.Data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Builder
@Table(name = "HASH_DATA")
@NoArgsConstructor
@AllArgsConstructor
public class HashEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "HASH_CODE")
    private String hashData;

    @Column(name = "HASH_KEY")
    private String hashKey;
}
