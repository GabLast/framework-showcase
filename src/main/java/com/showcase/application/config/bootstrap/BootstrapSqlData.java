package com.showcase.application.config.bootstrap;

import com.showcase.application.services.module.TestDataService;
import com.showcase.application.utils.Utilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

@Component
@Order()
@RequiredArgsConstructor
@Slf4j
public class BootstrapSqlData implements ApplicationRunner {


    private final TestDataService testDataService;

    private static Connection connection = null;
    private static final String JDBC_URL = "jdbc:h2:~/frameworkshowcase";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    public void prepareConnection() throws Exception {
        connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static void closeConnection() throws Exception {
        connection.close();
    }

    public void execute() throws Exception {
        String path = new File(Objects.requireNonNull(ClassLoader.getSystemClassLoader()
                .getResource("data/test_data.sql")).getFile()).toPath().toString();
        Utilities.runScript(path, connection);
        log.info("SQL Script has been Loaded");
    }

    private void runScript() {
        try {
            prepareConnection();
            execute();
            closeConnection();
        } catch (Exception e) {
            log.info("SQL Script error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            if(testDataService.count() == 0) {
                runScript();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
