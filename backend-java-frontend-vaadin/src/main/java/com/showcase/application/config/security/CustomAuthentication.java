package com.showcase.application.config.security;

import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.Token;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class CustomAuthentication extends AbstractAuthenticationToken {

    private final Token token;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public CustomAuthentication(Token token, Collection<? extends GrantedAuthority> authorities, UserSetting userSetting) {
        super(authorities);
        this.token = token;

        if(userSetting == null) {
            userSetting = new UserSetting();
        }

        this.setDetails(userSetting);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.getAuthorities();
    }

    @Override
    public Object getPrincipal() {
        return token.getUser();
    }
}
