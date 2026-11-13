package com.rhumanos

class RhCodigosPersonal {
    String codigo
    String descripcion
    com.rhumanos.RhPatron patron
    com.rhumanos.RhRama rama
    com.rhumanos.RhRelacion relacion

    static constraints = {
        relacion       blank: true, nullable: true
    }
}
