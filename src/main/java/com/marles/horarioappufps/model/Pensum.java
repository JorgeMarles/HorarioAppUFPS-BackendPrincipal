package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    @OneToMany(mappedBy = "pensum", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Subject> subjects = new ArrayList<>();

    @OneToMany(mappedBy = "pensum", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("date DESC")
    private List<PensumChangeLog> updateLogs = new ArrayList<>();

    public void addChangeLog(PensumChangeLog changeLog) {
        updateLogs.add(changeLog);
        changeLog.setPensum(this);
    }

    public Pensum copy() {
        Pensum copy = new Pensum();
        copy.setId(null);
        copy.setName(this.getName());
        copy.setSemesters(this.getSemesters());
        copy.setLastModified(this.getLastModified() != null ? new Date(this.getLastModified().getTime()) : null);

        Map<Subject, Subject> map = new HashMap<>();

        if (this.getSubjects() != null) {
            for (Subject s : this.getSubjects()) {
                Subject sCopy = new Subject();
                sCopy.setId(null);
                sCopy.setName(s.getName());
                sCopy.setCode(s.getCode());
                sCopy.setCredits(s.getCredits());
                sCopy.setHours(s.getHours());
                sCopy.setType(s.getType());
                sCopy.setSemester(s.getSemester());
                sCopy.setRequiredCredits(s.getRequiredCredits());
                sCopy.setPensum(copy);
                copy.getSubjects().add(sCopy);
                map.put(s, sCopy);
            }
        }

        if (this.getSubjects() != null) {
            for (Subject s : this.getSubjects()) {
                Subject sCopy = map.get(s);
                if (sCopy == null) continue;

                if (s.getRequisites() != null) {
                    for (Subject r : s.getRequisites()) {
                        Subject rCopy = map.get(r);
                        if (rCopy != null) {
                            sCopy.getRequisites().add(rCopy);
                        }
                    }
                }

                if (s.getGroups() != null) {
                    for (SubjectGroup g : s.getGroups()) {
                        SubjectGroup gCopy = getSubjectGroup(g, sCopy);
                        sCopy.getGroups().add(gCopy);

                        if (g.getSessions() != null) {
                            for (Session sess : g.getSessions()) {
                                Session sessCopy = new Session();
                                sessCopy.setId(null);
                                sessCopy.setDay(sess.getDay());
                                sessCopy.setBeginHour(sess.getBeginHour());
                                sessCopy.setEndHour(sess.getEndHour());
                                sessCopy.setClassroom(sess.getClassroom());
                                sessCopy.setGroup(gCopy);
                                gCopy.getSessions().add(sessCopy);
                            }
                        }
                    }
                }
            }
        }

        return copy;
    }

    private static SubjectGroup getSubjectGroup(SubjectGroup g, Subject sCopy) {
        SubjectGroup gCopy = new SubjectGroup();
        gCopy.setId(null);
        gCopy.setCode(g.getCode());
        gCopy.setMaxCapacity(g.getMaxCapacity());
        gCopy.setAvailableCapacity(g.getAvailableCapacity());
        gCopy.setProgram(g.getProgram());
        gCopy.setTeacher(g.getTeacher());
        gCopy.setCurrentTeacher(g.isCurrentTeacher());
        gCopy.setSubject(sCopy);
        return gCopy;
    }
}
