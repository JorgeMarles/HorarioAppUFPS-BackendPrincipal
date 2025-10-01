package com.marles.horarioappufps.service;

import com.marles.horarioappufps.dto.request.PensumCreationDto;
import com.marles.horarioappufps.dto.request.SubjectCreationDto;
import com.marles.horarioappufps.dto.request.SubjectGroupCreationDto;
import com.marles.horarioappufps.exception.PensumNotFoundException;
import com.marles.horarioappufps.model.Pensum;
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
        groupDto.setName("1155555-A");
        groupDto.setTeacher("Teacher A");
        groupDto.setProgram("ING");
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

        verify(pensumRepository, times(1)).save(any(Pensum.class));
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
        sgdto.setName("1155555-A");
        sgdto.setProgram("115");
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
        sgdto.setName("1155555-A");
        sgdto.setProgram("115");
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
        sgdto.setName("1155555-A");
        sgdto.setProgram("115");
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
        sgdto.setName("1155555-A");
        sgdto.setProgram("115");
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
}
