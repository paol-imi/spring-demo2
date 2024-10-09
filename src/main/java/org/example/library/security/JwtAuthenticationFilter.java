package org.example.library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.library.service.CustomUserDetailsService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter class to authenticate the user based on the JWT token.
 */
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /**
     * The JwtTokenProvider to use for the authentication.
     */
    private final JwtTokenProvider tokenProvider;

    /**
     * The CustomUserDetailsService to use for the authentication.
     */
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Filters the request and sets the user authentication based on the JWT token.
     *
     * @param request     The HttpServletRequest to filter
     * @param response    The HttpServletResponse to filter
     * @param filterChain The FilterChain to filter
     * @throws ServletException If an error occurs during the filtering
     * @throws IOException      If an error occurs during the filtering
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // TODO: Refactor this method.
        try {
            String jwt = this.getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                String username = this.tokenProvider.getUsernameFromJWT(jwt).get();
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            this.logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Gets the JWT token from the request.
     *
     * @param request The HttpServletRequest to get the JWT token from
     * @return The JWT token from the request
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}