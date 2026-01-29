package com.microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Tests Unitaires - StringUtil")
class StringUtilTest {

    @Test
    @DisplayName("Le constructeur privé doit lever une exception")
    void constructorShouldThrowException() {
        assertThatThrownBy(() -> {
            var constructor = StringUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Nested
    @DisplayName("Tests de génération aléatoire")
    class RandomGenerationTests {

        @Test
        @DisplayName("Doit générer une chaîne alphanumérique de la bonne longueur")
        void shouldGenerateAlphanumeric() {
            int length = 15;
            String result = StringUtil.generateRandomAlphanumeric(length);
            assertThat(result).hasSize(length);
            assertThat(result).matches("^[a-zA-Z0-9]+$");
        }

        @Test
        @DisplayName("Doit générer une chaîne numérique de la bonne longueur")
        void shouldGenerateNumeric() {
            int length = 6;
            String result = StringUtil.generateRandomNumeric(length);
            assertThat(result).hasSize(length);
            assertThat(result).containsOnlyDigits();
        }
    }

    @Nested
    @DisplayName("Tests de transformation (Capitalisation & Accents)")
    class TransformationTests {

        @Test
        @DisplayName("Doit capitaliser la première lettre")
        void shouldCapitalize() {
            assertThat(StringUtil.capitalize("java")).isEqualTo("Java");
            assertThat(StringUtil.capitalize("")).isEmpty();
            assertThat(StringUtil.capitalize(null)).isNull();
        }

        @Test
        @DisplayName("Doit capitaliser chaque mot")
        void shouldCapitalizeWords() {
            assertThat(StringUtil.capitalizeWords("baye rane")).isEqualTo("Baye Rane");
            assertThat(StringUtil.capitalizeWords("HELLO WORLD")).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("Doit supprimer les accents")
        void shouldRemoveAccents() {
            assertThat(StringUtil.removeAccents("éàçîû")).isEqualTo("eaciu");
            assertThat(StringUtil.removeAccents("NoAccents")).isEqualTo("NoAccents");
        }

        @Test
        @DisplayName("Doit transformer en slug valide")
        void shouldConvertToSlug() {
            assertThat(StringUtil.toSlug("Mon Super Article !")).isEqualTo("mon-super-article");
            assertThat(StringUtil.toSlug("L'été arrive bientôt")).isEqualTo("lete-arrive-bientot");
        }
    }

    @Nested
    @DisplayName("Tests de masquage et sécurité")
    class MaskingTests {

        @Test
        @DisplayName("Doit masquer une chaîne générique")
        void shouldMaskString() {
            // "12345678" -> start 2, end 2 -> "12****78"
            assertThat(StringUtil.mask("12345678", 2, 2)).isEqualTo("12****78");
        }

        @ParameterizedTest
        @CsvSource({
            "bayerane@example.com, b***e@example.com",
            "ab@test.com, a***@test.com",
            "test@domain.fr, t***t@domain.fr"
        })
        @DisplayName("Doit masquer les emails correctement")
        void shouldMaskEmail(String input, String expected) {
            assertThat(StringUtil.maskEmail(input)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Tests de validation et comptage")
    class ValidationTests {

        @Test
        @DisplayName("Doit vérifier si numérique")
        void shouldCheckIsNumeric() {
            assertThat(StringUtil.isNumeric("12345")).isTrue();
            assertThat(StringUtil.isNumeric("123a45")).isFalse();
            assertThat(StringUtil.isNumeric("")).isFalse();
        }

        @Test
        @DisplayName("Doit compter les occurrences")
        void shouldCountOccurrences() {
            assertThat(StringUtil.countOccurrences("ababa", "a")).isEqualTo(3);
            assertThat(StringUtil.countOccurrences("hello world", "o")).isEqualTo(2);
            assertThat(StringUtil.countOccurrences("test", "xyz")).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Tests utilitaires divers")
    class MiscTests {

        @Test
        @DisplayName("Doit tronquer une chaîne")
        void shouldTruncate() {
            assertThat(StringUtil.truncate("Texte long", 8, "...")).isEqualTo("Texte...");
            assertThat(StringUtil.truncate("Court", 10, "...")).isEqualTo("Court");
        }

        @Test
        @DisplayName("Doit inverser une chaîne")
        void shouldReverse() {
            assertThat(StringUtil.reverse("abc")).isEqualTo("cba");
            assertThat(StringUtil.reverse(null)).isNull();
        }

        @Test
        @DisplayName("Doit répéter une chaîne")
        void shouldRepeat() {
            assertThat(StringUtil.repeat("A", 3)).isEqualTo("AAA");
            assertThat(StringUtil.repeat("A", 0)).isEmpty();
        }
    }
}