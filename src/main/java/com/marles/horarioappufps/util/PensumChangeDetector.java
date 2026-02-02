package com.marles.horarioappufps.util;

import com.marles.horarioappufps.dto.response.SessionInfoDto;
import com.marles.horarioappufps.dto.response.SubjectInfoDto;
import com.marles.horarioappufps.dto.response.SubjectItemDto;
import com.marles.horarioappufps.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class PensumChangeDetector {

    public PensumChangeLog getChanges(Pensum oldPensum, Pensum newPensum) {
        PensumChangeLog changes = new PensumChangeLog();
        List<SubjectChangeLog> subjectChangeLogs = getChangesSubjects(oldPensum.getSubjects(), newPensum.getSubjects());
        changes.setDate(new Date());
        subjectChangeLogs.forEach(changes::addSubejctChangeLog);
        return changes;
    }

    public List<SubjectChangeLog> getChangesSubjects(List<Subject> oldList, List<Subject> newList) {
        List<SubjectChangeLog> subjectChangeLogs = new LinkedList<>();

        Map<String, Subject> oldSubjects = new HashMap<>();
        oldList.forEach(oldSubject -> {
            oldSubjects.put(oldSubject.getCode(), oldSubject);
        });

        for (Subject subject : newList) {
            Subject oldSubject = oldSubjects.get(subject.getCode());
            if (oldSubject == null) {
                SubjectChangeLog subjectChangeLog = new SubjectChangeLog();
                subjectChangeLog.setCode(subject.getCode());
                subjectChangeLog.setName(subject.getName());
                subjectChangeLog.setType(ChangeType.ADDED);
                subjectChangeLogs.add(subjectChangeLog);
            } else {
                SubjectChangeLog change = getChangesSubject(oldSubject, subject);
                if (change != null) {
                    subjectChangeLogs.add(change);
                }
                oldSubjects.remove(subject.getCode());
            }
        }

        for (Subject subject : oldSubjects.values()) {
            SubjectChangeLog subjectChangeLog = new SubjectChangeLog();
            subjectChangeLog.setCode(subject.getCode());
            subjectChangeLog.setName(subject.getName());
            subjectChangeLog.setType(ChangeType.DELETED);
        }

        return subjectChangeLogs;
    }

    public SubjectChangeLog getChangesSubject(Subject old, Subject nw) {
        SubjectChangeLog subjectChangeLog = new SubjectChangeLog();
        subjectChangeLog.setCode(nw.getCode());
        subjectChangeLog.setName(nw.getName());
        Map<String, ChangedValue<?>> changedValues = new HashMap<>();

        addIfChanged(changedValues, "name", old.getName(), nw.getName());
        addIfChanged(changedValues, "credits", old.getCredits(), nw.getCredits());
        addIfChanged(changedValues, "semester", old.getSemester(), nw.getSemester());
        addIfChanged(changedValues, "requiredCredits", old.getRequiredCredits(), nw.getRequiredCredits());
        addIfChanged(changedValues, "hours", old.getHours(), nw.getHours());
        addIfChanged(changedValues, "type", old.getType(), nw.getType());

        if (differsArraySubject(old.getRequisites(), nw.getRequisites())) {
            changedValues.put("requisites",
                    new ChangedValue<>(old.getRequisites().stream().map(SubjectItemDto::new).toList(),
                                       nw.getRequisites().stream().map(SubjectItemDto::new).toList()));
        }

        boolean changed = !changedValues.isEmpty();
        if (changed) {
            subjectChangeLog.setFieldChanges(changedValues);
        }

        List<GroupChangeLog> groupChangeLogs = getChangesSubjectGroups(old.getGroups(), nw.getGroups());
        if (!groupChangeLogs.isEmpty()) {
            changed = true;
            groupChangeLogs.forEach(subjectChangeLog::addGroupChangeLog);
        }

        subjectChangeLog.setType(ChangeType.MODIFIED);


        return changed ? subjectChangeLog : null;
    }

    public List<GroupChangeLog> getChangesSubjectGroups(List<SubjectGroup> oldList, List<SubjectGroup> newList) {
        List<GroupChangeLog> groupChangeLogs = new LinkedList<>();

        Map<String, SubjectGroup> oldSubjectGroups = new HashMap<>();
        oldList.forEach(oldSubjectGroup -> {
            oldSubjectGroups.put(oldSubjectGroup.getCode(), oldSubjectGroup);
        });

        for (SubjectGroup SubjectGroup : newList) {
            SubjectGroup oldSubjectGroup = oldSubjectGroups.get(SubjectGroup.getCode());
            if (oldSubjectGroup == null) {
                GroupChangeLog changeLog = new GroupChangeLog();
                changeLog.setCode(SubjectGroup.getCode());
                changeLog.setType(ChangeType.ADDED);
                groupChangeLogs.add(changeLog);
            } else {
                GroupChangeLog change = getChangesSubjectGroup(oldSubjectGroup, SubjectGroup);
                if (change != null) {
                    groupChangeLogs.add(change);
                }
                oldSubjectGroups.remove(SubjectGroup.getCode());
            }
        }

        for (SubjectGroup subjectGroup : oldSubjectGroups.values()) {
            GroupChangeLog changeLog = new GroupChangeLog();
            changeLog.setCode(subjectGroup.getCode());
            changeLog.setType(ChangeType.DELETED);
            groupChangeLogs.add(changeLog);
        }

        return groupChangeLogs;
    }

    public GroupChangeLog getChangesSubjectGroup(SubjectGroup old, SubjectGroup nw) {
        GroupChangeLog groupChangeLog = new GroupChangeLog();
        groupChangeLog.setCode(nw.getCode());

        Map<String, ChangedValue<?>> changedValues = new HashMap<>();

        addIfChanged(changedValues, "code", old.getCode(), nw.getCode());
        addIfChanged(changedValues, "maxCapacity", old.getMaxCapacity(), nw.getMaxCapacity());

        if (!"-".equals(nw.getTeacher())) {
            addIfChanged(changedValues, "teacher", old.getTeacher(), nw.getTeacher());
        } else {
            if (!"-".equals(old.getTeacher())) {
                changedValues.put("teacher", new ChangedValue<>(old.getTeacher(), nw.getTeacher()));
            }
        }

        if (differsArraySessions(old.getSessions(), nw.getSessions())) {
            changedValues.put("sessions", new ChangedValue<>(
                    old.getSessions().stream().map(SessionInfoDto::new).toList(),
                    nw.getSessions().stream().map(SessionInfoDto::new).toList()));
        }

        boolean changed = !changedValues.isEmpty();

        if (changed) {
            groupChangeLog.setFieldChanges(changedValues);
            groupChangeLog.setType(ChangeType.MODIFIED);
        }

        return changed ? groupChangeLog : null;
    }

    private boolean differsArraySubject(List<Subject> a, List<Subject> b) {
        List<String> codesA = new ArrayList<>(a.stream().map(Subject::getCode).toList());
        List<String> codesB = new ArrayList<>(b.stream().map(Subject::getCode).toList());
        codesA.sort(String.CASE_INSENSITIVE_ORDER);
        codesB.sort(String.CASE_INSENSITIVE_ORDER);
        log.info("CodesA {} + codesB {}", codesA.toString(), codesB.toString());
        return !codesA.equals(codesB);
    }

    private boolean differsArraySessions(List<Session> a, List<Session> b) {
        if (a.size() != b.size()) {
            return true;
        }
        Comparator<Session> cmp = (sa, sb) -> {
            if (sa.getDay() != sb.getDay()) {
                return sa.getDay() - sb.getDay();
            } else {
                return sa.getBeginHour() - sb.getBeginHour();
            }
        };
        a.sort(cmp);
        b.sort(cmp);
        boolean differs = false;
        Iterator<Session> itA = a.iterator();
        Iterator<Session> itB = b.iterator();
        while (!differs && itA.hasNext() && itB.hasNext()) {
            Session sA = itA.next();
            Session sB = itB.next();
            differs = sA.getBeginHour() != sB.getBeginHour() ||
                    sA.getDay() != sB.getDay() ||
                    sA.getEndHour() != sB.getEndHour() ||
                    !sA.getClassroom().equals(sB.getClassroom());
        }
        return differs;
    }

    private <T> void addIfChanged(Map<String, ChangedValue<?>> map, String key, T oldValue, T newValue) {
        if (!newValue.equals(oldValue)) {
            map.put(key, new ChangedValue<>(oldValue, newValue));
        }
    }
}
