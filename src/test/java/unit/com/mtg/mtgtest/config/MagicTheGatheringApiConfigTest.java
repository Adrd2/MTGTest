package unit.com.mtg.mtgtest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtg.mtgtest.card.catalog.CardCatalog;
import com.mtg.mtgtest.card.catalog.mapper.RawCardToCardMapper;
import com.mtg.mtgtest.card.catalog.model.Card;
import com.mtg.mtgtest.card.client.CardsClient;
import com.mtg.mtgtest.card.client.model.RawCard;
import com.mtg.mtgtest.card.client.util.BodyParser;
import com.mtg.mtgtest.config.MagicTheGatheringApiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MagicTheGatheringApiConfigTest {

    @Mock
    private WebClient webClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BodyParser bodyParser;

    @Mock
    private CardsClient cardsClient;

    @Mock
    private RawCardToCardMapper rawCardToCardMapperBean;

    @Autowired
    private MagicTheGatheringApiConfig apiConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        apiConfig = new MagicTheGatheringApiConfig();
    }

    @Test
    @DisplayName("Тест настройки webClient Bean")
    void testWebClientBean() {
        WebClient webClient = apiConfig.webClient();
        assertNotNull(webClient);
    }

    @Test
    @DisplayName("Тест настройки objectMapper Bean")
    void testObjectMapperBean() {
        ObjectMapper objectMapper = apiConfig.objectMapper();
        assertNotNull(objectMapper);
    }

    @Test
    @DisplayName("Тест настройки jsonDeserializer Bean")
    void testJsonDeserializerBean() {
        BodyParser bodyParser = apiConfig.jsonDeserializer(objectMapper);
        assertNotNull(bodyParser);
    }

    @Test
    @DisplayName("Тест настройки cardPagesCacheManager Bean")
    void testCardPagesCacheManagerBean() {
        CacheManager cacheManager = apiConfig.cardPagesCacheManager();
        assertNotNull(cacheManager);
    }

    @Test
    @DisplayName("Тест настройки rawCardToCardMapper Bean")
    void testRawCardToCardMapperBean() {
        Converter<RawCard, Card> mapper = apiConfig.rawCardToCardMapper();
        assertNotNull(mapper);
    }

    @DisplayName("Тест настройки cardsClient Bean")
    @Test
    void testCardsClientBean() {
        String baseUri = "https://example.com/api/cards";
        CardsClient client = apiConfig.cardsClient(baseUri, bodyParser, webClient);
        assertNotNull(client);
    }

    @Test
    @DisplayName("Тест настройки cardCatalog Bean")
    void testCardCatalogBean() {
        CardCatalog catalog = apiConfig.cardCatalog(cardsClient, rawCardToCardMapperBean);
        assertNotNull(catalog);
    }
}