package com.marles.horarioappufps.controller;

import com.marles.horarioappufps.dto.request.CompletedSubjectsDto;
import com.marles.horarioappufps.dto.request.PensumCreationDto;
import com.marles.horarioappufps.dto.response.PensumChangeLogDto;
import com.marles.horarioappufps.dto.response.PensumInfoDto;
import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.security.UserPrincipal;
import com.marles.horarioappufps.service.PensumService;
import com.marles.horarioappufps.service.UserSubjectService;
import com.marles.horarioappufps.util.ChangeLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pensum")
@Slf4j
public class PensumController {

    private final PensumService pensumService;
    private final UserSubjectService userSubjectService;
    private final ChangeLogMapper changeLogMapper;

    @Autowired
    public PensumController(PensumService pensumService, UserSubjectService userSubjectService, ChangeLogMapper changeLogMapper) {
        this.pensumService = pensumService;
        this.userSubjectService = userSubjectService;
        this.changeLogMapper = changeLogMapper;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PensumInfoDto> getPensum(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String uid = userPrincipal.getUsername();
        return ResponseEntity.ok(pensumService.getPensumInfoDto(uid));
    }

    @PostMapping("")
    public ResponseEntity<PensumInfoDto> savePensum(@RequestBody PensumCreationDto pensumCreationDto){
        log.info("Saving pensum: {} subjects",  pensumCreationDto.getSubjects().size());
        Pensum pensum = pensumService.savePensum(pensumCreationDto);
        return ResponseEntity.ok(new PensumInfoDto(pensum));
    }

    @GetMapping("/changelog")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PensumChangeLogDto>> getChangeLog(){
        Pensum pensum = pensumService.getPensum();
        List<PensumChangeLogDto> changelogs = pensum.getUpdateLogs().stream()
                .map(changeLogMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(changelogs);
    }

    @PutMapping("/completed/{code}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> toggleSubject(@PathVariable String code, @AuthenticationPrincipal UserPrincipal userPrincipal){
        String uid = userPrincipal.getUsername();
        userSubjectService.toggle(uid, code);
        return ResponseEntity.ok("Proceso realizado correctamente");
    }

    @PostMapping("/completed")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addListToUser(@RequestBody CompletedSubjectsDto completedSubjectsDto, @AuthenticationPrincipal UserPrincipal userPrincipal){
        String uid = userPrincipal.getUsername();
        userSubjectService.addList(uid, completedSubjectsDto.getSubjects());
        return ResponseEntity.ok("Proceso realizado correctamente");
    }
}
