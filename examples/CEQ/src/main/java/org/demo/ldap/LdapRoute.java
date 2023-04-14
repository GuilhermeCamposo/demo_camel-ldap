package org.demo.ldap;

import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;

public class LdapRoute extends org.apache.camel.builder.RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().bindingMode(RestBindingMode.off);

        rest("/ldap")
            .get()
                .produces("application/json")
                .to("direct:ldap-route");

        from("direct:ldap-route")
        .routeId("ldap-route")
        .setBody(simple("${header.query}"))
        .to("ldap:ldapBean?base='ou=users,dc=example,dc=org'")
        .process(new LdapProcessor())
        .marshal().json(JsonLibrary.Jackson)
        .log("Result: ${body}")
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

    }
    
}
