package com.showcase.application.services.configuration;

import com.showcase.application.models.configuration.Parameter;
import com.showcase.application.repositories.configuration.ParameterRepository;
import com.showcase.application.services.BaseService;
import com.showcase.application.utils.MyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParameterService extends BaseService<Parameter, Long> {

    private final ParameterRepository parameterRepository;

    @Override
    protected JpaRepository<Parameter, Long> getRepository() {
        return parameterRepository;
    }

    public void bootstrap() {
        try {
            create(Parameter.REMEMBER_ME_TOKEN,"param.remembermetoken", UUID.randomUUID().toString());

            log.info("Parameters created");
        } catch (MyException e) {
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    private void create(String code, String nameI18, String value) {
        try {
            Parameter tmp = findFirstByEnabledAndCode(true, code);
            if (tmp == null) {
//            Utilities.capitalizeEveryLetterOfString(code.replaceAll("_", " "))
                tmp = saveAndFlush(new Parameter(code, nameI18, value));
            }
//            return tmp;
        } catch (MyException e) {
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public Parameter findFirstByEnabledAndCode(boolean enabled, String code) {
        return parameterRepository.findFirstByEnabledAndCode(enabled, code);
    }

}
