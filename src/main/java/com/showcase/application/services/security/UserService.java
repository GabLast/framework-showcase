package com.showcase.application.services.security;

import com.showcase.application.models.security.User;
import com.showcase.application.repositories.security.UserRepository;
import com.showcase.application.services.BaseService;
import com.showcase.application.utils.MyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService extends BaseService<User, Long> {

    private final UserRepository userRepository;
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
                user.setLanguage("en");
                user.setAdmin(true);
            }

            saveAndFlush(user);
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

}
