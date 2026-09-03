package com.lendswift.lendswift.security;

import com.lendswift.lendswift.user.Role;

import java.util.UUID;


public record AuthPrincipal(UUID userId, String email, Role role) {
}
