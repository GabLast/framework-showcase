package com.showcase.application.services.security;

import com.showcase.application.models.security.Permit;
import com.showcase.application.repositories.security.PermitRepository;
import com.showcase.application.services.BaseService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.Utilities;
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
public class PermitService extends BaseService<Permit, Long> {

    private final PermitRepository permitRepository;

    @Override
    protected JpaRepository<Permit, Long> getRepository() {
        return permitRepository;
    }

    public void bootstrap() {
        try {
            Permit testData = create(null, Permit.MENU_TEST_DATA, "Menu for Test Data");
            create(testData, Permit.TEST_DATA_CREATE, "Create on Test Data");
            create(testData, Permit.TEST_DATA_EDIT, "Create on Test Data");
            create(testData, Permit.TEST_DATA_VIEW, "Create on Test Data");
            create(testData, Permit.TEST_DATA_DELETE, "Create on Test Data");

            log.info("Created Permits");
        } catch (MyException e) {
            log.error(e.getMessage(), e);
            throw new MyException(MyException.SERVER_ERROR, e.getMessage());
        }
    }

    private Permit create(Permit father, String code, String description) {
        Permit tmp = findFirstByCodeAndEnabled(true, code);
        if (tmp == null) {
            tmp = saveAndFlush(new Permit(father, code, Utilities.capitalizeEveryLetterOfString(code.replaceAll("_", " ")), description));
        }
        return tmp;
    }

    public Permit findFirstByCodeAndEnabled(boolean enabled, String code) {
        return permitRepository.findFirstByEnabledAndCode(true, code);
    }

    public List<Permit> findAllByEnabled(boolean enabled) {
        return permitRepository.findAllByEnabled(enabled);
    }

    public List<Permit> findAllByEnabledAndPermitFatherIsNull(boolean enabled) {
        return permitRepository.findAllByEnabledAndPermitFatherIsNull(enabled);
    }
    public List<Permit> findAllByEnabledAndPermitFather(boolean enabled, Permit father) {
        return permitRepository.findAllByEnabledAndPermitFather(enabled, father);
    }

}
