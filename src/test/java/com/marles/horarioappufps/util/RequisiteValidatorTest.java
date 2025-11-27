package com.marles.horarioappufps.util;

import com.marles.horarioappufps.exception.RequisiteConflictException;
import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.service.PensumService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for RequisiteValidator
 * A -> B means A is requisite of B, that is, A unlocks B
 */
@Slf4j
public class RequisiteValidatorTest {
    /**
     * Case 1.
     * A -> B
     * Add A, then Add B
     * RequisiteConlictException expected
     */
    @Test
    public void TestRequisiteException_Case1(){
        Pensum pensum = new Pensum();
        Subject A = new Subject();
        A.setCode("A");
        Subject B = new Subject();
        B.setCode("B");
        B.setRequisites(List.of(A));

        pensum.setSubjects(List.of(A,B));

        RequisiteValidator rv = new RequisiteValidator(pensum);

        assertDoesNotThrow(() -> {
            rv.add("A");
        });
        assertThrows(RequisiteConflictException.class, () -> {
           rv.add("B");
        });
    }

    /**
     * Case 2.
     * A -> B
     * Add B, then Add A
     * RequisiteConlictException expected
     */
    @Test
    public void TestRequisiteException_Case2(){
        Pensum pensum = new Pensum();
        Subject A = new Subject();
        A.setCode("A");
        Subject B = new Subject();
        B.setCode("B");
        B.setRequisites(List.of(A));

        pensum.setSubjects(List.of(A,B));

        RequisiteValidator rv = new RequisiteValidator(pensum);

        assertDoesNotThrow(() -> {
            rv.add("B");
        });
        assertThrows(RequisiteConflictException.class, () -> {
            rv.add("A");
        });
    }

    /**
     * Case 3.
     * A -> B
     * A -> C
     * Add B, Add C, then Add A
     * RequisiteConlictException expected
     */
    @Test
    public void TestRequisiteException_Case3(){
        Pensum pensum = new Pensum();
        Subject A = new Subject();
        A.setCode("A");
        Subject B = new Subject();
        B.setCode("B");
        B.setRequisites(List.of(A));
        Subject C = new Subject();
        C.setCode("C");
        C.setRequisites(List.of(A));

        pensum.setSubjects(List.of(A,B,C));

        RequisiteValidator rv = new RequisiteValidator(pensum);

        assertDoesNotThrow(() -> {
            rv.add("B");
            rv.add("C");
        });
        assertThrows(RequisiteConflictException.class, () -> {
            rv.add("A");
        });
    }

    /**
     * Case 4.
     * A -> C
     * B -> C
     * Add A, Add B, then Add C
     * RequisiteConflictException expected
     */
    @Test
    public void TestRequisiteException_Case4(){
        Pensum pensum = new Pensum();
        Subject A = new Subject();
        A.setCode("A");
        Subject B = new Subject();
        B.setCode("B");
        Subject C = new Subject();
        C.setCode("C");
        C.setRequisites(List.of(A,B));

        pensum.setSubjects(List.of(A,B,C));

        RequisiteValidator rv = new RequisiteValidator(pensum);

        assertDoesNotThrow(() -> {
            rv.add("A");
            rv.add("B");
        });
        assertThrows(RequisiteConflictException.class, () -> {
            rv.add("C");
        });
    }

    /**
     * Case 5.
     * A -> C
     * B -> C
     * Add A, Add B
     * No Exception expected
     */
    @Test
    public void TestRequisiteException_Ok(){
        Pensum pensum = new Pensum();
        Subject A = new Subject();
        A.setCode("A");
        Subject B = new Subject();
        B.setCode("B");
        Subject C = new Subject();
        C.setCode("C");
        C.setRequisites(List.of(A,B));

        pensum.setSubjects(List.of(A,B,C));

        RequisiteValidator rv = new RequisiteValidator(pensum);


        assertDoesNotThrow(() -> {
            rv.add("A");
            rv.add("B");
        });
    }

