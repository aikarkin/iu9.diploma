import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.junit.jupiter.api.Test;

public class GuavaBiMapTests {
    @Test
    public void testBiMap() {
        BiMap<String, Integer> biMap = HashBiMap.create();
        biMap.put("abc", 123);
    }
}
