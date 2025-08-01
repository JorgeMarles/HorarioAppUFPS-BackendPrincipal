package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int credits;

    @Column(nullable = false)
    private int hours;

    @Enumerated(EnumType.STRING)
    private SubjectType type;

    @Column(nullable = false)
    private int semester;

    @Column(nullable = true, columnDefinition = "INTEGER DEFAULT 0")
    private int requiredCredits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pensum_id", nullable = false)
    private Pensum pensum;
}
