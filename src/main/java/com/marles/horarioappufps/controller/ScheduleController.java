package com.marles.horarioappufps.controller;

import com.marles.horarioappufps.dto.request.ScheduleCreationDto;
import com.marles.horarioappufps.dto.request.ScheduleGroupChangeDto;
import com.marles.horarioappufps.dto.response.schedule.ScheduleInfoDto;
import com.marles.horarioappufps.model.Schedule;
import com.marles.horarioappufps.security.UserPrincipal;
import com.marles.horarioappufps.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService){
        this.scheduleService = scheduleService;
    }

    private void validatePermissions(Long scheduleId, UserPrincipal userPrincipal) {
        String uid = userPrincipal.getUsername();
        boolean isAdmin = userPrincipal.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        scheduleService.validatePermissions(scheduleId, uid, isAdmin);
    }

    @GetMapping("/user/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduleInfoDto>> getByUser(@PathVariable String uid){
        List<ScheduleInfoDto> scheduleInfoDtoList = scheduleService.getByUserUid_Dto(uid);

        return ResponseEntity.ok(scheduleInfoDtoList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ScheduleInfoDto> getById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        validatePermissions(id, userPrincipal);
        return ResponseEntity.ok(scheduleService.getById_Dto(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ScheduleInfoDto>> getMySchedules(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String uid = userPrincipal.getUsername();
        return ResponseEntity.ok(scheduleService.getByUserUid_Dto(uid));
    }

    @PostMapping("/duplicate/{scheduleId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ScheduleInfoDto> duplicateSchedule(@PathVariable Long scheduleId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        validatePermissions(scheduleId, userPrincipal);
        Schedule cpy = scheduleService.duplicateSchedule(scheduleId, userPrincipal.getUsername());
        return ResponseEntity.ok(scheduleService.getFromSchedule(cpy));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ScheduleInfoDto> createSchedule(@RequestBody ScheduleCreationDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String uid = userPrincipal.getUsername();
        Schedule resp = scheduleService.createSchedule(dto.getTitle(), uid);
        return ResponseEntity.ok(scheduleService.getFromSchedule(resp));
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        validatePermissions(scheduleId, userPrincipal);
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok("Horario eliminado correctamente");
    }

    @PostMapping("/{scheduleId}/subject/{subjectCode}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ScheduleInfoDto> addSubject(@PathVariable Long scheduleId, @PathVariable String subjectCode, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        validatePermissions(scheduleId, userPrincipal);
        Schedule schedule = scheduleService.addSubject(scheduleId, subjectCode);
        return ResponseEntity.ok(scheduleService.getFromSchedule(schedule));
    }

    @DeleteMapping("/{scheduleId}/group/{groupCode}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ScheduleInfoDto> deleteGroup(@PathVariable Long scheduleId, @PathVariable String groupCode, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        validatePermissions(scheduleId, userPrincipal);
        Schedule schedule = scheduleService.deleteFromSchedule(scheduleId, groupCode);
        return ResponseEntity.ok(scheduleService.getFromSchedule(schedule));
    }

    @PutMapping("/{scheduleId}/group/{oldCode}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ScheduleInfoDto> changeGroup(@PathVariable Long scheduleId, @PathVariable String oldCode, @RequestBody ScheduleGroupChangeDto newCodeDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        validatePermissions(scheduleId, userPrincipal);
        Schedule schedule = scheduleService.changeGroup(scheduleId, oldCode, newCodeDto.getNewCode());
        return ResponseEntity.ok(scheduleService.getFromSchedule(schedule));
    }

}
