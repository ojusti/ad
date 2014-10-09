package fr.infologic.vei.sso;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

class Login
{
    private final LoginContext context;
    Login(LoginContext context)
    {
        this.context = context;
    }
    
    Subject login() throws LoginException
    {
        context.login();
        return context.getSubject();
    }
}
