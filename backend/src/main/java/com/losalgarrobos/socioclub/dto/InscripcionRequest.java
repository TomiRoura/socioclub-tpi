package com.losalgarrobos.socioclub.dto;

// Datos para pedir una inscripción: { "socioId": 3, "actividadId": 1 }.
// No es la entidad Inscripcion (que tiene un Socio y una Actividad completos, no sus ids),
// por eso hace falta esta clase chica. La usan el POST de la API (@RequestBody) y el
// formulario de inscripción de la secretaría (th:object).
public class InscripcionRequest {

    private Long socioId;
    private Long actividadId;

    public InscripcionRequest() {
    }

    public InscripcionRequest(Long socioId, Long actividadId) {
        this.socioId = socioId;
        this.actividadId = actividadId;
    }

    public Long getSocioId() {
        return socioId;
    }

    public void setSocioId(Long socioId) {
        this.socioId = socioId;
    }

    public Long getActividadId() {
        return actividadId;
    }

    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }
}
