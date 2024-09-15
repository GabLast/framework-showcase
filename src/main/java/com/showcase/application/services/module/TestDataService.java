package com.showcase.application.services.module;

import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.repositories.module.TestDataRepository;
import com.showcase.application.services.BaseService;
import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.OffsetBasedPageRequest;
import com.showcase.application.views.broadcaster.module.BroadcasterTestData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TestDataService extends BaseService<TestData, Long> {
    private final TestDataRepository testDataRepository;

    @Override
    protected JpaRepository<TestData, Long> getRepository() {
        return testDataRepository;
    }

    @Transactional(readOnly = true)
    public Optional<TestData> getTestDataById(Long id) {
        return testDataRepository.getTestDataById(id);
    }

    @Transactional(readOnly = true)
    public List<TestData> findAllFilter(boolean enabled, String timeZoneId,
                                        String word, String description, TestType testType, LocalDate dateStart, LocalDate dateEnd,
                                        Integer limit, Integer offset, Sort sort) {
        try {

            TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
            if (timeZone == null) {
                timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
            }

            return testDataRepository.findAllFilter(
                    enabled,
                    word,
                    description,
                    testType,
                    dateStart != null ? Date.from(dateStart.atStartOfDay().atZone(timeZone.toZoneId()).toInstant()) : null,
                    dateEnd != null ? Date.from(dateEnd.atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toInstant()) : null,
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
    public Integer countAllFilter(boolean enabled, String timeZoneId,
                                  String word, String description, TestType testType, LocalDate dateStart, LocalDate dateEnd) {
        try {

            TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
            if (timeZone == null) {
                timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
            }

            return testDataRepository.countAllFilter(
                    enabled,
                    word,
                    description,
                    testType,
                    dateStart != null ? Date.from(dateStart.atStartOfDay().atZone(timeZone.toZoneId()).toInstant()) : null,
                    dateEnd != null ? Date.from(dateEnd.atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toInstant()) : null
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

    public TestData saveTestData(TestData testData, UserSetting userSetting) {
        try {

            if (testData == null) {
                throw new MyException(MyException.CLIENT_ERROR, "error.null", userSetting.getLocale());
            }

            testData = saveAndFlush(testData);

            TestData finalObject = testData;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    TransactionSynchronization.super.afterCommit();

                    BroadcasterTestData.getInstance().broadcast(finalObject);
                }
            });

            return testData;

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
