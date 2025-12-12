package com.doanltmmt.Backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Ensure DB schema matches the intended JPA mapping for TopicRegistration.approved (nullable).
 * This runs once at startup in dev to relax NOT NULL constraint if present.
 */
@Component
public class SchemaFixer {
    private static final Logger log = LoggerFactory.getLogger(SchemaFixer.class);

    private final JdbcTemplate jdbcTemplate;

    public SchemaFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void relaxApprovedNullConstraint() {
        try {
            // MySQL: change approved column allowing NULL. Works if column is TINYINT(1) / BOOLEAN.
            jdbcTemplate.execute("ALTER TABLE topic_registration MODIFY COLUMN approved TINYINT(1) NULL");
            log.info("SchemaFixer: ensured topic_registration.approved allows NULL");
        } catch (Exception ex) {
            // Ignore if fails (e.g., no permission or already nullable). Log at debug for awareness.
            log.debug("SchemaFixer: skipping approved column alter (reason: {})", ex.getMessage());
        }
    }
}

