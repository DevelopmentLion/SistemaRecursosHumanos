package com.rhumanos

class RhHorarios {
    String horarioDescripcion
    com.rhumanos.RhJornada horarioTurno
    Integer horarioJornadaHrs
    String horarioObservaciones
    String horarioActivo
    String lunesIn
    String lunesOut
    
    String martesIn
    String martesOut
    
    String miercolesIn
    String miercolesOut
    
    String juevesIn
    String juevesOut
    
    String viernesIn
    String viernesOut
    
    String sabadoIn
    String sabadoOut
    
    String domingoIn
    String domingoOut
    
    Date dateCreated
    Date lastUpdated

    static constraints = {
        horarioObservaciones blank: true, nullable: true
        horarioActivo blank: true, nullable: true
        
        lastUpdated blank: true, nullable: true
        
        lunesIn blank: true, nullable: true
        martesIn blank: true, nullable: true
        miercolesIn blank: true, nullable: true
        juevesIn blank: true, nullable: true
        viernesIn blank: true, nullable: true
        sabadoIn blank: true, nullable: true
        domingoIn blank: true, nullable: true
        
        lunesOut blank: true, nullable: true
        martesOut blank: true, nullable: true
        miercolesOut blank: true, nullable: true
        juevesOut blank: true, nullable: true
        viernesOut blank: true, nullable: true
        sabadoOut blank: true, nullable: true
        domingoOut blank: true, nullable: true
    }
}
