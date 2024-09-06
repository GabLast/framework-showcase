package com.showcase.application.services.other;

import com.showcase.application.models.other.TestType;
import com.showcase.application.repositories.other.TestTypeRepository;
import com.showcase.application.services.BaseService;
import com.showcase.application.utils.MyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
