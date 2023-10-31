package unit.com.mtg.mtgtest.search;

import com.mtg.mtgtest.search.trie.RedundantTrie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedundantTrieTest {

    private RedundantTrie<String> trie;

    @BeforeEach
    void setUp() {
        trie = new RedundantTrie<>(
                Function.identity(),
                key -> Spliterators.spliteratorUnknownSize(Arrays.stream(key.split("")).iterator(), 0)
        );
    }

    @Test
    @DisplayName("Проверка добавления элементов")
    void testAdd() {
        trie.add("apple");
        trie.add("appetite");

        List<String> result = trie.search("app", 1);
        assertEquals(2, result.size());
        assertTrue(result.contains("apple"));
        assertTrue(result.contains("appetite"));
    }

    @Test
    @DisplayName("Поиск по ключу с заданным расстоянием")
    void testSearch() {
        trie.add("apple");
        trie.add("appetite");
        trie.add("banana");

        List<String> result = trie.search("app", 1);
        assertEquals(2, result.size());
        assertTrue(result.contains("apple"));
        assertTrue(result.contains("appetite"));

        result = trie.search("ban", 1);
        assertEquals(1, result.size());
        assertTrue(result.contains("banana"));
    }

    @Test
    @DisplayName("Поиск по ключу с допустимым расстоянием")
    void testSearchWithDistance() {
        trie.add("apple");
        trie.add("appetite");

        List<String> result = trie.search("apl", 1);
        assertEquals(2, result.size());
        assertTrue(result.contains("apple"));
    }

    @Test
    @DisplayName("Поиск по ключу без совпадений")
    void testSearchNoMatch() {
        trie.add("apple");
        trie.add("appetite");

        List<String> result = trie.search("xyz", 1);
        assertTrue(result.isEmpty());
    }
}
