package com.marles.horarioappufps.util;

import com.marles.horarioappufps.exception.ScheduleConflictException;
import com.marles.horarioappufps.model.Session;
import com.marles.horarioappufps.model.SubjectGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for detecting Overlaps in Schedules. <br>
 * For optimization purposes, it uses a Segment Tree to verify any overlapping occurring. <br>
 * Given the problem's specifications, it can be represented as an Online Painting Subarrays Problem. <br>
 * Thus, it can be solved for this use case with a Lazy Propagation Max Segment Tree.
 */
@Slf4j
public class OverlapValidator {

    private final int DAYS = 6;
    private final int HOUR_SPACES = 16;
    private final SegmentTree segmentTree = new SegmentTree(DAYS * HOUR_SPACES);
    private final List<SubjectGroup> subjectGroups = new ArrayList<>(20);

    public void addList(List<SubjectGroup> subjectGroups) {
        for (SubjectGroup subjectGroup : subjectGroups) {
            this.add(subjectGroup);
        }
    }

    public void add(SubjectGroup subjectGroup) throws ScheduleConflictException {
        SubjectGroup overlapped = this.overlaps(subjectGroup);
        if (overlapped != null) {
            throw new ScheduleConflictException(subjectGroup.getCode(), overlapped.getCode());
        }
        for (Session session : subjectGroup.getSessions()) {
            this.add(session, this.subjectGroups.size());
        }
        this.subjectGroups.add(subjectGroup);
    }

    public void add(Session session, int value) {
        log.info("Adding session {} {} {}", session.getDay(), session.getBeginHour(), session.getEndHour());
        int i = getBeginIndex(session);
        int j = getEndIndex(session);
        log.info("Adding group {} to in range [{},{})", value, i, j);
        this.segmentTree.update(i, j, value);
    }

    public SubjectGroup overlaps(SubjectGroup group) {
        for (Session session : group.getSessions()) {
            SubjectGroup ans = overlaps(session);
            if (ans != null) {
                return ans;
            }
        }
        return null;
    }

    public SubjectGroup overlaps(Session session) {
        int i = getBeginIndex(session);
        int j = getEndIndex(session);
        int idx = this.segmentTree.query(i, j);
        log.info("Overlapping group in range [{},{}): {}", i, j, idx);
        SubjectGroup ans = null;
        if (idx != -1) {
            ans = subjectGroups.get(idx);
        }
        return ans;
    }

    private int getBeginIndex(Session session){
        return session.getBeginHour() + (HOUR_SPACES * session.getDay());
    }

    private int getEndIndex(Session session){
        return session.getEndHour() + (HOUR_SPACES * session.getDay()) - 1;
    }
}
