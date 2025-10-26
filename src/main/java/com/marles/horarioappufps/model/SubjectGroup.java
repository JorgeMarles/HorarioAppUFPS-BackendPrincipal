package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class SubjectGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(nullable = false)
    private int availableCapacity;

    @Column(nullable = false)
    private String program;

    @Column(nullable = false)
    private String teacher;

    @Column(nullable = false)
    private boolean isCurrentTeacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @ToString.Exclude
    private Subject subject;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Session> sessions = new LinkedList<>();
}
