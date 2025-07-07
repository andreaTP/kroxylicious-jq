package io.github.andreatp.kroxylicious.jq.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Jackson configuration object for both the jq filters.<br />
 * Both filters perform the same transformation process (though on different types of messages and at
 * different points), only replacing one configured String value with another single configured String value,
 * meaning they can share a single configuration class.<br />
 * <br />
 * This configuration class accepts one String argument: the name of the wasm module to be loaded
 * replaced with.
 */
public class JqFilterConfig {
    private String jqFilter;

    /**
     * Empty constructor for Jackson serialization
     */
    public JqFilterConfig() {
        jqFilter = null;
    }

    /**
     * @param jqFilter the WASM module to be loaded
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public JqFilterConfig(@JsonProperty(required = true) String jqFilter) {
        this.jqFilter = jqFilter;
    }

    /**
     * Returns the configured replacer module
     */
    public String getJqFilter() {
        return jqFilter;
    }
}
