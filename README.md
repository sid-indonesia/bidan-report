[![Run Tests & Build](https://github.com/sid-indonesia/bidan-report/actions/workflows/build.yml/badge.svg)](https://github.com/sid-indonesia/bidan-report/actions/workflows/build.yml)
[![Build & Release](https://github.com/sid-indonesia/bidan-report/actions/workflows/publish.yml/badge.svg)](https://github.com/sid-indonesia/bidan-report/actions/workflows/publish.yml)

# bidan-report

**Repo Owner:** Levi [@muhammad-levi](https://github.com/muhammad-levi)

Spring Boot App to export data converted by [db-converter-Bidan](https://github.com/sid-indonesia/db-converter/tree/app/bidan) into CSV and do some validations regarding the data. Will be refactored later, but currently integration to WhatsApp Business API (WABA) provided by [Qontak](https://www.qontak.com/id) reside in this Maven project as well.

## Overview

`bidan-report` app is report generation component, written in Java as a RESTful web application integrated with [Keycloak](https://www.keycloak.org) for Identity and Access Management (OAuth 2.0), using [Spring Boot](https://spring.io/projects/spring-boot) framework.

## Deployment
https://docs.spring.io/spring-boot/docs/2.4.6/reference/html/deployment.html#deployment-systemd-service


## Use

### Development

Maven is used as the software project management tool.

### Building

Use `mvn clean package` to build the war file. Maven will build war with dependencies and place them in the target directory.

### Running

Current version of the `bidan-report` is a standalone restful web application that expects some configurations at the runtime:
- Spring profiles in the `spring.profiles` configuration property, `prod` profile is available.
- DB user credentials in the `spring.datasource` configuration property
- Keycloak configuration, you can refer to [the basic, mandatory configuration](https://www.baeldung.com/spring-boot-keycloak#keycloak-config).
- Integration to Qontak's WABA required configuration properties such as: 
  ```
  spring:
    security:
      oauth2:
        client:
          registration:
            keycloak:
              client-id: oauth-client
              client-secret: "b77be48f-33b4-4d5b-9326-8fc54f44dd74"

  qontak:
    base-url: https://chat-service.qontak.com
    client-id: service_accounts_enabled_client_id
    client-secret: hard-to-crack-client-secret
    username: supervisor_user
    password: hard-to-crack-user-password
    whats-app:
      pregnant-woman-message-template-id: 89ede237-16f0-40d1-8a37-7f59446895ca
      non-pregnant-woman-message-template-id: 89ede237-16f0-40d1-8a37-7f59446895cb
      visit-reminder-message-template-id: 89ede237-16f0-40d1-8a37-7f59446895cc
      visit-reminder-with-header-image-message-template-id: 89ede237-16f0-40d1-8a37-7f59446895cd
      visit-reminder-interval-in-days: 1
      visit-interval-in-days: 30
      pregnancy-gap-message-template-id: 89ede237-16f0-40d1-8a37-7f59446895ce
      health-education-message-template-id: 89ede237-16f0-40d1-8a37-7f59446895cf
      channel-integration-id: 89ede237-16f0-40d1-8a37-7f59446895c1
      district-health-office-name: Jakarta Pusat
      health-education-contact-list-csv-absolute-file-name: /tmp/bidan-report/csv/contacts_health-education.csv

  scheduling:
    intro-message:
      fixed-rate-in-ms: 1800000 # 30 minutes
      initial-delay-in-ms: 1800000 # 30 minutes
    visit-reminder:
      cron: "0 30 18 * * ?" # At 18:30
      zone: Asia/Jakarta
    pregnancy-gap:
      fixed-rate-in-ms: 3600000 # 1 hour
      initial-delay-in-ms: 3600000 # 1 hour
    health-education:
      cron: "0 0 13 * * Mon" # At 01:00 PM, only on Monday
      zone: Asia/Jakarta
  hapi-fhir-server:
    base-url: https://hapi-fhir.example.org/fhir
  ```
- [Optional] Spring liquibase configuration property if you would like to use different DB user for the DB migration scripts, or anything else that Spring Liquibase supports.

By default, application will run on port 8081. Alternative port can be specified using `server.port` option.

`java -jar bidan-report-Bidan.war --server.port=9000`

## TODO

- Refactor integration to Qontak's WABA into a new component
