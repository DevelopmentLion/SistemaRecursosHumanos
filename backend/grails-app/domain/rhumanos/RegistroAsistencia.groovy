package rhumanos

import com.rhumanos.RhTipoIncidencia

class RegistroAsistencia {

    static belongsTo = [
            trabajador:                 rhumanos.RhTrabajadores,
    ]
    
    //rhumanos.RhTrabajadores trabajador
    Date                                fechaAmparada
    com.rhumanos.RhTipoIncidencia       tipoIncidencia
    String                              observaciones
    
    Date                                dateCreated
    String                              creado
    
    String                              tipoRegistro //1: registro de archivo, 2: incidencias a mano
    String                              archivo
    Date                                fechaArchivo
    Integer                             lineaArchivo

    static constraints = {
        observaciones                   blank: true, nullable: true
        archivo                         blank: true, nullable: true
        fechaArchivo                    blank: true, nullable: true
        lineaArchivo                    blank: true, nullable: true
        
    }
}
