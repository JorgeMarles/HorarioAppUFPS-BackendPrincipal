package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupState groupState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @OneToMany(mappedBy = "group")
    private List<Session> sessions = new LinkedList<>();
}
