package fr.infologic.vei.sso;

import java.util.Objects;

import javax.security.auth.login.LoginContext;

/** A client who can authenticate with services using tickets */
public class ClientLogin
{
    private final Login client;

    public ClientLogin(LoginContext clientLogin)
    {
        Objects.requireNonNull(clientLogin, "Client LoginContext must not be null");
        this.client = new Login(clientLogin);
    }

    /** factory method for ticket generator on client side */
    public TicketGrantingTicket login() throws LoginException
    {
        try
        {
            return new TicketGrantingTicket(client.login());
        }
        catch (javax.security.auth.login.LoginException e)
        {
            throw new LoginException(e);
        }
    }
}
