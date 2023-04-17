package org.demo.ldap;

import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JsonLibrary;

public class LdapRoute extends org.apache.camel.builder.RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:getQuery")
        .routeId("ldap-route")
        .setBody(simple("${header.query}"))
        .to("ldap:ldapConfigBean?base='ou=users,dc=example,dc=org'")
        .to("bean:ldapProcessorBean")
        .choice()
            .when(simple("${body.isEmpty()}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(constant(""))
            .otherwise()
                .marshal().json(JsonLibrary.Jackson)
                .log("Result: ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        .endChoice();

    }
    
}
