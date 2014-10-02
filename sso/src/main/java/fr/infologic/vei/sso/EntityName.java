package fr.infologic.vei.sso;

import javax.security.auth.Subject;

public class EntityName
{
    private final String name;

    public EntityName(String name)
    {
        this.name = name;
    }
    static EntityName fromSubject(Subject subject)
    {
        return new EntityName(subject.getPrincipals().iterator().next().getName());
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) { return true; }
        if(obj == null) { return false; }
        if(getClass() != obj.getClass()) { return false; }
        EntityName other = (EntityName) obj;
        if(name == null)
        {
            if(other.name != null) { return false; }
        }
        else if(!name.equals(other.name)) { return false; }
        return true;
    }
    
    

}
