package sajipay.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProductIdGenerator {
    private static final Set<String> usedIds = new HashSet<>();
    private static final Map<String, Integer> categoryCounters = new HashMap<>();

    static {
        categoryCounters.put("food", 0);
        categoryCounters.put("beverage", 0);
    }

    public static String generateId(String category) {
        category = category.toLowerCase();

        if (!categoryCounters.containsKey(category)) {
            throw new IllegalArgumentException("Unknown category: " + category);
        }

        String prefix = switch (category) {
            case "food" -> "F";
            case "beverage" -> "B";
            default -> throw new IllegalArgumentException("Unsupported category: " + category);
        };

        String id;
        do {
            int counter = categoryCounters.get(category) + 1;
            categoryCounters.put(category, counter);
            id = prefix + String.format("%03d", counter);
        } while (usedIds.contains(id));

        usedIds.add(id);
        return id;
    }

    // Optional: reset all counters and used IDs (for testing or reinitialization)
    public static void reset() {
        usedIds.clear();
        categoryCounters.put("food", 0);
        categoryCounters.put("beverage", 0);
    }
}
