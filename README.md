# Kroxylicious Jq Filter

This jq filter project provides an example usage of [custom filters](https://kroxylicious.io/kroxylicious/#_custom_filters) in Kroxylicious.
Here we leverage a pure Java [Jq4j](https://github.com/roastedroot/jq4j) to build a filter that can be dynamically reconfigured with arbitrary `jq` expression filters.

To learn more about Kroxylicious, visit the [docs](https://kroxylicious.io/kroxylicious).
To learn more about Jq, visit the [docs](https://jqlang.org/).

## Getting started

### Build

Building the jq project is easy!

```shell
mvn verify
```

### Run with a Kroxylicious Distribution

Build as above and then:

1. docker run -d -p 9092:9092 apache/kafka-native:4.0.0
2. obtain and unpack a [kroxylicious-app distribution](https://github.com/kroxylicious/kroxylicious/releases) (tested with 0.13.0):
3. run the proxy:
   ```shell
   KROXYLICIOUS_DISTRIBUTION_DIR=~/Downloads/kroxylicious-app-0.13.0 # replace with your installation directory
   export KROXYLICIOUS_CLASSPATH="$(pwd)/target/kroxylicious-jq-0.0.1-SNAPSHOT-bin/*"
   ${KROXYLICIOUS_DISTRIBUTION_DIR}/bin/kroxylicious-start.sh --config ./jq-proxy-config.yml
   ```

Your proxy should now be running and ready to be connected to using bootstrap server `localhost:9192` from a kafka client.

### Configure

Filters can be added and removed by altering the `filterDefinitions` and `defaultFilters` lists in the `jq-proxy-config.yml` file. You can also reconfigure the filters by changing the configuration values in this file.

The **JqFetchResponseFilter** and **JqProduceRequestFilter** each have two configuration values that must be specified for them to work:

 - `jqFilter` - the string represent the jq filter to be executed

To configure the **JqProduceRequestFilter** use:

```yaml
filters:
  - type: JqProduceRequestFilterFactory
    config:
      jqFilter: .
```

This means that it will execute the `.` filter using `jq`. 

To configure the **JqFetchResponseFilter** use:

```yaml
filters:
  - type: JqFetchResponseFilterFactory
    config:
      jqFilter: .realnames as $names | .posts[] | {title, author: $names[.author]}
```

This means that it will execute the specified filter query.
