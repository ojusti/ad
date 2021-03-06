package fr.infologic.vei.sso;

import javax.security.auth.login.LoginContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LoginUsingADTest
{
    private EntityName serviceName;
    private LoginContext user;
    private ServiceTicketAuthenticator service;

    @Before
    public void setup() 
    {
        System.setProperty("java.security.krb5.realm", "INFOLOGIC.LAN");
        System.setProperty("java.security.krb5.kdc", "10.99.0.1");
        System.setProperty("java.security.auth.login.config", getClass().getResource("/login.conf").getPath());
        System.setProperty("sun.security.krb5.debug", "true");
    }
    @After
    public void teardown() 
    {
        System.clearProperty("java.security.krb5.realm");
        System.clearProperty("java.security.krb5.kdc");
        System.clearProperty("java.security.auth.login.config");
        System.clearProperty("sun.security.krb5.debug");
    }
    @Before
    public void makeEntities() throws Exception
    {
        LoginContext targetServer = new LoginContext("server");
        service = LoginTestsHelper.loginServer(targetServer);
        serviceName = EntityName.fromSubject(targetServer.getSubject());
    }
    
    @Test
    public void login_using_external_AD() throws LoginException, javax.security.auth.login.LoginException
    {
        test_with_external_AD("client");
    }
    
    @Test
    public void sso_using_external_AD() throws LoginException, javax.security.auth.login.LoginException
    {
        test_with_external_AD("sso_client");
    }
    
    private void test_with_external_AD(String loginEntry) throws LoginException, javax.security.auth.login.LoginException
    {
        user = new LoginContext(loginEntry);
        ServiceTicket ticket = LoginTestsHelper.userAcquiresTicket(user, serviceName);

        EntityName client = service.authenticate(ticket);
        
        LoginTestsHelper.assertThatClientIsUser(client, user);
    }

}
