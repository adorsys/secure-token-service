# Proof of Possession

## How to use

### Dependencies

#### Maven

```
    <dependency>
        <groupId>de.adorsys.sts</groupId>
        <artifactId>sts-pop</artifactId>
        <version>${version}</version>
    </dependency>
```

#### Gradle

```
    dependencies {
        compile "de.adorsys.sts:sts-pop:${version}"
    }
```

### Configuration class

```
    @Configuration
    @EnablePOP
    public class YourConfigurationClass {
        ...
    }
```

## Endpoint

```
    $ curl -X GET --header 'Accept: application/json' 'http://${yourhost}/pop'
```
