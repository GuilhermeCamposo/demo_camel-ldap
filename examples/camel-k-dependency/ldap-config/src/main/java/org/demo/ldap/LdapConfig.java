package org.demo.ldap;

import io.quarkus.arc.Unremovable;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jboss.logging.Logger;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

@Dependent
@Unremovable
public class LdapConfig {

    private static final Logger LOG = Logger.getLogger(LdapConfig.class);

    @Produces()
    @Named("ldapBean")
    public InitialDirContext LdapBean() throws NamingException {

        LOG.info("setting LDAP Server Properties");

        try{
            return new InitialDirContext();
        } catch( Exception e ){
            LOG.error("Error getting JNDI configuration", e);
            return null;
        }
    }

}
