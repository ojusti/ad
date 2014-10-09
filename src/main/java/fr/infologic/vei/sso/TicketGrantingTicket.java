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

public class TicketGrantingTicket
{
    private final Subject subject;

    TicketGrantingTicket(Subject subject)
    {
        this.subject = subject;
    }

    /**
     * On behalf of a client, acquires tickets that can be used for authentication with a service
     * 
     * @param service the target service
     * @return a ticket that can be authenticated by the service's {@link ServiceTicketAuthenticator}
     * @throws LoginException if the ticket cannot be granted
     */
    public ServiceTicket acquireServiceTicket(EntityName service) throws LoginException
    {
        try
        {
            return new ServiceTicket(Subject.doAs(subject, new ServiceTicketGenerator(service.getName())));
        }
        catch (PrivilegedActionException e)
        {
            throw new LoginException(e);
        }
    }

    public EntityName getClient()
    {
        return EntityName.fromSubject(subject);
    }

    
    private class ServiceTicketGenerator implements PrivilegedExceptionAction<byte[]>
    {
        private final String serviceName;

        ServiceTicketGenerator(String serviceName)
        {
            this.serviceName = serviceName;
        }

        @Override
        public byte[] run() throws GSSException
        {
            GSSContext gssContext = makeSecurityContext();
            try
            {
                return gssContext.initSecContext(new byte[0], 0, 0);
            }
            finally
            {
                gssContext.dispose();
            }
        }

        private GSSContext makeSecurityContext() throws GSSException
        {
            Oid kerberos5Oid = new Oid("1.2.840.113554.1.2.2");
            GSSManager gssManager = GSSManager.getInstance();
            GSSName client = gssManager.createName(getClient().getName(), null);
            GSSCredential clientCredentials = gssManager.createCredential(client, 8*60*60, kerberos5Oid, GSSCredential.INITIATE_ONLY);
            
            GSSName service = gssManager.createName(serviceName, null);
            return gssManager.createContext(service, kerberos5Oid, clientCredentials, GSSContext.DEFAULT_LIFETIME);
        }
    }
}
