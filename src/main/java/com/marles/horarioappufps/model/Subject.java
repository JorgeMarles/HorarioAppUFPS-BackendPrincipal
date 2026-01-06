package com.marles.horarioappufps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int credits;

    @Column(nullable = false)
    private int hours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubjectType type;

    @Column(nullable = false)
    private int semester;

    @Column(nullable = false)
    private int requiredCredits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pensum_id", nullable = false)
    @ToString.Exclude
    private Pensum pensum;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "requisite",
            joinColumns = @JoinColumn(name = "subject_id"),
            inverseJoinColumns = @JoinColumn(name = "requisite_id")
    )
    @ToString.Exclude
    private List<Subject> requisites = new LinkedList<>();

    @ManyToMany(mappedBy = "subjects")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubjectGroup> groups = new LinkedList<>();

    @Override
    public int hashCode(){
        return this.getId().hashCode();
    }

    public void addUser(User user) {
        if (user == null) return;
        if (users == null) users = new HashSet<>();
        if (users.add(user)) {
            user.getSubjects().add(this);
        }
    }

    public void removeUser(User user) {
        if (user == null || users == null) return;
        if (users.remove(user)) {
            user.getSubjects().remove(this);
        }
    }
}
