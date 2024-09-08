package com.showcase.application.services.other;

import com.showcase.application.models.other.TestData;
import com.showcase.application.models.other.TestType;
import com.showcase.application.repositories.other.TestDataRepository;
import com.showcase.application.services.BaseService;
import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.OffsetBasedPageRequest;
import com.showcase.application.utils.Utilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
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
    public List<TestData> findAllFilter(boolean enabled, String timeZoneId,
                                        String word, String description, TestType testType, LocalDate dateStart, LocalDate dateEnd,
                                        Integer limit, Integer offset, Sort sort) {
        try {

            TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
            if (timeZone == null) {
                timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
            }

            System.out.println("date: " + Utilities.formatDate(Date.from(dateStart.atStartOfDay().atZone(timeZone.toZoneId()).toInstant()), "", timeZoneId));

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
}
