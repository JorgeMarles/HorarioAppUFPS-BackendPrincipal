package com.marles.horarioappufps.service;

import com.marles.horarioappufps.dto.request.PensumCreationDto;
import com.marles.horarioappufps.dto.request.SessionCreationDto;
import com.marles.horarioappufps.dto.request.SubjectCreationDto;
import com.marles.horarioappufps.dto.request.SubjectGroupCreationDto;
import com.marles.horarioappufps.dto.response.SubjectItemDto;
import com.marles.horarioappufps.exception.PensumNotFoundException;
import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.Session;
import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.model.SubjectGroup;
import com.marles.horarioappufps.repository.PensumRepository;
import com.marles.horarioappufps.repository.SessionRepository;
import com.marles.horarioappufps.repository.SubjectGroupRepository;
import com.marles.horarioappufps.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PensumServiceTest {
    @InjectMocks
    private PensumService pensumService;

    @Mock
    private PensumRepository pensumRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectGroupRepository subjectGroupRepository;

    @Mock
    private SessionRepository sessionRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindPensum_Default() {
        Pensum pensum = new Pensum();
        pensum.setId(1L);
        when(pensumRepository.findById(1L)).thenReturn(Optional.of(pensum));
        when(pensumRepository.save(any(Pensum.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Pensum found = pensumService.getPensum();
        assertEquals(pensum.getId(), found.getId());
    }

    @Test
    public void testFindPensum_NotFound(){
        when(pensumRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(PensumNotFoundException.class, () -> pensumService.getPensum(2L));
    }

    @Test
    public void testPensumSave() {
        PensumCreationDto pensumDto = new PensumCreationDto();
        pensumDto.setName("Pensum");
        pensumDto.setSemesters(10);
        pensumDto.setUpdateTeachers(false);

        SubjectCreationDto subjectDto = new SubjectCreationDto();
        subjectDto.setCode("1155555");
        subjectDto.setName("Subject 1");
        subjectDto.setCredits(4);
        subjectDto.setHours(6);
        subjectDto.setSemester(1);
        subjectDto.setRequisites(List.of());

        SubjectGroupCreationDto groupDto = new SubjectGroupCreationDto();
        groupDto.setCode("1155555-A");
        groupDto.setTeacher("Teacher A");
        groupDto.setMaxCapacity(30);
        groupDto.setAvailableCapacity(20);
        groupDto.setSessions(List.of());

        subjectDto.setGroups(List.of(groupDto));
        pensumDto.setSubjects(List.of(subjectDto));

        Pensum pensumEntity = new Pensum();
        pensumEntity.setId(1L);
        pensumEntity.setName("Pensum");
        pensumEntity.setSemesters(10);

        Subject subjectEntity = new Subject();
        subjectEntity.setCode("1155555");

        SubjectGroup groupEntity = new SubjectGroup();
        groupEntity.setCode("1155555-A");
        groupEntity.setTeacher("Teacher A");

        when(pensumRepository.save(any(Pensum.class))).thenReturn(pensumEntity);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subjectEntity);
        when(subjectRepository.findByCode("1155555")).thenReturn(Optional.empty());
        when(subjectGroupRepository.findByCode("1155555-A")).thenReturn(Optional.empty());
        when(subjectGroupRepository.save(any(SubjectGroup.class))).thenReturn(groupEntity);

        Pensum result = pensumService.savePensum(pensumDto);

        assertNotNull(result);
        assertEquals("Pensum", result.getName());
        assertEquals(10, result.getSemesters());

        verify(pensumRepository, times(2)).save(any(Pensum.class));
        verify(subjectRepository, times(1)).findByCode("1155555");
        verify(subjectRepository, times(2)).save(any(Subject.class));
        verify(subjectGroupRepository, times(1)).findByCode("1155555-A");
        verify(subjectGroupRepository, times(1)).save(any(SubjectGroup.class));
    }

    /**
     * Teacher valid data
     * Case 1: Object: -, DTO: -
     * Answer: -, current teacher Yes
     *
     * Case 2: Object: -, DTO: B
     * Answer: B, current teacher Yes
     *
     * Case 3: Object: A, DTO: -
     * Answer: A, current teacher No
     *
     * Case 4: Object: A, DTO: B
     * Answer: B, current teacher Yes
     */

    @Test
    public void testUpdateFields_SubjectGroup_Case1(){
        SubjectGroupCreationDto sgdto = new SubjectGroupCreationDto();
        sgdto.setCode("1155555-A");
        
        sgdto.setMaxCapacity(30);
        sgdto.setAvailableCapacity(20);
        sgdto.setTeacher("-");

        SubjectGroup subjectGroup = new SubjectGroup();
        subjectGroup.setCode("1155555-A");
        subjectGroup.setTeacher("-");

        ArgumentCaptor<SubjectGroup> argumentCaptor = ArgumentCaptor.forClass(SubjectGroup.class);

        when(subjectGroupRepository.findByCode("1155555-A")).thenReturn(Optional.of(subjectGroup));
        when(subjectGroupRepository.save(argumentCaptor.capture())).thenReturn(subjectGroup);

        pensumService.createOrUpdateSubjectGroup(sgdto,  null, true);

        SubjectGroup result =  argumentCaptor.getValue();

        assertEquals("-", result.getTeacher());
        assertTrue(result.isCurrentTeacher());
    }

    @Test
    public void testUpdateFields_SubjectGroup_Case2(){
        SubjectGroupCreationDto sgdto = new SubjectGroupCreationDto();
        sgdto.setCode("1155555-A");
        
        sgdto.setMaxCapacity(30);
        sgdto.setAvailableCapacity(20);
        sgdto.setTeacher("B");

        SubjectGroup subjectGroup = new SubjectGroup();
        subjectGroup.setCode("1155555-A");
        subjectGroup.setTeacher("-");

        ArgumentCaptor<SubjectGroup> argumentCaptor = ArgumentCaptor.forClass(SubjectGroup.class);

        when(subjectGroupRepository.findByCode("1155555-A")).thenReturn(Optional.of(subjectGroup));
        when(subjectGroupRepository.save(argumentCaptor.capture())).thenReturn(subjectGroup);

        pensumService.createOrUpdateSubjectGroup(sgdto, null, true);

        SubjectGroup result =  argumentCaptor.getValue();

        assertEquals("B", result.getTeacher());
        assertTrue(result.isCurrentTeacher());
    }

    @Test
    public void testUpdateFields_SubjectGroup_Case3(){
        SubjectGroupCreationDto sgdto = new SubjectGroupCreationDto();
        sgdto.setCode("1155555-A");
        
        sgdto.setMaxCapacity(30);
        sgdto.setAvailableCapacity(20);
        sgdto.setTeacher("-");

        SubjectGroup subjectGroup = new SubjectGroup();
        subjectGroup.setCode("1155555-A");
        subjectGroup.setTeacher("A");

        ArgumentCaptor<SubjectGroup> argumentCaptor = ArgumentCaptor.forClass(SubjectGroup.class);

        when(subjectGroupRepository.findByCode("1155555-A")).thenReturn(Optional.of(subjectGroup));
        when(subjectGroupRepository.save(argumentCaptor.capture())).thenReturn(subjectGroup);

        pensumService.createOrUpdateSubjectGroup(sgdto, null, true);

        SubjectGroup result =  argumentCaptor.getValue();

        assertEquals("A", result.getTeacher());
        assertFalse(result.isCurrentTeacher());
    }

    @Test
    public void testUpdateFields_SubjectGroup_Case4(){
        SubjectGroupCreationDto sgdto = new SubjectGroupCreationDto();
        sgdto.setCode("1155555-A");
        
        sgdto.setMaxCapacity(30);
        sgdto.setAvailableCapacity(20);
        sgdto.setTeacher("B");

        SubjectGroup subjectGroup = new SubjectGroup();
        subjectGroup.setCode("1155555-A");
        subjectGroup.setTeacher("A");

        ArgumentCaptor<SubjectGroup> argumentCaptor = ArgumentCaptor.forClass(SubjectGroup.class);

        when(subjectGroupRepository.findByCode("1155555-A")).thenReturn(Optional.of(subjectGroup));
        when(subjectGroupRepository.save(argumentCaptor.capture())).thenReturn(subjectGroup);

        pensumService.createOrUpdateSubjectGroup(sgdto, null, true);

        SubjectGroup result =  argumentCaptor.getValue();

        assertEquals("B", result.getTeacher());
        assertTrue(result.isCurrentTeacher());
    }

    @Test
    public void testSubject_DuplicateCode() {
        PensumCreationDto pensumDto = new PensumCreationDto();
        pensumDto.setName("Pensum");
        pensumDto.setSemesters(10);
        pensumDto.setUpdateTeachers(false);

        SubjectCreationDto s1 = new SubjectCreationDto();
        s1.setCode("1155104");
        s1.setName("Fundamentos de Programacion");
        s1.setCredits(4);
        s1.setHours(4);
        s1.setSemester(1);
        s1.setRequisites(List.of());
        s1.setGroups(List.of());

        SubjectCreationDto s2 = new SubjectCreationDto();
        s2.setCode("1155104");
        s2.setName("Programacion Orientada a Objetos I");
        s2.setCredits(4);
        s2.setHours(4);
        s2.setSemester(2);
        s2.setRequisites(List.of());
        s2.setGroups(List.of());

        pensumDto.setSubjects(List.of(s1, s2));

        Pensum existingPensum = new Pensum();
        existingPensum.setId(1L);

        Subject sub1 = new Subject();
        sub1.setCode("1155104");
        sub1.setName("Fundamentos de Programacion");

        when(pensumRepository.findById(1L)).thenReturn(Optional.of(existingPensum));
        when(pensumRepository.save(any(Pensum.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(IllegalArgumentException.class, () -> pensumService.savePensum(pensumDto));
    }

    @Test
    public void testSubjectGroup_DuplicateCode() {
        PensumCreationDto pensumDto = new PensumCreationDto();
        pensumDto.setName("Pensum");
        pensumDto.setSemesters(10);
        pensumDto.setUpdateTeachers(false);

        SubjectCreationDto s1 = new SubjectCreationDto();
        s1.setCode("1155104");
        s1.setName("Fundamentos de Programacion");
        s1.setRequisites(List.of());

        SubjectGroupCreationDto sg1 = new SubjectGroupCreationDto();
        sg1.setCode("1155104-A");
        sg1.setSessions(List.of());

        SubjectCreationDto s2 = new SubjectCreationDto();
        s2.setCode("1155204");
        s2.setName("POO II");
        s2.setRequisites(List.of());

        SubjectGroupCreationDto sg2 = new SubjectGroupCreationDto();
        sg2.setCode("1155104-A");
        sg2.setSessions(List.of());

        s1.setGroups(List.of(sg1));
        s2.setGroups(List.of(sg2));

        pensumDto.setSubjects(List.of(s1, s2));

        when(pensumRepository.save(any(Pensum.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subjectGroupRepository.save(any(SubjectGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(IllegalArgumentException.class, () -> pensumService.savePensum(pensumDto));
    }

    @Test
    public void testCircularRequisite() {
        PensumCreationDto pensumDto = new PensumCreationDto();
        pensumDto.setName("Pensum");
        pensumDto.setSemesters(10);
        pensumDto.setUpdateTeachers(false);

        SubjectCreationDto s1 = new SubjectCreationDto();
        s1.setCode("1155104");
        s1.setName("Fundamentos de Programacion");
        s1.setCredits(4);
        s1.setHours(4);
        s1.setSemester(1);
        s1.setGroups(List.of());

        SubjectCreationDto s2 = new SubjectCreationDto();
        s2.setCode("1155204");
        s2.setName("Programacion Orientada a Objetos I");
        s2.setCredits(4);
        s2.setHours(4);
        s2.setSemester(2);
        s2.setGroups(List.of());

        SubjectItemDto si1 = new SubjectItemDto();
        si1.setCode("1155104");

        SubjectItemDto si2 = new SubjectItemDto();
        si2.setCode("1155204");

        s1.setRequisites(List.of(si2));
        s2.setRequisites(List.of(si1));

        pensumDto.setSubjects(List.of(s1, s2));

        when(pensumRepository.save(any(Pensum.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(IllegalArgumentException.class, () -> pensumService.savePensum(pensumDto));

        s1.setRequisites(List.of());
        s2.setRequisites(List.of(si1));

        assertDoesNotThrow(() -> pensumService.savePensum(pensumDto));
    }

    @Test
    public void test_SubjectGroupCollision() {
        PensumCreationDto pensumDto = new PensumCreationDto();
        pensumDto.setName("Pensum");
        pensumDto.setSemesters(10);
        pensumDto.setUpdateTeachers(false);

        SubjectCreationDto s1 = new SubjectCreationDto();
        s1.setCode("1155104");
        s1.setName("Fundamentos de Programacion");
        s1.setCredits(4);
        s1.setHours(4);
        s1.setSemester(1);
        s1.setRequisites(List.of());

        SubjectGroupCreationDto sg1 = new SubjectGroupCreationDto();
        sg1.setCode("1155104-A");

        SessionCreationDto ss1 = new SessionCreationDto();
        ss1.setDay(0);
        ss1.setBeginHour(0);
        ss1.setEndHour(2);

        SessionCreationDto ss2 = new SessionCreationDto();
        ss2.setDay(0);
        ss2.setBeginHour(1);
        ss2.setEndHour(3);

        sg1.setSessions(List.of(ss1, ss2));

        s1.setGroups(List.of(sg1));

        pensumDto.setSubjects(List.of(s1));

        when(pensumRepository.save(any(Pensum.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subjectGroupRepository.save(any(SubjectGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(IllegalArgumentException.class, () -> pensumService.savePensum(pensumDto));

        ss2.setBeginHour(2);

        assertDoesNotThrow(() -> pensumService.savePensum(pensumDto));
    }
}
