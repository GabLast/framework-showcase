package com.showcase.application.services.security;

import com.showcase.application.models.security.Profile;
import com.showcase.application.models.security.ProfilePermit;
import com.showcase.application.repositories.security.ProfilePermitRepository;
import com.showcase.application.services.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProfilePermitService extends BaseService<ProfilePermit, Long> {

    private final ProfilePermitRepository profilePermitRepository;


    @Override
    protected JpaRepository<ProfilePermit, Long> getRepository() {
        return profilePermitRepository;
    }

    public List<ProfilePermit> findAllByEnabledAndProfile(boolean enabled, Profile profile) {
        return profilePermitRepository.findAllByEnabledAndProfileAndPermit_EnabledIsTrueAndProfile_EnabledIsTrue(enabled, profile);
    }
}
