package fr.infologic.vei.sso;

import static org.assertj.core.api.Assertions.assertThat;

import javax.security.auth.login.LoginContext;

public class LoginTestsHelper
{
    static ServiceTicketAuthenticator loginServer(LoginContext targetServer) throws LoginException
    {
        return new ServiceLogin(targetServer).login();
    }
    
    static ServiceTicket userAcquiresTicket(LoginContext user, EntityName service) throws LoginException
    {
        return new ClientLogin(user).login().acquireServiceTicket(service);
    }
    
    static void assertThatClientIsUser(EntityName client, LoginContext user)
    {
        assertThat(client).isEqualTo(EntityName.fromSubject(user.getSubject()));
    }
    
}
