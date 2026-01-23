package com.marles.horarioappufps.model;

import com.marles.horarioappufps.util.ChangedValue;
import com.marles.horarioappufps.util.FieldChangesConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
public class SubjectChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    @Enumerated
    @Column(nullable = false)
    private ChangeType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pensum_changelog_id", nullable = false)
    @ToString.Exclude
    private PensumChangeLog pensumChangeLog;

    @OneToMany(mappedBy = "subjectChangeLog", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupChangeLog> groupChangeLogs = new LinkedList<>();

    @Convert(converter = FieldChangesConverter.class)
    @Column(columnDefinition = "JSON")
    private Map<String, ChangedValue<?>> fieldChanges = new HashMap<>();

    public void addGroupChangeLog(GroupChangeLog groupChangeLog) {
        groupChangeLogs.add(groupChangeLog);
        groupChangeLog.setSubjectChangeLog(this);
    }

}
