package kz.danke.user.service.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kz.danke.user.service.config.AppConfigProperties;
import kz.danke.user.service.service.JsonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service("userJwtService")
public class JwtServiceImpl implements JwtService<String> {

    private final JsonObjectMapper jsonObjectMapper;
    private final AppConfigProperties appConfigProperties;

    @Autowired
    public JwtServiceImpl(JsonObjectMapper jsonObjectMapper,
                          AppConfigProperties appConfigProperties) {
        this.jsonObjectMapper = jsonObjectMapper;
        this.appConfigProperties = appConfigProperties;
    }

    @Override
    public String extractTokenSubject(String token) {
        return extractTokenClaims(token)
                .getSubject();
    }

    @Override
    public Claims extractTokenClaims(String token) {
        String secret = Base64.getEncoder().encodeToString(appConfigProperties.getJwt().getSecret().getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean validateToken(String token) {
        return extractTokenClaims(token)
                .getExpiration()
                .before(new Date());
    }

    @Override
    public String generateToken(Authentication authentication) {
        final String keyUserClaims = "user";
        final String oauth2User = "oauth2_user";

        HashMap<String, Object> claims = new HashMap<>();

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        String serializedUserDetails = jsonObjectMapper.serializeObject(principal);

        claims.put(keyUserClaims, serializedUserDetails);
        claims.put(oauth2User, null);

        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + appConfigProperties.getJwt().getExpiration() * 1000);

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(principal.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(appConfigProperties.getJwt().getSecret().getBytes()))
                .compact();
    }
}
