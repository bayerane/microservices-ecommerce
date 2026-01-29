package com.microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Tests Unitaires - ValidationUtil")
class ValidationUtilTest {

    @Test
    @DisplayName("Le constructeur privé doit lever une exception")
    void constructorShouldThrowException() {
        assertThatThrownBy(() -> {
            var constructor = ValidationUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Nested
    @DisplayName("Tests de validation d'Email")
    class EmailValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {"test@example.com", "user.name@domain.co.uk", "info+123@sub.domain.org"})
        @DisplayName("Doit valider les emails corrects")
        void shouldValidateCorrectEmails(String email) {
            assertThat(ValidationUtil.isValidEmail(email)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"plainaddress", "#@%^%#$@#$@#.com", "@domain.com", "joe@domain..com"})
        @DisplayName("Doit rejeter les emails invalides")
        void shouldRejectInvalidEmails(String email) {
            assertThat(ValidationUtil.isValidEmail(email)).isFalse();
        }

        @Test
        @DisplayName("Doit normaliser l'email correctement")
        void shouldNormalizeEmail() {
            assertThat(ValidationUtil.normalizeEmail("  TEST@Example.COM  ")).isEqualTo("test@example.com");
            assertThat(ValidationUtil.normalizeEmail(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Tests de sécurité (Mots de passe)")
    class PasswordSecurityTests {

        @Test
        @DisplayName("Doit valider un mot de passe fort")
        void shouldValidateStrongPassword() {
            assertThat(ValidationUtil.isStrongPassword("Pass1234")).isTrue();
            assertThat(ValidationUtil.isStrongPassword("weak")).isFalse(); // Trop court
            assertThat(ValidationUtil.isStrongPassword("ONLYUPPER123")).isFalse(); // Pas de minuscule
        }

        @Test
        @DisplayName("Doit valider un mot de passe très fort (avec caractères spéciaux)")
        void shouldValidateVeryStrongPassword() {
            assertThat(ValidationUtil.isVeryStrongPassword("Safe@Pass123")).isTrue();
            assertThat(ValidationUtil.isVeryStrongPassword("SafePass123")).isFalse(); // Manque caractère spécial
        }

        @Test
        @DisplayName("Doit retourner les exigences textuelles")
        void shouldReturnRequirements() {
            assertThat(ValidationUtil.getPasswordRequirements()).contains("8", "majuscule", "minuscule", "chiffre");
        }
    }

    @Nested
    @DisplayName("Tests de validation de chaînes")
    class StringValidationTests {

        @Test
        @DisplayName("Doit vérifier si une chaîne est vide ou nulle")
        void shouldCheckIsNullOrEmpty() {
            assertThat(ValidationUtil.isNullOrEmpty(null)).isTrue();
            assertThat(ValidationUtil.isNullOrEmpty("   ")).isTrue();
            assertThat(ValidationUtil.isNullOrEmpty("content")).isFalse();
        }

        @Test
        @DisplayName("Doit vérifier la plage de longueur")
        void shouldValidateLengthBetween() {
            String str = "Hello";
            assertThat(ValidationUtil.isLengthBetween(str, 3, 10)).isTrue();
            assertThat(ValidationUtil.isLengthBetween(str, 6, 10)).isFalse();
            assertThat(ValidationUtil.isLengthBetween(null, 0, 5)).isFalse();
        }
    }

    @Nested
    @DisplayName("Tests de validation de UUID et Téléphone")
    class FormatValidationTests {

        @Test
        @DisplayName("Doit valider un UUID correct")
        void shouldValidateUUID() {
            String validUUID = "550e8400-e29b-41d4-a716-446655440000";
            assertThat(ValidationUtil.isValidUUID(validUUID)).isTrue();
            assertThat(ValidationUtil.isValidUUID("invalid-uuid")).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {"+33612345678", "0612345678", "00221771234567"})
        @DisplayName("Doit valider les numéros de téléphone")
        void shouldValidatePhone(String phone) {
            assertThat(ValidationUtil.isValidPhone(phone)).isTrue();
        }
    }

    @Nested
    @DisplayName("Tests numériques")
    class NumericValidationTests {

        @Test
        @DisplayName("Doit valider les nombres positifs")
        void shouldValidatePositive() {
            assertThat(ValidationUtil.isPositive(10)).isTrue();
            assertThat(ValidationUtil.isPositive(0)).isFalse();
            assertThat(ValidationUtil.isPositive(-5)).isFalse();
        }

        @Test
        @DisplayName("Doit vérifier si une valeur est dans une plage")
        void shouldValidateRange() {
            assertThat(ValidationUtil.isInRange(50.5, 0, 100)).isTrue();
            assertThat(ValidationUtil.isInRange(150, 0, 100)).isFalse();
        }
    }
}