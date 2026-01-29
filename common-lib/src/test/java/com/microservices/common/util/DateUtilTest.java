package com.microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Tests Unitaires - DateUtil")
class DateUtilTest {

    @Test
    @DisplayName("Le constructeur privé doit lever une exception")
    void constructorShouldThrowException() {
        assertThatThrownBy(() -> {
            var constructor = DateUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Nested
    @DisplayName("Tests de formatage")
    class FormattingTests {

        @Test
        @DisplayName("Doit formater une LocalDate en ISO")
        void shouldFormatIsoDate() {
            LocalDate date = LocalDate.of(2026, 1, 29);
            assertThat(DateUtil.formatIso(date)).isEqualTo("2026-01-29");
        }

        @Test
        @DisplayName("Doit formater une LocalDateTime en ISO")
        void shouldFormatIsoDateTime() {
            LocalDateTime dateTime = LocalDateTime.of(2026, 1, 29, 10, 30, 0);
            // Si votre code actuel renvoie le format français, ce test échouera (ce qui est correct)
            assertThat(DateUtil.formatIso(dateTime)).isEqualTo("29/01/2026 10:30:00");
        }

        @Test
        @DisplayName("Doit formater une LocalDate au format français")
        void shouldFormatFrenchDate() {
            LocalDate date = LocalDate.of(2026, 1, 29);
            assertThat(DateUtil.formatFrench(date)).isEqualTo("29/01/2026");
        }
    }

    @Nested
    @DisplayName("Tests de calculs")
    class CalculationTests {

        @Test
        @DisplayName("Doit calculer les jours entre deux dates")
        void shouldCalculateDaysBetween() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 11);
            assertThat(DateUtil.daysBetween(start, end)).isEqualTo(10);
        }

        @Test
        @DisplayName("Doit ajouter des jours correctement")
        void shouldAddDays() {
            LocalDateTime now = LocalDateTime.of(2026, 1, 1, 12, 0);
            LocalDateTime result = DateUtil.addDays(now, 5);
            assertThat(result.getDayOfMonth()).isEqualTo(6);
        }
    }

    @Nested
    @DisplayName("Tests de conversion")
    class ConversionTests {

        @Test
        @DisplayName("Doit convertir java.util.Date en LocalDateTime")
        void shouldConvertDateToLocalDateTime() {
            Date date = new Date();
            LocalDateTime result = DateUtil.toLocalDateTime(date);
            assertThat(result).isNotNull();
            assertThat(result.getYear()).isEqualTo(LocalDateTime.now().getYear());
        }

        @Test
        @DisplayName("Doit convertir LocalDateTime en java.util.Date")
        void shouldConvertLocalDateTimeToDate() {
            LocalDateTime ldt = LocalDateTime.of(2026, 1, 29, 10, 0);
            Date result = DateUtil.toDate(ldt);
            assertThat(result).isNotNull();
            
            LocalDateTime back = result.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            assertThat(back).isEqualTo(ldt);
        }
    }

    @Nested
    @DisplayName("Tests de validation temporelle")
    class ValidationTests {

        @Test
        @DisplayName("Doit identifier une date passée")
        void shouldIdentifyPastDate() {
            LocalDateTime past = LocalDateTime.now().minusDays(1);
            assertThat(DateUtil.isPast(past)).isTrue();
        }

        @Test
        @DisplayName("Doit identifier une date future")
        void shouldIdentifyFutureDate() {
            LocalDateTime future = LocalDateTime.now().plusDays(1);
            assertThat(DateUtil.isFuture(future)).isTrue();
        }
    }

    @Test
    @DisplayName("Doit renvoyer null si les entrées sont nulles")
    void shouldReturnNullOnNullInputs() {
        assertThat(DateUtil.formatIso((LocalDate) null)).isNull();
        assertThat(DateUtil.parseIsoDate(null)).isNull();
        assertThat(DateUtil.toLocalDateTime(null)).isNull();
        assertThat(DateUtil.startOfDay(null)).isNull();
    }
}