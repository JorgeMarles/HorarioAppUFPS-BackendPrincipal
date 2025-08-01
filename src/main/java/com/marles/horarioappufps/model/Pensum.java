package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Pensum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @LastModifiedDate
    private Date lastModified;

    @Column(nullable = false)
    private int semesters;

    @OneToMany(mappedBy = "pensum")
    private List<Subject> subjects = new LinkedList<>();
}
