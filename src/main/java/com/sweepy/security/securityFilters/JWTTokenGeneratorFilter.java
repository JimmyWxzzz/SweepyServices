//package com.sweepy.security.securityFilters;
//
//import com.sweepy.security.securityConstant.SecurityConstants;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.crypto.SecretKey;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;
//
//public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
//
//	private String JWT;
//	@Override
//	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		if (null != authentication) {
//			SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
//			String jwt = Jwts.builder().setIssuer("Sweepy").setSubject("JWT Token")
//						.claim("username", authentication.getName())
//					  .claim("authorities", populateAuthorities(authentication.getAuthorities()))
//					  .setIssuedAt(new Date())
//					.setExpiration(new Date((new Date()).getTime() + 400000))
//					.signWith(key).compact();
//			response.setHeader(SecurityConstants.JWT_HEADER, jwt);
//		}
//
//		chain.doFilter(request, response);
//	}
//
//	@Override
//	protected boolean shouldNotFilter(HttpServletRequest request) {
//		return !(request.getServletPath().equals("/login") | request.getServletPath().equals("/signup") | request.getServletPath().equals("/test"));
//	}
//
//	private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
//		Set<String> authoritiesSet = new HashSet<>();
//        for (GrantedAuthority authority : collection) {
//        	authoritiesSet.add(authority.getAuthority());
//        }
//        return String.join(",", authoritiesSet);
//	}
//}
