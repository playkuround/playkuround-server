package com.playkuround.playkuroundserver.securityPersistence.code;

import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/persistence-test")
@RequiredArgsConstructor
public class PersistenceController {

    private final PersistenceService persistenceService;

    @GetMapping("success")
    public void success(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        persistenceService.successIncreaseAttendanceDay(userDetails.getUser());
    }

    @GetMapping("fail")
    public void fail(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        persistenceService.failIncreaseAttendanceDay(userDetails.getUser());
    }

    @GetMapping("rollback")
    public void rollback(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        persistenceService.rollbackIncreaseAttendanceDay(userDetails.getUser());
    }

}
