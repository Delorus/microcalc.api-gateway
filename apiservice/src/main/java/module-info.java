/**
 * @author maksim
 * @since 22.02.2020
 */
module api.service.apiservice {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.kafka;
    requires kafka.clients;
    requires api.service.expr;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.classmate; // need for Spring's ValidationAutoConfiguration
//    requires lombok; // not working https://github.com/rzwitserloot/lombok/issues/2125

    exports ru.sherb.microcalc.apiservice to spring.boot.devtools, spring.beans, spring.context;
    exports ru.sherb.microcalc.apiservice.kafka to spring.beans, spring.context;
    exports ru.sherb.microcalc.apiservice.service to spring.beans, spring.context;
    exports ru.sherb.microcalc.apiservice.endpoint.v1 to spring.beans, spring.context;

    opens ru.sherb.microcalc.apiservice to spring.core;
    opens ru.sherb.microcalc.apiservice.kafka to spring.core;
}