package com.showcase.application.services.module;

import com.showcase.application.models.module.TestType;
import com.showcase.application.repositories.module.TestTypeRepository;
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
public class TestTypeService extends BaseService<TestType, Long> {

    private final TestTypeRepository testTypeRepository;

    @Override
    protected JpaRepository<TestType, Long> getRepository() {
        return testTypeRepository;
    }

    public void bootstrap() {
        try {
            for(TestType.TestTypeCode value: TestType.TestTypeCode.values()){
                TestType tipoDeal = testTypeRepository.findByCode(value.getCode());
                if(tipoDeal == null) {
                    tipoDeal = new TestType();
                    tipoDeal.setCode(value.getCode());
                    tipoDeal.setName(value.getName());
                    tipoDeal.setDescription(value.getDescription());
                    saveAndFlush(tipoDeal);
                }
            }
            log.info("Created TestType Pre-defined values");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public List<TestType> findAllByEnabled(boolean a) {
        return testTypeRepository.findAllByEnabled(true);
    }
}
