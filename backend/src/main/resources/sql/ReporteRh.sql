-- ============================================================================
-- ReporteRh.sql
--
-- Consulta usada por RhumanosController#printMes para obtener los registros de
-- asistencia del nuevo checador del mes seleccionado.
--
-- El controlador la ejecuta con groovy.sql.Sql.rows() pasando estos parámetros
-- con nombre:
--   :fechaDe   -> inicio del mes (java.util.Date)
--   :fechaA    -> fin del mes   (java.util.Date)
--   :jornada   -> id de la jornada/turno (Long)
--   :relacion  -> id de la relación laboral (Long)
--
-- Columnas esperadas por el código (snake_case):
--   id, nombre_completo, curp, rfc, apellido_paterno, apellido_materno,
--   nombres, clave_pago, horario_texto, jornada, observaciones_reporte,
--   observaciones, relacion, codigo, descripcion, area_laboral, fecha, dia,
--   mes, anio, horas
--
-- NOTA: restaura aquí la consulta SQL original del sistema anterior
-- (antes se leía desde el directorio web /sql/ReporteRh.sql).
-- ============================================================================
SELECT 1 AS id,
       '' AS nombre_completo,
       '' AS curp,
       '' AS rfc,
       '' AS apellido_paterno,
       '' AS apellido_materno,
       '' AS nombres,
       '' AS clave_pago,
       '' AS horario_texto,
       '' AS jornada,
       '' AS observaciones_reporte,
       '' AS observaciones,
       '' AS relacion,
       '' AS codigo,
       '' AS descripcion,
       '' AS area_laboral,
       NULL AS fecha,
       '' AS dia,
       '' AS mes,
       '' AS anio,
       '' AS horas
WHERE 1 = 0;
