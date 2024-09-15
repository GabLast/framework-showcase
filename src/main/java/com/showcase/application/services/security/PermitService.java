package com.showcase.application.services.security;

import com.showcase.application.models.security.Permit;
import com.showcase.application.repositories.security.PermitRepository;
import com.showcase.application.services.BaseService;
import com.showcase.application.utils.MyException;
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

            Permit processes = create(null, Permit.PROCESSES_MODULE, "module.processes", "module.processes");

            Permit testData = create(processes, Permit.MENU_TEST_DATA, "menu.testdata", "menu.testdata");
            create(testData, Permit.TEST_DATA_CREATE, "create,menu.testdata.action", "create,menu.testdata.action");
            create(testData, Permit.TEST_DATA_EDIT, "edit,menu.testdata.action", "edit,menu.testdata.action");
            create(testData, Permit.TEST_DATA_VIEW, "view,menu.testdata.action", "view,menu.testdata.action");
            create(testData, Permit.TEST_DATA_DELETE, "delete,menu.testdata.action", "delete,menu.testdata.action");

            //********************************************************************

            Permit security = create(null, Permit.SECURITY_MODULE, "module.security", "module.security");

            Permit profiles = create(security, Permit.MENU_PROFILE, "menu.profile", "menu.profile");
            create(profiles, Permit.PROFILE_CREATE, "create,menu.profile.action", "create,menu.profile.action");
            create(profiles, Permit.PROFILE_EDIT, "edit,menu.profile.action", "edit,menu.profile.action");
            create(profiles, Permit.PROFILE_VIEW, "view,menu.profile.action", "view,menu.profile.action");
            create(profiles, Permit.PROFILE_DELETE, "delete,menu.profile.action", "delete,menu.profile.action");

            Permit users = create(security, Permit.MENU_USER, "menu.user", "menu.user");
            create(users, Permit.USER_CREATE, "create,menu.user.action", "create,menu.user.action");
            create(users, Permit.USER_EDIT, "edit,menu.user.action", "edit,menu.user.action");
            create(users, Permit.USER_VIEW, "view,menu.user.action", "view,menu.user.action");
            create(users, Permit.USER_DELETE, "delete,menu.user.action", "delete,menu.user.action");

            //********************************************************************

            Permit reports = create(null, Permit.REPORTS_MODULE, "module.reports", "module.reports");
            create(reports, Permit.REPORT_TEST_DATA, "report.testdata", "report.testdata");

            log.info("Created Permits");
        } catch (MyException e) {
            log.error(e.getMessage(), e);
            throw new MyException(MyException.SERVER_ERROR, e.getMessage());
        }
    }

    private Permit create(Permit father, String code, String nameI18, String descriptionI18) {
        try {
            Permit tmp = findFirstByCodeAndEnabled(true, code);
            if (tmp == null) {
//            Utilities.capitalizeEveryLetterOfString(code.replaceAll("_", " "))
                tmp = saveAndFlush(new Permit(father, code, nameI18, descriptionI18));
            }
            return tmp;
        } catch (MyException e) {
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public Permit findFirstByCodeAndEnabled(boolean enabled, String code) {
        return permitRepository.findFirstByEnabledAndCode(enabled, code);
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
