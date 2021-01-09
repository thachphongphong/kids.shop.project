package kz.danke.user.service.router;

import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.response.UserCabinetResponse;
import kz.danke.user.service.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class UserRouterTest extends AbstractRouterLayer {

    @BeforeEach
    public void setup(ApplicationContext applicationContext) {
        Mockito.reset(userService, stateMachineProcessingService);
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void userRouter_GetUserCabinet() {
        final String id = UUID.randomUUID().toString();
        final User user = User.builder()
                .id(id)
                .username(testData)
                .authorities(Collections.singleton(Authorities.ROLE_USER.name()))
                .build();
        String token = jwtService.generateToken(user);

        when(userService.getUserInSession()).thenReturn(Mono.just(user));

        webTestClient
                .get()
                .uri("/cabinet")
                .header("accessToken", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserCabinetResponse.class)
                .value(u -> Assertions.assertEquals(u.getId(), id));

        verify(userService, times(1)).getUserInSession();
    }

    @Test
    public void userRouter_GetUserCabinet_ReturnUserNotAuthorizedException() throws JsonProcessingException {
        User user = User.builder().id(UUID.randomUUID().toString()).build();

        String token = TestUtil.generateToken(user, null, properties.getJwt().getSecret());

        webTestClient
                .get()
                .uri("/cabinet")
                .header("accessToken", token)
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(userService, times(0)).getUserInSession();
    }

    @Test
    public void userRouter_GetUserCabinet_ReturnUserNotAuthorizedException_EmptyHeader() {
        webTestClient
                .get()
                .uri("/cabinet")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(userService, times(0)).getUserInSession();
    }
}
