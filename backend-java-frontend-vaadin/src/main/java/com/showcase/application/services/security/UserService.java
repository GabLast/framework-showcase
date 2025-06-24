package com.showcase.application.services.security;

import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.ProfileUser;
import com.showcase.application.models.security.User;
import com.showcase.application.repositories.security.UserRepository;
import com.showcase.application.services.BaseService;
import com.showcase.application.utils.OffsetBasedPageRequest;
import com.showcase.application.utils.TranslationProvider;
import com.showcase.application.utils.exceptions.MyException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService extends BaseService<User, Long> {

    private final UserRepository userRepository;
    private final ProfileUserService profileUserService;
    private final TranslationProvider translationProvider;
    @Getter
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    public void bootstrap() {
        try {
            User user = findByUsername("admin");
            if (user == null) {
                user = new User();
                user.setUsername("admin");
                user.setPassword(passwordEncoder.encode("123"));
                user.setName("Administrator");
                user.setAdmin(true);
                saveAndFlush(user);
            }

            log.info("Created admin user");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public User findByUsername(String string) {
        try {
            return userRepository.findByUsername(string);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public User findByMail(String string) {
        try {
            return userRepository.findByMail(string);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public User findByUsernameOrMail(String string) {
        try {
            return userRepository.findByUsernameOrMail(string);
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
    public List<User> findAllFilter(Boolean enabled, String name, String mail, Boolean admin, int limit, int offset, Sort sort) {
        try {
            return userRepository.findAllFilter(
                    enabled,
                    name,
                    mail,
                    admin,
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
    public Integer countAllFilter(Boolean enabled, String name, String mail, Boolean admin) {
        try {
            return userRepository.countAllFilter(
                    enabled,
                    name,
                    mail,
                    admin
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

    public User saveUser(User user, List<ProfileUser> details, List<ProfileUser> listDelete, UserSetting userSetting) {
        try {

            user = createUser(user, userSetting);

            for (ProfileUser profileUser : listDelete) {
                profileUserService.delete(profileUser);
            }

            for (ProfileUser detail : details) {
                detail.setUser(user);
            }
            profileUserService.saveAllAndFlush(details);

            return user;
        } catch (Exception e) {
//            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public User createUser(User user, UserSetting userSetting) {
        try {

            if (user == null) {
                throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.value.null", userSetting.getLocale()));
            }

            User tmp = get(user.getId()).orElse(user);

            String username = StringUtils.deleteWhitespace(user.getUsername()).toLowerCase();
            String mail = StringUtils.deleteWhitespace(user.getMail()).toLowerCase();

            if (StringUtils.isBlank(username)) {
                throw new MyException(MyException.CLIENT_ERROR,
                        translationProvider.getTranslation(
                                "error.empty",
                                userSetting.getLocale(),
                                translationProvider.getTranslation("form.user.username", userSetting.getLocale()))
                );
            }

            if (StringUtils.isBlank(mail)) {
                throw new MyException(MyException.CLIENT_ERROR,
                        translationProvider.getTranslation(
                                "error.empty",
                                userSetting.getLocale(),
                                translationProvider.getTranslation("form.user.mail", userSetting.getLocale()))
                );
            }

            if (findByUsername(user.getUsername()) != null && !tmp.getId().equals(user.getId())) {
                throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.usernametaken", userSetting.getLocale()));
            }

            if (findByMail(user.getMail()) != null && !tmp.getId().equals(user.getId())) {
                throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.mailtaken", userSetting.getLocale()));
            }

            user.setUsername(username);
            user.setMail(mail);
            if (!user.getPassword().equals(tmp.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user = saveAndFlush(user);

            return user;
        } catch (Exception e) {
//            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public void delete(User user, UserSetting userSetting) {
        try {

            if (user == null) {
                throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.value.null", userSetting.getLocale()));
            }

            for (ProfileUser profile : profileUserService.findAllByEnabledAndUser(true, user)) {
                profileUserService.delete(profile);
            }

            disable(user);

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
