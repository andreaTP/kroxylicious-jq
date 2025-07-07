package io.github.andreatp.kroxylicious.jq;

import io.github.andreatp.kroxylicious.jq.config.JqFilterConfig;
import io.kroxylicious.proxy.filter.FilterFactory;
import io.kroxylicious.proxy.filter.FilterFactoryContext;
import io.kroxylicious.proxy.plugin.Plugin;
import io.kroxylicious.proxy.plugin.Plugins;

@Plugin(configType = JqFilterConfig.class)
public class JqProduceRequestFilterFactory implements FilterFactory<JqFilterConfig, JqFilterConfig> {
    @Override
    public JqFilterConfig initialize(FilterFactoryContext context, JqFilterConfig config) {
        return Plugins.requireConfig(this, config);
    }

    @Override
    public JqProduceRequestFilter createFilter(FilterFactoryContext context, JqFilterConfig configuration) {
        return new JqProduceRequestFilter(configuration);
    }

}
