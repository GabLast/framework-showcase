package com.showcase.application.services.security;

import com.showcase.application.models.security.Profile;
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

    @Override
    protected JpaRepository<Profile, Long> getRepository() {
        return profileRepository;
    }

    @Transactional(readOnly = true)
    public List<Profile> findAllFilter(boolean enabled,
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
    public Integer countAllFilter(boolean enabled,
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
}
