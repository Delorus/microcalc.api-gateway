/**
 * @author maksim
 * @since 22.02.2020
 */
module api.service.apiservice {
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires lombok;
    requires jackson.annotations;
    requires spring.web;
    requires reactor.core;
    requires api.service.expr;
}