package com.marles.horarioappufps.controller;

import com.marles.horarioappufps.dto.request.CompletedSubjectsDto;
import com.marles.horarioappufps.dto.request.PensumCreationDto;
import com.marles.horarioappufps.dto.response.PensumInfoDto;
import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.security.UserPrincipal;
import com.marles.horarioappufps.service.PensumService;
import com.marles.horarioappufps.service.UserSubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pensum")
@Slf4j
public class PensumController {

    private final PensumService pensumService;
    private final UserSubjectService userSubjectService;

    @Autowired
    public PensumController(PensumService pensumService, UserSubjectService userSubjectService) {
        this.pensumService = pensumService;
        this.userSubjectService = userSubjectService;
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
