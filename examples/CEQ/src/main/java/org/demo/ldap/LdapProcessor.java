package org.demo.ldap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LdapProcessor implements Processor {
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
}
