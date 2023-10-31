package unit.com.mtg.mtgtest.card.catalog.mtgio;

import com.mtg.mtgtest.card.catalog.CardCriteria;
import com.mtg.mtgtest.card.catalog.mapper.RawCardToCardMapper;
import com.mtg.mtgtest.card.catalog.model.Card;
import com.mtg.mtgtest.card.catalog.model.Color;
import com.mtg.mtgtest.card.catalog.mtgio.MagicTheGatheringCardCatalog;
import com.mtg.mtgtest.card.client.CardsClient;
import com.mtg.mtgtest.card.client.model.Page;
import com.mtg.mtgtest.card.client.model.RawCard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MagicTheGatheringCardCatalogTest {

    private final CardsClient client = mock(CardsClient.class);
    private final RawCardToCardMapper mapper = mock(RawCardToCardMapper.class);
    private final MagicTheGatheringCardCatalog service = new MagicTheGatheringCardCatalog(client, mapper);

    @Test
    @DisplayName("Получение всех карт, позитивный сценарий")
    void getAllCardsPositiveTest() {
        Page firstPage = mock(Page.class);
        Page secondPage = mock(Page.class);

        RawCard entity1 = mock(RawCard.class);
        RawCard entity2 = mock(RawCard.class);
        RawCard entity3 = mock(RawCard.class);

        when(firstPage.cards()).thenReturn(Arrays.asList(entity1, entity2));
        when(secondPage.cards()).thenReturn(Collections.singletonList(entity3));

        when(firstPage.nextPageNumber()).thenReturn(Optional.of(2));
        when(firstPage.lastPageNumber()).thenReturn(2);

        when(client.getPage(1)).thenReturn(Mono.just(firstPage));
        when(client.getPage(2)).thenReturn(Mono.just(secondPage));

        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);
        Card card3 = mock(Card.class);

        when(mapper.convert(entity1)).thenReturn(card1);
        when(mapper.convert(entity2)).thenReturn(card2);
        when(mapper.convert(entity3)).thenReturn(card3);

        List<Card> result = service.getAllCards().collectList().block();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3, result.size()),
                () -> assertTrue(result.containsAll(Arrays.asList(card1, card2, card3)))
        );
    }

    @Test
    @DisplayName("Получение всех карт, негативный сценарий: клиентский сервис не возвращает данных")
    void getAllCardsNegativeTest_NoDataFromClient() {
        when(client.getPage(anyInt())).thenReturn(Mono.empty());

        List<Card> result = service.getAllCards().collectList().block();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Получение всех карт, негативный сценарий: не удалось преобразовать сырую карту")
    void getAllCardsNegativeTest_FailedMapping() {
        RawCard entity = mock(RawCard.class);
        when(mapper.convert(entity)).thenThrow(new RuntimeException("Failed to map raw card"));

        Page page = mock(Page.class);
        when(page.cards()).thenReturn(Collections.singletonList(entity));
        when(client.getPage(anyInt())).thenReturn(Mono.just(page));

        List<Card> result = service.getAllCards().onErrorResume(e -> Flux.empty()).collectList().block();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("Фильтрация карт по критериям")
    void matchCardsTest() {
        CardCriteria criteria = mock(CardCriteria.class);

        when(criteria.nameContains()).thenReturn(Optional.of("Name"));
        when(criteria.colorIdentity()).thenReturn(Optional.of(Set.of(Color.RED)));

        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);
        Card card3 = mock(Card.class);

        when(card1.name()).thenReturn("Name1");
        when(card1.colorIdentity()).thenReturn(Set.of(Color.RED));
        when(card2.name()).thenReturn("Name2");
        when(card2.colorIdentity()).thenReturn(Set.of(Color.BLUE));
        when(card3.name()).thenReturn("Name3");
        when(card3.colorIdentity()).thenReturn(Set.of(Color.WHITE));
        when(mapper.convert(any(RawCard.class))).thenReturn(card1, card2, card3);

        RawCard rawCard1 = mock(RawCard.class);
        RawCard rawCard2 = mock(RawCard.class);
        RawCard rawCard3 = mock(RawCard.class);
        Page page = mock(Page.class);
        when(page.cards()).thenReturn(Arrays.asList(rawCard1, rawCard2, rawCard3));
        when(client.getPage(anyInt())).thenReturn(Mono.just(page));

        List<Card> result = service.matchCards(criteria).collectList().block();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3, result.size()),
                () -> assertTrue(result.contains(card1))
        );
    }

    @Test
    @DisplayName("Фильтрация карт по критериям, негативный сценарий: нет совпадений")
    void matchCardsNegativeTest_NoMatches() {
        CardCriteria criteria = mock(CardCriteria.class);
        when(criteria.nameContains()).thenReturn(Optional.of("UnmatchedName"));
        when(criteria.colorIdentity()).thenReturn(Optional.of(Set.of(Color.GREEN)));

        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);

        when(card1.name()).thenReturn("Name1");
        when(card1.colorIdentity()).thenReturn(Set.of(Color.RED));
        when(card2.name()).thenReturn("Name2");
        when(card2.colorIdentity()).thenReturn(Set.of(Color.BLUE));

        when(mapper.convert(any(RawCard.class))).thenReturn(card1, card2);
        RawCard rawCard1 = mock(RawCard.class);
        RawCard rawCard2 = mock(RawCard.class);
        Page page = mock(Page.class);
        when(page.cards()).thenReturn(Arrays.asList(rawCard1, rawCard2));
        when(client.getPage(anyInt())).thenReturn(Mono.just(page));

        List<Card> result = service.matchCards(criteria).collectList().block();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}