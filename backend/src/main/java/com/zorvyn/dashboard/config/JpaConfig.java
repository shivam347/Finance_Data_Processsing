package com.zorvyn.dashboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/* When i try to register then createdat column shows null error , however i already
applied annotation called EntityAuditingListener, which fills automatically , but we have
defined this globally so that it works fine , without this on register give 403 error */

@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
