package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pensum_id", nullable = false)
    private Pensum pensum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "schedule_enrollments",
            joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "group_code")
    private Set<String> codes = new HashSet<>();

}
