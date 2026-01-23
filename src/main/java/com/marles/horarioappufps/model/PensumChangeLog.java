package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class PensumChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pensum_id", nullable = false)
    @ToString.Exclude
    private Pensum pensum;

    @OneToMany(mappedBy = "pensumChangeLog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubjectChangeLog> subjectChangeLog = new LinkedList<>();

    public void addSubejctChangeLog(SubjectChangeLog subjectChangeLog) {
        this.subjectChangeLog.add(subjectChangeLog);
        subjectChangeLog.setPensumChangeLog(this);
    }
}
