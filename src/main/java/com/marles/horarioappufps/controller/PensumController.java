package com.marles.horarioappufps.controller;

import com.marles.horarioappufps.dto.request.PensumCreationDto;
import com.marles.horarioappufps.dto.response.PensumInfoDto;
import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.service.PensumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pensum")
@Slf4j
public class PensumController {

    private final PensumService pensumService;

    @Autowired
    public PensumController(PensumService pensumService) {
        this.pensumService = pensumService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PensumInfoDto> getPensum(){
        return ResponseEntity.ok(new PensumInfoDto(pensumService.getPensum()));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PensumInfoDto> savePensum(@RequestBody PensumCreationDto pensumCreationDto){
        Pensum pensum = pensumService.savePensum(pensumCreationDto);
        return ResponseEntity.ok(new PensumInfoDto(pensum));
    }
}
