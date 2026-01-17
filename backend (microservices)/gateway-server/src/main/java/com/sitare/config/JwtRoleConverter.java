package com.sitare.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtRoleConverter implements Converter<String, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(String role) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null && !role.isEmpty()) {
            // Ensure role starts with ROLE_ prefix
            String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
            authorities.add(new SimpleGrantedAuthority(roleName));
        }
        return authorities;
    }
}
