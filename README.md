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

### Configure

Filters can be added and removed by altering the `filters` list in the `jq-proxy-config.yml` file. You can also reconfigure the filters by changing the configuration values in this file.

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
