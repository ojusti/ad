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

public class LoginUsingEmbeddedKerberosTest extends KerberosSecurityTestcase
{
    private LoginContext user;
    private EntityName serviceName;
    private ServiceTicketAuthenticator service;

    @Before
    public void makeEntities() throws Exception
    {
        LoginContext targetServer = makeServer("service");
        serviceName = EntityName.fromSubject(targetServer.getSubject());
        service = loginServer(targetServer);
        
        user = makeClient("user");
    }
    
    @Test
    public void login_using_embedded_kerberos() throws LoginException
    {
        ServiceTicket ticket = userAcquiresTicket(user, serviceName);

        EntityName client = service.authenticate(ticket);
        
        assertThatClientIsUser(client, user);
    }

    @Test(expected=LoginException.class)
    public void authentication_refused_by_another_server() throws Exception
    {
        ServiceTicket otherServiceTicket = userAcquiresTicket(user, otherServiceName());

        service.authenticate(otherServiceTicket);
    }

    @Test
    public void authenticate_another_client() throws Exception
    {
        ServiceTicket ticket = userAcquiresTicket(user, serviceName);
        EntityName client = service.authenticate(ticket);
        
        LoginContext anotherUser = anotherUser();
        ServiceTicket anotherUserTicket = userAcquiresTicket(anotherUser, serviceName);
        EntityName anotherClient = service.authenticate(anotherUserTicket);
        
        assertThatClientIsUser(client, user);
        assertThatClientIsUser(anotherClient, anotherUser);
    }
    
    @Test(expected=LoginException.class)
    public void ticket_refused_for_inexistent_service() throws LoginException
    {
        userAcquiresTicket(user, inexistentService());
    }

    private static EntityName inexistentService()
    {
        return new EntityName("inexistent_service");
    }

    private static ServiceTicket userAcquiresTicket(LoginContext user, EntityName service) throws LoginException
    {
        return new ClientLogin(user).login().acquireServiceTicket(service);
    }
    
    private static void assertThatClientIsUser(EntityName client, LoginContext user)
    {
        assertThat(client).isEqualTo(EntityName.fromSubject(user.getSubject()));
    }
    
    private LoginContext anotherUser() throws Exception
    {
        return makeClient("another_user");
    }

    private EntityName otherServiceName() throws Exception
    {
        return EntityName.fromSubject(makeServer("another_service").getSubject());
    }
    
    private static ServiceTicketAuthenticator loginServer(LoginContext targetServer) throws LoginException
    {
        return new ServiceLogin(targetServer).login();
    }
    
    private LoginContext makeClient(String userName) throws Exception 
    {
        File keytab = makePrincipal(userName);
        Subject subject = makeSubject(userName);
        return new LoginContext("", subject, null, KerberosConfiguration.createClientConfig(userName, keytab));
    }
    
    private LoginContext makeServer(String serverName) throws Exception
    {
        File keytab = makePrincipal(serverName);
        Subject subject = makeSubject(serverName);
        return new LoginContext("", subject, null, KerberosConfiguration.createServerConfig(serverName, keytab));
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
