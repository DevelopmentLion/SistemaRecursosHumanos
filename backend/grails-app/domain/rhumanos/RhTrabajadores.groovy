package rhumanos

import com.rhumanos.RhAreaLaboral
import com.rhumanos.RhCodigosPersonal
import com.rhumanos.RhFuncionReal
import com.rhumanos.RhJornada
import com.rhumanos.RhRelacion

class RhTrabajadores {

    static hasMany = [
            incidencias:                    rhumanos.RegistroAsistencia,
    ]
    
    String                                  nombres
    String                                  apellidoPaterno
    String                                  apellidoMaterno
    String                                  activo
    String                                  rfc
    String                                  curp
    String                                  telefonos
    String                                  email
    
    com.rhumanos.RhCodigosPersonal          codigoTrabajador
    com.rhumanos.RhRelacion                 relacionLaboral
    com.rhumanos.RhAreaLaboral              areaLaboral
    com.rhumanos.RhFuncionReal              funcionReal
    com.rhumanos.RhJornada                  turno
    String                                  clavePago
    String                                  horarioTexto
    
    String                                  observacionesReporte
    String                                  observaciones
    
    static mapping = {
        observaciones                       type:"text"
    }
    
    static constraints = {
        apellidoMaterno                     blank: true, nullable: true
        telefonos                           blank: true, nullable: true
        email                               blank: true, nullable: true
        observacionesReporte                blank: true, nullable: true
        observaciones                       blank: true, nullable: true
    }
}
