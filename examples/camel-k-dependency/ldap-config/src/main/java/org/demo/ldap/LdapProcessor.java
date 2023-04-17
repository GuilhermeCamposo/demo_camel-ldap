package org.demo.ldap;

import org.apache.camel.Exchange;

import io.quarkus.arc.Unremovable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ApplicationScoped
@Unremovable
@Named("ldapProcessorBean")
public class LdapProcessor {

    
    public void process(Exchange exchange) throws Exception {
        ArrayList<SearchResult> body = exchange.getIn().getBody(ArrayList.class);

        if(body != null && !body.isEmpty()  ){
            List<HashMap<String,Object>> records = new ArrayList<> ();
            for(SearchResult result : body){
                NamingEnumeration enumeration = result.getAttributes().getAll();
                HashMap<String,Object>  map = new HashMap<>();

                while (enumeration.hasMore()){
                    Attribute att = (Attribute) enumeration.next();
                    map.put(att.getID(),att.get());
                }
                records.add(map);
            }
            exchange.getIn().setBody(records);
        }
        
    }
}
