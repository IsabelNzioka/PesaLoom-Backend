package com.pesaloom.pesaloom.security;

import com.pesaloom.pesaloom.user.Role;

import java.util.UUID;


public record AuthPrincipal(UUID userId, String email, Role role) {
}
