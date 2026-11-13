package com.rhumanos

class RhTipoIncidencia {
    String tipoIncidencia
    String simbologia
    String observaciones

    static constraints = {
        observaciones blank: true, nullable: true
    }
}
