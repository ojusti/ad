package fr.infologic.vei.sso;

import java.io.Serializable;

public class ServiceTicket implements Serializable
{
    final byte[] ticket;

    ServiceTicket(byte[] ticket)
    {
        this.ticket = ticket;
    }
}
