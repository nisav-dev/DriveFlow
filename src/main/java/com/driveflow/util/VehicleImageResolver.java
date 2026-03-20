package com.driveflow.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class VehicleImageResolver {

    private static final String FALLBACK_IMAGE =
            "https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=1200&h=700&fit=crop&auto=format&q=80";

    private static final Map<String, String> MODEL_IMAGES = new HashMap<>();
    private static final Map<String, String> MAKE_IMAGES = new HashMap<>();
    private static final Map<String, String> CATEGORY_IMAGES = new HashMap<>();

    static {
        MODEL_IMAGES.put("hyundai i10", "https://images.unsplash.com/photo-1549924231-f129b911e442?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("hyundai i20", "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("toyota corolla", "https://images.unsplash.com/photo-1590362891991-f776e747a588?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("toyota rav4", "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("kia sportage", "https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("bmw 5 series", "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("tesla model 3", "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("volkswagen transporter", "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("mercedes c-class", "https://images.unsplash.com/photo-1617469767053-d3b523a0b982?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("suzuki swift", "https://images.unsplash.com/photo-1542282088-fe8426682b8f?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("mazda cx-5", "https://images.unsplash.com/photo-1541348263662-e068662d82af?w=1200&h=700&fit=crop&auto=format&q=80");
        MODEL_IMAGES.put("ford focus", "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=1200&h=700&fit=crop&auto=format&q=80");

        MAKE_IMAGES.put("toyota", "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=1200&h=700&fit=crop&auto=format&q=80");
        MAKE_IMAGES.put("hyundai", "https://images.unsplash.com/photo-1535732820275-9ffd998cac22?w=1200&h=700&fit=crop&auto=format&q=80");
        MAKE_IMAGES.put("kia", "https://images.unsplash.com/photo-1553440569-bcc63803a83d?w=1200&h=700&fit=crop&auto=format&q=80");
        MAKE_IMAGES.put("bmw", "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=1200&h=700&fit=crop&auto=format&q=80");
        MAKE_IMAGES.put("tesla", "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=1200&h=700&fit=crop&auto=format&q=80");
        MAKE_IMAGES.put("volkswagen", "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=1200&h=700&fit=crop&auto=format&q=80");
        MAKE_IMAGES.put("mercedes", "https://images.unsplash.com/photo-1617469767053-d3b523a0b982?w=1200&h=700&fit=crop&auto=format&q=80");
        MAKE_IMAGES.put("suzuki", "https://images.unsplash.com/photo-1542282088-fe8426682b8f?w=1200&h=700&fit=crop&auto=format&q=80");
        MAKE_IMAGES.put("mazda", "https://images.unsplash.com/photo-1541348263662-e068662d82af?w=1200&h=700&fit=crop&auto=format&q=80");
        MAKE_IMAGES.put("ford", "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=1200&h=700&fit=crop&auto=format&q=80");

        CATEGORY_IMAGES.put("economy", "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=1200&h=700&fit=crop&auto=format&q=80");
        CATEGORY_IMAGES.put("compact", "https://images.unsplash.com/photo-1590362891991-f776e747a588?w=1200&h=700&fit=crop&auto=format&q=80");
        CATEGORY_IMAGES.put("suv", "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=1200&h=700&fit=crop&auto=format&q=80");
        CATEGORY_IMAGES.put("mini", "https://images.unsplash.com/photo-1542282088-fe8426682b8f?w=1200&h=700&fit=crop&auto=format&q=80");
        CATEGORY_IMAGES.put("luxury", "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=1200&h=700&fit=crop&auto=format&q=80");
        CATEGORY_IMAGES.put("van", "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=1200&h=700&fit=crop&auto=format&q=80");
        CATEGORY_IMAGES.put("electric", "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=1200&h=700&fit=crop&auto=format&q=80");
        CATEGORY_IMAGES.put("premium", "https://images.unsplash.com/photo-1617469767053-d3b523a0b982?w=1200&h=700&fit=crop&auto=format&q=80");
    }

    private VehicleImageResolver() {
    }

    public static String resolve(String make, String model, String category) {
        String key = normalized(make) + " " + normalized(model);
        String byModel = MODEL_IMAGES.get(key.trim());
        if (byModel != null) {
            return byModel;
        }

        String byMake = MAKE_IMAGES.get(normalized(make));
        if (byMake != null) {
            return byMake;
        }

        String byCategory = CATEGORY_IMAGES.get(normalized(category));
        if (byCategory != null) {
            return byCategory;
        }

        return FALLBACK_IMAGE;
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
