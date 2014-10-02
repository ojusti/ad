package fr.infologic.vei.sso;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;

import org.apache.hadoop.minikdc.KerberosSecurityTestcase;
import org.junit.Before;
import org.junit.Test;

public class SSOUsingEmbeddedKerberosTest extends KerberosSecurityTestcase
{
    private LoginContext clientLoginContext;
    private EntityName server;
    private LoginContext serverLoginContext;

    @Test
    public void sso_using_embedded_kerberos() throws LoginException
    {//client
        ClientLogin sso = new ClientLogin(clientLoginContext);
        TicketGrantingTicket tgt = sso.login();
        ServiceTicket ticket = tgt.acquireServiceTicket(server);

        //server   
        ServiceLogin server = new ServiceLogin(serverLoginContext);
        ServiceTicketAuthenticator authenticator = server.login();
        EntityName client = authenticator.authenticate(ticket);
        
        assertThat(client).isEqualTo(tgt.getClient());
    }
    
    @Before
    public void makeClient() throws Exception
    {
        String userName = "user";
        File keytab = makePrincipal(userName);
        Subject subject = makeSubject(userName);
        clientLoginContext = new LoginContext("", subject, null, KerberosConfiguration.createClientConfig(userName, keytab));
    }
    
    @Before
    public void makeServer() throws Exception
    {
        String serverName = "service";
        File keytab = makePrincipal(serverName);
        Subject subject = makeSubject(serverName);
        server = EntityName.fromSubject(subject);
        serverLoginContext = new LoginContext("", subject, null, KerberosConfiguration.createServerConfig(serverName, keytab));
    }
    
    private File makePrincipal(String principal) throws Exception
    {
        File keytab = new File(getWorkDir(), String.format("%s.keytab", principal));
        getKdc().createPrincipal(keytab, principal);
        return keytab;
    }

    private static Subject makeSubject(String userName)
    {
        Set<Principal> principals = new HashSet<>();
        principals.add(new KerberosPrincipal(userName));
        return new Subject(false, principals, new HashSet<>(), new HashSet<>());
    }

}
