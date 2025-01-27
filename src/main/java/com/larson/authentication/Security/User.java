package com.larson.authentication.Security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
@Entity
@Table(name="users")
@Getter
@Setter
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String oauthID;
    private String firstname;
    private String lastname;
    private String email;
    private boolean admin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(!this.admin) return Collections.emptyList();

        return Arrays.asList(
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
    }
    @Override
    public String getPassword() {
        throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
    }
    @Override
    public String getUsername() {
        return this.email;
    }

}
