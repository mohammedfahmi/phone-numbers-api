# phone-numbers-api

phone-numbers-api is a private dockerized microservice that serve customer phone numbers operations.


To build the application run the following command from the repository root

```bash
mvn
```

to build without running the unit and integration tests phoneNumberConfiguration.yaml

```bash
mvn -DskipTests -DskipITs
```

# Generated Code

your models and controllers are generated from the Open-api spec file check **phoneNumbers-api.yaml** in .`/src/main/resources`,
when you first check out the repository you will find errors because the generated files are not there yet,
so you need to run `mvn` or `mvn clean compile` to generate these files, 
we use openapi-generator-maven-plugin to generate the controllers and models from the Open-api spec file, 
we use mustache templates to customize generated models, you can customize all generated files, check the default mustache templates of `OpenAPITools/openapi-generator project` for Spring boot [here](https://github.com/OpenAPITools/openapi-generator/tree/v4.3.1/modules/openapi-generator/src/main/resources/JavaSpring).

your build will also generate the `jpaMetaModelEntities`, you need to add them to your source code after your first build,
the generated jpaMetaModelEntities are uses in our JPA Specification Criteria builder,
you do that by marking this directory `target/generated-sources/annotations` as a `Generated Source Root` in your IDE,
in intellij-Idea you do that by simply right-clicking on the directory, in the menu go to `Mark Directory as` and then select `Generated Source Root`.

# Run Locally

To run the application in a docker container run the following script from the repository root

```bash
bash start-up.sh
```

# Debugging Locally

The phone-numbers-api docker container that you started in the `Run Locally` step exposes port `5005` for remote debugging, so you can attach your remote debugger in your IDE to it.

#Phone-numbers validation Configuration

the phone number validation configuration is added in **phoneNumberConfiguration.yaml** in your resource directory,
you can add phone number countries and states configurations in this file, and they will be dynamically loaded in your application and used to validate the customer phone numbers data, just make sure to update the corresponding Unit tests.

#Liquibase

we use liquibase scripts to do any database schema changes, you can add new script in this directory `src\main\resources\db\changelog\changes` with a number at the beginning of the file name greater than the biggest script number by 1.

# Security

basic authentication secures The callback endpoint. Username and password set by the following properties:

```yaml
endpoint:
  security:
    user:
      name: ${auth.name}
      password: ${auth.password}
      role: ${auth.role}
      management-context: ${management.server.base-path}
```
check **application.yaml** in ./phone-numbers-api/src/main/resources


Cors allow origin headers are added to request coming from Urls in `white-list-origins` property,
to add a new Origin simply add one to the list.

```yaml
endpoint:
  security:
    white-list-origins:
      origins:
        -
          "http://localhost:3000"
        -
          "http://localhost:3001"
```
check **application.yaml** in ./phone-numbers-api/src/main/resources

# Testing

To run all tests, execute the following command from the repository root

```bash
mvn
```
 we have two types of Tests:
 - unit-tests: 
   - the files that end with Test.
   - they are run by surefire maven plugin in the maven test phase.
   - to skip them add `-DskipTests` after your mvn command.
 - integration-tests:
   - the files that end with IT.
   - they are run by failsafe maven plugin in the maven verify life-cycle and integration-tests goal.
   - to skip them add `-DskipITs` after your mvn command.
   - the tests run across docker instance of this application.  

 the test code coverage are run by `jacoco-maven-plugin` and you can find the test reports in `\target\site\jacoco-aggregate` folder after a successful build, your code coverage must pass coverage ratio of 90% for a successful build.