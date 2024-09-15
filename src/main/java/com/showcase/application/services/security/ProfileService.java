package com.showcase.application.services.security;

import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.Permit;
import com.showcase.application.models.security.Profile;
import com.showcase.application.models.security.ProfilePermit;
import com.showcase.application.repositories.security.ProfileRepository;
import com.showcase.application.services.BaseService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.OffsetBasedPageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProfileService extends BaseService<Profile, Long> {

    private final ProfileRepository profileRepository;
    private final ProfilePermitService profilePermitService;

    @Override
    protected JpaRepository<Profile, Long> getRepository() {
        return profileRepository;
    }

    @Transactional(readOnly = true)
    public List<Profile> findAllFilter(Boolean enabled,
                                       String name, String description,
                                       Integer limit, Integer offset, Sort sort) {
        try {

            return profileRepository.findAllFilter(
                    enabled,
                    name,
                    description,
                    sort == null ? new OffsetBasedPageRequest(limit, offset) : new OffsetBasedPageRequest(limit, offset, sort)
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }


    @Transactional(readOnly = true)
    public Integer countAllFilter(Boolean enabled,
                                  String name, String description) {
        try {

            return profileRepository.countAllFilter(
                    enabled,
                    name,
                    description
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public Profile create(Profile profile, List<Permit> list, UserSetting userSetting) {
        try {

            if (profile == null) {
                throw new MyException(MyException.CLIENT_ERROR, "error.null", userSetting.getLocale());
            }

            if (list == null || list.isEmpty()) {
                throw new MyException(MyException.CLIENT_ERROR, "error.empty.details", userSetting.getLocale());
            }

            profile = saveAndFlush(profile);

            for (ProfilePermit profilePermit : profilePermitService.findAllByEnabledAndProfile(true, profile)) {
                profilePermitService.delete(profilePermit);
            }

            for (Permit permit : list) {
                profilePermitService.saveAndFlush(new ProfilePermit(profile, permit));
            }

            return profile;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public void delete(Profile profile, UserSetting userSetting) {
        try {

            if (profile == null) {
                throw new MyException(MyException.CLIENT_ERROR, "error.null", userSetting.getLocale());
            }

            for (ProfilePermit profilePermit : profilePermitService.findAllByEnabledAndProfile(true, profile)) {
                profilePermitService.delete(profilePermit);
            }

            disable(profile);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Profile> findAllByEnabled(boolean enabled) {
        return profileRepository.findAllByEnabled(enabled);
    }
}
