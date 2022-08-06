package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserFindDao userFindDao;

    public UserProfileDto.Response getUserProfile(String email) {
        User user = userFindDao.findByEmail(email);
        return UserProfileDto.Response.of(user);
    }

}
