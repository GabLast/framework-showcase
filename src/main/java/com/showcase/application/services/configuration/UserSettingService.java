package com.showcase.application.services.configuration;

import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.User;
import com.showcase.application.repositories.configuration.UserSettingRepository;
import com.showcase.application.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingService extends BaseService<UserSetting, Long> {

    private final UserSettingRepository userSettingRepository;
    @Override
    protected JpaRepository<UserSetting, Long> getRepository() {
        return userSettingRepository;
    }

    public UserSetting findByUserAndEnabled(boolean enabled, User user) {
        return userSettingRepository.findByEnabledAndUser(enabled, user);
    }

}
