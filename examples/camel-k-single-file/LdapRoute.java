// camel-k: language=java
// camel-k: build-property=quarkus.naming.enable-jndi=true
// camel-k: property=file:application.properties
// camel-k: dependency=camel-ldap
// camel-k: resource=file:jndi.properties

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultRegistry;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;


public class LdapRoute extends RouteBuilder {

  private static final Logger LOG = Logger.getLogger(LdapRoute.class);

  @Override
  public void configure() throws Exception {

    Supplier<Object> supplier = new Supplier<Object>() {
       
        @Override
        public Object get() {
            try{
                LOG.info("setting LDAP Server Properties");
                return new InitialDirContext();
            } catch( Exception e ){
                LOG.error("Error getting JNDI configuration", e);
                return null;
            }
        }
    };

    getCamelContext().getRegistry().bindAsPrototype("ldapBean", InitialDirContext.class, supplier);
    
    restConfiguration().bindingMode(RestBindingMode.off);

    rest("/ldap")
        .get()
            .produces("application/json")
            .to("direct:ldap-route");

    from("direct:ldap-route")
    .routeId("ldap-route")
    .setBody(simple("${header.query}"))
    .to("ldap:ldapBean?base='ou=users,dc=example,dc=org'")
    .process(new Processor() {
        @Override
        public void process(Exchange exchange) throws Exception {
            ArrayList<SearchResult> body = exchange.getIn().getBody(ArrayList.class);
            List<HashMap<String,Object>> records = new ArrayList<> ();

            if(body != null){
                for(SearchResult result : body){
                    NamingEnumeration enumeration = result.getAttributes().getAll();
                    HashMap<String,Object>  map = new HashMap<>();
                    while (enumeration.hasMore()){
                        Attribute att = (Attribute) enumeration.next();
                        map.put(att.getID(),att.get());
                    }
                    records.add(map);
                }
            }
            exchange.getIn().setBody(records);
        }
    })
    .marshal().json(JsonLibrary.Jackson)
    .log("Result: ${body}")
    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

  }

}
