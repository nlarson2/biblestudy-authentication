package com.larson.authentication.Security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String oauthID;
    private String firstname;
    private String lastname;
    private String email;
    private String[] roles;

    public User(String oauthID, String firstname, String lastname, String email){
        this.oauthID=oauthID; this.firstname=firstname; this.lastname=lastname;
        this.email=email; 
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if(this.roles == null) return null;
        return  Arrays.stream(this.roles)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
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