    /**
     * Case 1. Indirect
     * A -> B
     * B -> C
     * C -> D
     * Add A, then add D
     * RequisiteConflictException expected
     */
    @Test
    public void TestRequisiteException_Case1Indirect() {
        Subject A = new Subject();
        A.setCode("A");
        Subject B = new Subject();
        B.setCode("B");
        Subject C = new Subject();
        C.setCode("C");
        Subject D = new Subject();
        D.setCode("D");

        B.setRequisites(List.of(A));
        C.setRequisites(List.of(B));
        D.setRequisites(List.of(C));

        Pensum p = new Pensum();
        p.setSubjects(List.of(A,B,C,D));
        RequisiteValidator rv = new RequisiteValidator(p);

        assertDoesNotThrow(() -> {
            rv.add("A");
        });
        assertThrows(RequisiteConflictException.class, () -> {
            rv.add("D");
        });
    }

    /**
     * Case 2. Indirect
     * A -> B
     * B -> C
     * C -> D
     * Add D, then add A
     * RequisiteConflictException expected
     */
    @Test
    public void TestRequisiteException_Case2Indirect() {
        Subject A = new Subject();
        A.setCode("A");
        Subject B = new Subject();
        B.setCode("B");
        Subject C = new Subject();
        C.setCode("C");
        Subject D = new Subject();
        D.setCode("D");

        B.setRequisites(List.of(A));
        C.setRequisites(List.of(B));
        D.setRequisites(List.of(C));

        Pensum p = new Pensum();
        p.setSubjects(List.of(A,B,C,D));

        RequisiteValidator rv = new RequisiteValidator(p);

        assertDoesNotThrow(() -> {
            rv.add("D");
        });
        assertThrows(RequisiteConflictException.class, () -> {
            rv.add("A");
        });
    }

    /**
     * Case 3. Indirect
     * A -> B
     * B -> C
     * C -> D
     * C -> E
     * Add D, Add E, then Add A
     * RequisiteConlictException expected
     */
    @Test
    public void TestRequisiteException_Case3Indirect() {
        Subject A = new Subject();
        A.setCode("A");
        Subject B = new Subject();
        B.setCode("B");
        Subject C = new Subject();
        C.setCode("C");
        Subject D = new Subject();
        D.setCode("D");
        Subject E = new Subject();
        E.setCode("E");

        B.setRequisites(List.of(A));
        C.setRequisites(List.of(B));
        D.setRequisites(List.of(C));
        E.setRequisites(List.of(C));

        Pensum p = new Pensum();
        p.setSubjects(List.of(A,B,C,D,E));

        RequisiteValidator rv = new RequisiteValidator(p);

        assertDoesNotThrow(() -> {
            rv.add("D");
            rv.add("E");
        });
        assertThrows(RequisiteConflictException.class, () -> {
            rv.add("A");
        });
    }

    /**
     * Case 4. Indirect
     * A -> C
     * B -> C
     * C -> D
     * D -> E
     * Add A, Add B, then Add E
     * RequisiteConlictException expected
     */
    @Test
    public void TestRequisiteException_Case4Indirect() {
        Subject A = new Subject();
        A.setCode("A");
        Subject B = new Subject();
        B.setCode("B");
        Subject C = new Subject();
        C.setCode("C");
        Subject D = new Subject();
        D.setCode("D");
        Subject E = new Subject();
        E.setCode("E");

        C.setRequisites(List.of(A,B));
        D.setRequisites(List.of(C));
        E.setRequisites(List.of(D));

        Pensum p = new Pensum();
        p.setSubjects(List.of(A,B,C,D,E));

        RequisiteValidator rv = new RequisiteValidator(p);

        assertDoesNotThrow(() -> {
            rv.add("A");
            rv.add("B");
        });
        assertThrows(RequisiteConflictException.class, () -> {
            rv.add("E");
        });
    }
}
