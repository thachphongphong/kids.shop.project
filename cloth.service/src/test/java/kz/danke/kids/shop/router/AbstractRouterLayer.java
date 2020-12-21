package kz.danke.kids.shop.router;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.config.handler.CategoryHandler;
import kz.danke.kids.shop.config.handler.ClothHandler;
import kz.danke.kids.shop.config.handler.PromotionHandler;
import kz.danke.kids.shop.config.router.ClothRouterConfig;
import kz.danke.kids.shop.repository.CategoryReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.repository.PromotionCardReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.CategoryService;
import kz.danke.kids.shop.service.ClothService;
import kz.danke.kids.shop.service.PromotionService;
import kz.danke.kids.shop.service.impl.CategoryServiceImpl;
import kz.danke.kids.shop.service.impl.ClothServiceImpl;
import kz.danke.kids.shop.service.impl.PromotionServiceImpl;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.reactive.server.HttpHandlerConnector;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ClothHandler.class,
        CategoryHandler.class,
        PromotionHandler.class,
        ClothRouterConfig.class
})
@TestPropertySource("classpath:application-test.properties")
@WebFluxTest
@TestExecutionListeners(listeners = {
        MockitoTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
})
public class AbstractRouterLayer {
    @MockBean
    protected ClothService clothService;
    @MockBean
    protected CategoryService categoryService;
    @MockBean
    protected PromotionService promotionService;
    protected WebTestClient webTestClient;
}
