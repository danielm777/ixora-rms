package com.ixora.ldap;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSchema;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSchema;
import com.novell.ldap.LDAPSearchResults;

public class GetAttributeSchema {

    public static void main( String[] args ) {
    	args = new String[4];
    		args[0] = "www.openldap.com";
    		args[1] = "";
    		args[2] = "";
    		args[3] = "cn=monitor";
          if (args.length != 4) {
            System.err.println("Usage:   java GetAttributeSchema  <host name>" +
            " <login dn> <password> <entry dn>");
            System.err.println("Example: java GetAttributeSchema Acme.com"
                        + " \"cn=admin,o=Acme\" secret \"cn=john,o=Acme\"");
            System.exit(1);
        }


        int ldapPort = LDAPConnection.DEFAULT_PORT;
        int ldapVersion  = LDAPConnection.LDAP_V3;
        int searchScope = LDAPConnection.SCOPE_BASE;
        String ldapHost       = args[0];
        String loginDN        = args[1];
        String password       = args[2];
        String entryDN        = args[3];
        String searchFilter   = "Objectclass=*";
        LDAPConnection lc = new LDAPConnection();
        LDAPSchema dirSchema=null;

        try {
           // connect to the server

            lc.connect( ldapHost, ldapPort );
           // bind to the server

            lc.bind( ldapVersion, loginDN, password.getBytes("UTF8") );

            try    {
                dirSchema=lc.fetchSchema(lc.getSchemaDN());
            } catch( LDAPException e) {
                e.printStackTrace();
            }

            LDAPSearchResults searchResults =
                lc.search(  entryDN,
                            searchScope,
                            searchFilter,
                            null,         // return all attributes

                            false);       // return attrs and values


            LDAPEntry Entry = null;
            try {
                    Entry = searchResults.next();
            }catch(LDAPException e) {
                    System.out.println("Error: " + e.toString());
            }

            LDAPAttributeSet attributeSet = Entry.getAttributeSet();
            Iterator allAttributes = attributeSet.iterator();

            while(allAttributes.hasNext()) {
                    LDAPAttribute attribute =
                                (LDAPAttribute)allAttributes.next();
                    String attributeName = attribute.getName();

                    LDAPAttributeSchema as = dirSchema.getAttributeSchema(attributeName);
                    Enumeration en = as.getStringValues();
                    while(en.hasMoreElements()) {
						System.out.println("GetAttributeSchema.main() - " + en.nextElement());

					}
                    System.out.println("    "
                      + attributeName
                      + "      "
                      + as);
                    System.out.println("\n");
            }
        }
        catch( LDAPException e ) {
            System.out.println( "Error: " + e.toString() );
        }
        catch( UnsupportedEncodingException e ) {
            System.out.println( "Error: " + e.toString() );
        }
        System.exit(0);
    }
}