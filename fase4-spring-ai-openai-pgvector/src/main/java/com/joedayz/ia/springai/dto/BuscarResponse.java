package com.joedayz.ia.springai.dto;

import java.util.List;
import java.util.Map;

public class BuscarResponse {
    private String query;
    private int total;
    private List<ResultadoDocumento> resultados;

    public BuscarResponse() {
    }

    public BuscarResponse(String query, List<ResultadoDocumento> resultados) {
        this.query = query;
        this.resultados = resultados;
        this.total = resultados == null ? 0 : resultados.size();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ResultadoDocumento> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoDocumento> resultados) {
        this.resultados = resultados;
        this.total = resultados == null ? 0 : resultados.size();
    }

    public static class ResultadoDocumento {
        private String contenido;
        private Map<String, Object> metadata;

        public ResultadoDocumento() {
        }

        public ResultadoDocumento(String contenido, Map<String, Object> metadata) {
            this.contenido = contenido;
            this.metadata = metadata;
        }

        public String getContenido() {
            return contenido;
        }

        public void setContenido(String contenido) {
            this.contenido = contenido;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }
}
