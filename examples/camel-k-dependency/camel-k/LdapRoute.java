// camel-k: language=java
// camel-k: dependency=camel-ldap
// camel-k: dependency=mvn:org.demo.ldap:ldap-config:1.0.2
// camel-k: open-api=file:openapi.json

import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;

import org.demo.ldap.LdapProcessor;

public class LdapRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    from("direct:getQuery")
    .routeId("getQuery")
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

    from("direct:getHealth")
    .routeId("getHealth")
    .setBody(simple("cn=user01"))
    .to("ldap:ldapConfigBean?base='ou=users,dc=example,dc=org'")
    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
    .setBody(constant("running"));

  }

}
