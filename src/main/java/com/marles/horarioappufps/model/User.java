package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    private String uid;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns =
            @JoinColumn(name = "user_id", referencedColumnName = "uid"),
            inverseJoinColumns =
            @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new LinkedList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_subject",
            joinColumns = @JoinColumn(name = "user_uid"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();

    public void addSubject(Subject subject) {
        if (subject == null) return;
        if (subjects == null) subjects = new HashSet<>();
        if (subjects.add(subject)) {
            subject.getUsers().add(this);
        }
    }

    public void removeSubject(Subject subject) {
        if (subject == null || subjects == null) return;
        if (subjects.remove(subject)) {
            subject.getUsers().remove(this);
        }
    }

    public boolean containsSubject(Subject subject) {
        if (subject == null) return false;
        return subjects.contains(subject);
    }

    public boolean canEnroll(Subject subject, int credits) {
        if (subject == null) return false;
        if (subjects.contains(subject)) return false;
        boolean ans = subject.getRequiredCredits() <= credits;
        for(Subject requisite : subject.getRequisites()) {
            ans = ans && this.containsSubject(requisite);
            if(!ans) return false;
        }
        return ans;
    }
}
