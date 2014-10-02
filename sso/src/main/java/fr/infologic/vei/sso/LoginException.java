package fr.infologic.vei.sso;

import java.security.PrivilegedActionException;


public class LoginException extends Exception
{
    LoginException(javax.security.auth.login.LoginException e)
    {
        super(e);
    }

    LoginException(PrivilegedActionException e)
    {
        super(e);
    }
}
