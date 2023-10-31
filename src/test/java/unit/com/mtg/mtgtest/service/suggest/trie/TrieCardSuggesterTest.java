package unit.com.mtg.mtgtest.service.suggest.trie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mtg.mtgtest.card.catalog.CardCatalog;
import com.mtg.mtgtest.card.catalog.model.Card;
import com.mtg.mtgtest.card.catalog.model.Color;
import com.mtg.mtgtest.card.catalog.model.Impl.TestCard;
import com.mtg.mtgtest.service.suggest.trie.TrieCardSuggester;
import org.apache.commons.collections4.Trie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class TrieCardSuggesterTest {

    private TrieCardSuggester cardSuggester;
    private CardCatalog cardCatalog;

    private Card card1;
    private Card card2;

    @BeforeEach
    void prepare() {
        cardCatalog = mock(CardCatalog.class);
        cardSuggester = new TrieCardSuggester();
        when(cardCatalog.getAllCards()).thenReturn(Flux.empty());
        cardSuggester.populateTrie(cardCatalog);
        Set<Color> colorIdentity1 = Set.of(Color.RED, Color.GREEN);
        int convertedManaCost1 = 3;
        card1 = new TestCard("Test Card 1", colorIdentity1, convertedManaCost1);
        card2 = new TestCard("Test Card 2", colorIdentity1, convertedManaCost1);
        System.out.println("prepare" + card1);
        System.out.println("prepare" + card2);


    }

    @Test
    @DisplayName("Тест точного совпадения")
    void suggestCardsByName_ExactMatch() {

        when(cardCatalog.getAllCards()).thenReturn(Flux.just(card1, card2));

        List<Card> expectedCards = List.of(card1, card2);
        List<Card> actualCards = cardCatalog.getAllCards().collectList().block();

        assertEquals(expectedCards, actualCards);
    }

    @Test
    @DisplayName("Тест нахождения нескольких совпадений")
    void suggestCardsByName_MultipleMatches() {
        when(cardCatalog.getAllCards()).thenReturn(Flux.just(card1, card2));

        List<Card> expectedCards = List.of(card1, card2);
        List<Card> actualCards = cardCatalog.getAllCards().collectList().block();

        assertEquals(expectedCards, actualCards);
    }

    @Test
    @DisplayName("Тест расплывчатого совпадения")
    void suggestCardsByName_FuzzyMatch() {
        String cardName = "Test";
        Set<Color> colorIdentity1 = Set.of(Color.RED, Color.GREEN);
        int convertedManaCost1 = 3;
        Set<Color> colorIdentity2 = Set.of(Color.BLUE);
        int convertedManaCost2 = 2;
        Card card1 = new TestCard("Test Card 1", colorIdentity1, convertedManaCost1);
        Card card2 = new TestCard("Test Card 2", colorIdentity2, convertedManaCost2);

        when(cardCatalog.getAllCards()).thenReturn(Flux.just(card1, card2));

        List<Card> expectedCards = List.of(card1, card2);
        List<Card> actualCards = cardCatalog.getAllCards().collectList().block();

        assertEquals(expectedCards, actualCards);
    }
}