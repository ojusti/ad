package fr.infologic.vei.sso;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public class ServiceTicketAuthenticator
{
    private final Subject subject;

    ServiceTicketAuthenticator(Subject subject)
    {
        this.subject = subject;
    }

    /**
     * On behalf of a server, authenticates clients using tickets
     * 
     * @param a ticket granted to a clients using {@link TicketGrantingTicket#acquireServiceTicket(EntityName)}
     * @return the name of the peer who acquired the ticket
     * @throws LoginException if the ticket cannot be authenticated
     */
    public EntityName authenticate(ServiceTicket ticket) throws LoginException
    {
        try
        {
            return new EntityName(Subject.doAs(subject, new ServiceTicketDecoder(ticket.ticket)));
        }
        catch (PrivilegedActionException e)
        {
            throw new LoginException(e);
        }
    }
    
    /**
     * run method returns the name of the peer who created the ticket
     */
    private class ServiceTicketDecoder implements PrivilegedExceptionAction<String>
    {
        private final byte[] ticket;

        ServiceTicketDecoder(byte[] ticket)
        {
            this.ticket = ticket;
        }

        @Override
        public String run() throws GSSException
        {
            GSSContext context = makeSecurityContext();
            try
            {
                context.acceptSecContext(this.ticket, 0, this.ticket.length);
                return context.getSrcName().toString();
            }
            finally
            {
                context.dispose();
            }
        }

        private GSSContext makeSecurityContext() throws GSSException
        {
            Oid kerberos5Oid = new Oid("1.2.840.113554.1.2.2");
            GSSManager gssManager = GSSManager.getInstance();
            GSSName service = gssManager.createName(getService().getName(), null);
            GSSCredential serviceCredentials = gssManager.createCredential(service, GSSCredential.INDEFINITE_LIFETIME, kerberos5Oid, GSSCredential.ACCEPT_ONLY);
            return gssManager.createContext(serviceCredentials);
        }
    }

    EntityName getService()
    {
        return EntityName.fromSubject(subject);
    }
}
