package com.marles.horarioappufps.util;

import com.marles.horarioappufps.exception.ScheduleConflictException;
import com.marles.horarioappufps.model.Session;
import com.marles.horarioappufps.model.SubjectGroup;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OverlapValidatorTest {

    @Test
    public void testOverlapException_addList(){
        SubjectGroup sg1 = new SubjectGroup();
        sg1.setCode("1155555-A");
        SubjectGroup sg2 = new SubjectGroup();
        sg2.setCode("1155555-B");

        Session s11 = new Session();
        s11.setDay(0);
        s11.setBeginHour(0);
        s11.setEndHour(2);

        Session s21 = new Session();
        s21.setDay(0);
        s21.setBeginHour(1);
        s21.setEndHour(3);

        sg1.setSessions(List.of(s11));
        sg2.setSessions(List.of(s21));

        List<SubjectGroup> subjectGroups = List.of(sg1, sg2);

        OverlapValidator overlapValidator = new OverlapValidator();

        assertThrows(ScheduleConflictException.class, ()->{
           overlapValidator.addList(subjectGroups);
        });
    }

    @Test
    public void testOverlapException_add(){
        SubjectGroup sg1 = new SubjectGroup();
        sg1.setCode("1155555-A");
        SubjectGroup sg2 = new SubjectGroup();
        sg2.setCode("1155555-B");

        Session s11 = new Session();
        s11.setDay(0);
        s11.setBeginHour(0);
        s11.setEndHour(2);

        Session s21 = new Session();
        s21.setDay(0);
        s21.setBeginHour(2);
        s21.setEndHour(4);

        sg1.setSessions(List.of(s11));
        sg2.setSessions(List.of(s21));

        List<SubjectGroup> subjectGroups = List.of(sg1, sg2);

        OverlapValidator overlapValidator = new OverlapValidator();

        overlapValidator.addList(subjectGroups);

        SubjectGroup sg3 = new SubjectGroup();
        sg3.setCode("1155555-C");
        Session s31 = new Session();
        s31.setDay(0);
        s31.setBeginHour(0);
        s31.setEndHour(2);

        sg3.setSessions(List.of(s31));

        assertThrows(ScheduleConflictException.class, ()->{
            overlapValidator.add(sg3);
        });
    }

    @Test
    public void testOverlap_overlaps_true_1(){
        SubjectGroup sg1 = new SubjectGroup();
        sg1.setCode("1155555-A");
        SubjectGroup sg2 = new SubjectGroup();
        sg2.setCode("1155555-B");

        Session s11 = new Session();
        s11.setDay(0);
        s11.setBeginHour(0);
        s11.setEndHour(2);

        Session s21 = new Session();
        s21.setDay(0);
        s21.setBeginHour(2);
        s21.setEndHour(4);

        sg1.setSessions(List.of(s11));
        sg2.setSessions(List.of(s21));

        List<SubjectGroup> subjectGroups = List.of(sg1, sg2);

        OverlapValidator overlapValidator = new OverlapValidator();

        overlapValidator.addList(subjectGroups);

        SubjectGroup sg3 = new SubjectGroup();
        sg3.setCode("1155555-C");
        Session s31 = new Session();
        s31.setDay(0);
        s31.setBeginHour(0);
        s31.setEndHour(2);

        sg3.setSessions(List.of(s31));

        assertEquals("1155555-A", overlapValidator.overlaps(sg3).getCode());
    }

    @Test
    public void testOverlap_overlaps_true_2(){
        SubjectGroup sg1 = new SubjectGroup();
        sg1.setCode("1155555-A");
        SubjectGroup sg2 = new SubjectGroup();
        sg2.setCode("1155555-B");

        Session s11 = new Session();
        s11.setDay(0);
        s11.setBeginHour(0);
        s11.setEndHour(2);

        Session s21 = new Session();
        s21.setDay(0);
        s21.setBeginHour(2);
        s21.setEndHour(4);

        sg1.setSessions(List.of(s11));
        sg2.setSessions(List.of(s21));

        List<SubjectGroup> subjectGroups = List.of(sg1, sg2);

        OverlapValidator overlapValidator = new OverlapValidator();

        overlapValidator.addList(subjectGroups);

        SubjectGroup sg3 = new SubjectGroup();
        sg3.setCode("1155555-C");
        Session s31 = new Session();
        s31.setDay(0);
        s31.setBeginHour(2);
        s31.setEndHour(4);

        sg3.setSessions(List.of(s31));

        assertEquals("1155555-B", overlapValidator.overlaps(sg3).getCode());
    }

    @Test
    public void testOverlap_overlaps_true_3(){
        SubjectGroup sg1 = new SubjectGroup();
        sg1.setCode("1155555-A");
        SubjectGroup sg2 = new SubjectGroup();
        sg2.setCode("1155555-B");

        Session s11 = new Session();
        s11.setDay(0);
        s11.setBeginHour(0);
        s11.setEndHour(2);

        Session s21 = new Session();
        s21.setDay(0);
        s21.setBeginHour(2);
        s21.setEndHour(4);

        sg1.setSessions(List.of(s11));
        sg2.setSessions(List.of(s21));

        List<SubjectGroup> subjectGroups = List.of(sg1, sg2);

        OverlapValidator overlapValidator = new OverlapValidator();

        overlapValidator.addList(subjectGroups);

        SubjectGroup sg3 = new SubjectGroup();
        sg3.setCode("1155555-C");
        Session s31 = new Session();
        s31.setDay(0);
        s31.setBeginHour(1);
        s31.setEndHour(3);

        sg3.setSessions(List.of(s31));

        assertEquals("1155555-B", overlapValidator.overlaps(sg3).getCode());
    }

    @Test
    public void testOverlap_overlaps_false(){
        SubjectGroup sg1 = new SubjectGroup();
        sg1.setCode("1155555-A");
        SubjectGroup sg2 = new SubjectGroup();
        sg2.setCode("1155555-B");

        Session s11 = new Session();
        s11.setDay(0);
        s11.setBeginHour(0);
        s11.setEndHour(2);

        Session s21 = new Session();
        s21.setDay(0);
        s21.setBeginHour(2);
        s21.setEndHour(4);

        sg1.setSessions(List.of(s11));
        sg2.setSessions(List.of(s21));

        List<SubjectGroup> subjectGroups = List.of(sg1, sg2);

        OverlapValidator overlapValidator = new OverlapValidator();

        overlapValidator.addList(subjectGroups);

        SubjectGroup sg3 = new SubjectGroup();
        sg3.setCode("1155555-C");
        Session s31 = new Session();
        s31.setDay(1);
        s31.setBeginHour(0);
        s31.setEndHour(2);

        assertNull(overlapValidator.overlaps(sg3));
    }

    @Test
    public void testOverlap_overlaps_false_2(){
        SubjectGroup sg1 = new SubjectGroup();
        sg1.setCode("1155555-A");

        Session s11 = new Session();
        s11.setDay(5);
        s11.setBeginHour(15);
        s11.setEndHour(16);

        sg1.setSessions(List.of(s11));

        List<SubjectGroup> subjectGroups = List.of(sg1);

        OverlapValidator overlapValidator = new OverlapValidator();

        overlapValidator.addList(subjectGroups);

        SubjectGroup sg3 = new SubjectGroup();
        sg3.setCode("1155555-C");
        Session s31 = new Session();
        s31.setDay(5);
        s31.setBeginHour(14);
        s31.setEndHour(15);

        assertNull(overlapValidator.overlaps(sg3));
    }
}
