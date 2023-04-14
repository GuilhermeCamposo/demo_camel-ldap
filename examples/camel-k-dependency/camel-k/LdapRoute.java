// camel-k: language=java
// camel-k: dependency=camel-ldap
// camel-k: dependency=mvn:org.demo.ldap:ldap-config:1.0.1

import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;

import org.demo.ldap.LdapProcessor;

public class LdapRoute extends RouteBuilder {

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
