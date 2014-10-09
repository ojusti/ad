package fr.infologic.vei.sso;

import java.util.Objects;

import javax.security.auth.login.LoginContext;

/** A server who can authenticate clients */
public class ServiceLogin
{
    private final Login server;

    public ServiceLogin(LoginContext serverLogin)
    {
        Objects.requireNonNull(serverLogin, "Server LoginContext must not be null");
        this.server = new Login(serverLogin);
    }

    /** factory method for ticket authenticator on server side */
    public ServiceTicketAuthenticator login() throws LoginException
    {
        try
        {
            return new ServiceTicketAuthenticator(server.login());
        }
        catch (javax.security.auth.login.LoginException e)
        {
            throw new LoginException(e);
        }
    }
}
