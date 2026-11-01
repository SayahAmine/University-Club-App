package com.university.club.membership.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class PresidentBootstrapRunner implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(PresidentBootstrapRunner.class);

    private final PresidentBootstrapService bootstrapService;
    private final String presidentEmail;

    public PresidentBootstrapRunner(
            PresidentBootstrapService bootstrapService,
            @Value("${app.bootstrap.president-email:}") String presidentEmail
    ) {
        this.bootstrapService = bootstrapService;
        this.presidentEmail = presidentEmail;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (presidentEmail == null || presidentEmail.isBlank()) {
            log.info("President bootstrap skipped: no email configured.");
            return;
        }

        bootstrapService.bootstrapPresident(presidentEmail);

        log.info("President bootstrap check completed.");
    }
}