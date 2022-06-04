package matiukhin.grigorii.search_engine.algorithms;

import java.util.Locale;

public class TextSanitizer {
    public static String sanitizeText(String text) {
        return text
                .trim()
                .replaceAll("[.,/#!$%^&*;:{}=_`~()]", "")
                .replaceAll("\\s+-+\\s+", "")
                .toLowerCase(Locale.ROOT);
    }
}
