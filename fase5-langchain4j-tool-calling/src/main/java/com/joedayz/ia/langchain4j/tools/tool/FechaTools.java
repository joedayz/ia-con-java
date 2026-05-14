package com.joedayz.ia.langchain4j.tools.tool;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Lab 14: Herramientas de fecha/hora con @Tool de LangChain4j.
 *
 * Demuestra herramientas sin parámetros que proveen información
 * en tiempo real al LLM.
 */
@Component
public class FechaTools {

    private static final Logger log = LoggerFactory.getLogger(FechaTools.class);

    private static final Locale LOCALE_ES = Locale.of("es", "ES");

    @Tool("Obtiene la fecha actual en formato legible en español")
    public String fechaActual() {
        String fecha = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", LOCALE_ES));
        log.info("📅 fechaActual() = {}", fecha);
        return fecha;
    }

    @Tool("Obtiene la fecha y hora actual en formato legible en español")
    public String fechaHoraActual() {
        String fechaHora = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy, HH:mm:ss", LOCALE_ES));
        log.info("📅 fechaHoraActual() = {}", fechaHora);
        return fechaHora;
    }
}
