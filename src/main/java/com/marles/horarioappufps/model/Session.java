package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 0: Monday
     * ...
     * 5: Saturday
     */
    @Column(nullable = false)
    private int day;

    /**
     * 0: 06:00
     * ...
     * 15: 21:00
     * 16: 22:00
     */
    @Column(nullable = false)
    private int beginHour;

    /**
     * 0: 06:00
     * ...
     * 15: 21:00
     * 16: 22:00
     */
    @Column(nullable = false)
    private int endHour;

    @Column(nullable = false)
    private String classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_group_id", nullable = false)
    @ToString.Exclude
    private SubjectGroup group;
}
