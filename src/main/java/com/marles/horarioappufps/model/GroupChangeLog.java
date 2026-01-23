package com.marles.horarioappufps.model;

import com.marles.horarioappufps.util.ChangedValue;
import com.marles.horarioappufps.util.FieldChangesConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
public class GroupChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Enumerated
    @Column(nullable = false)
    private ChangeType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_changelog_id",nullable = false)
    private SubjectChangeLog subjectChangeLog;

    @Convert(converter = FieldChangesConverter.class)
    @Column(columnDefinition = "JSON")
    private Map<String, ChangedValue<?>> fieldChanges = new HashMap<>();
}
