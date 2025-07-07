package io.github.andreatp.kroxylicious.jq.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JqFilterConfigTest {

    private static final String CONFIG_JQ_FILTER = ".";

    @Test
    void validateJqFetchResponseConfigTest() {
        JqFilterConfig config = new JqFilterConfig(CONFIG_JQ_FILTER);
        assertThat(config.getJqFilter()).isEqualTo(CONFIG_JQ_FILTER);
    }

}
