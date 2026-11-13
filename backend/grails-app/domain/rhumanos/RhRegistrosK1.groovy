package rhumanos

class RhRegistrosK1 {
    Integer                             person // el personId del checador
    Date                                datetime // La fecha y hora completa que amapra el registro
    
    String                              date // La fecha formato dd-MM-yyyy
    String                              time //La hora de la autentificacion
    
    String                              direction // e para entrada, s para salida
    
    String                              device
    String                              deviceSerial
    String                              name
    String                              card
    
    static constraints = {
        
    }
}
