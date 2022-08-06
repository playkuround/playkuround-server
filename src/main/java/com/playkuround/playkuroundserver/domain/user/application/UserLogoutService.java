package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLogoutService {

    private final UserFindDao userFindDao;

    public void logout(String userEmail) {
        User user = userFindDao.findByEmail(userEmail);
        user.revokeRefreshToken();
    }

}
