package org.example.config.pebble;

import io.pebbletemplates.boot.autoconfigure.PebbleAutoConfiguration;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Configuration
@AutoConfigureBefore(PebbleAutoConfiguration.class)
public class PebbleConfig {

    @Bean
    public Extension customPebbleExtension() {
        return new AbstractExtension() {
            @Override
            public Map<String, Filter> getFilters() {
                Map<String, Filter> filters = new HashMap<>();
                filters.put("formatDate", new FormatDateFilter());
                filters.put("formatPrice", new FormatPriceFilter());
                filters.put("formatMonth", new FormatMonthFilter());
                filters.put("formatDateTime", new FormatDateTimeFilter());
                filters.put("templateName", new TemplateNameFilter());
                return filters;
            }
        };
    }

    // Filtro: {{ fecha | formatDate }} -> 31/12/2025
    private static class FormatDateFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                            EvaluationContext context, int lineNumber) throws PebbleException {
            if (input == null) {
                return "";
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.of("es", "ES"));

                switch (input) {
                    case LocalDate localDate -> {
                        return localDate.format(formatter);
                    }
                    case Date date -> {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.of("es", "ES"));
                        return sdf.format(date);
                    }
                    case String _ -> {
                        try {
                            LocalDate date = LocalDate.parse(input.toString());
                            return date.format(formatter);
                        } catch (Exception e) {
                            return input.toString();
                        }
                    }
                    default -> {}
                }
            } catch (Exception e) {
                return input.toString();
            }

            return input.toString();
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // Filtro: {{ fecha | formatMonth }} -> Enero
    private static class FormatMonthFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                            EvaluationContext context, int lineNumber) throws PebbleException {
            if (input == null) {
                return "";
            }

            try {
                if (input instanceof LocalDateTime dateTime) {
                    return dateTime.getMonth().getDisplayName(
                            java.time.format.TextStyle.FULL, Locale.of("es", "ES")
                    );
                }
            } catch (Exception e) {
                return "";
            }

            return "";
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // Filtro: {{ fecha | formatDateTime }} -> 31 Dic 2025, 23:59
    private static class FormatDateTimeFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                            EvaluationContext context, int lineNumber) throws PebbleException {
            if (input == null) {
                return "";
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.of("es", "ES"));

                if (input instanceof LocalDateTime) {
                    return ((LocalDateTime) input).format(formatter);
                }
            } catch (Exception e) {
                return input.toString();
            }

            return input.toString();
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // Filtro: {{ precio | formatPrice }} -> 1.250,50 €
    private static class FormatPriceFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                            EvaluationContext context, int lineNumber) throws PebbleException {
            if (input == null) {
                return "0,00 €";
            }

            try {
                double price;
                if (input instanceof Number) {
                    price = ((Number) input).doubleValue();
                } else {
                    price = Double.parseDouble(input.toString());
                }

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("es", "ES"));
                symbols.setDecimalSeparator(',');
                symbols.setGroupingSeparator('.');

                DecimalFormat df = new DecimalFormat("#,##0.00 €", symbols);
                return df.format(price);
            } catch (Exception e) {
                return input + " €";
            }
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // Filtro auxiliar para lógica de plantillas
    private static class TemplateNameFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                            EvaluationContext context, int lineNumber) throws PebbleException {
            if (input == null) {
                return "";
            }
            String fullPath = input.toString();
            try {
                String[] parts = fullPath.split("/");
                return parts[parts.length - 1];
            } catch (Exception e) {
                return fullPath;
            }
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }
}