package rhumanos

class User {
    String username
    String password
    String nombres
    String apellidoPaterno
    String apellidoMaterno
    String correo
    String curp
    String cedula
    String sexo
    String especialidad
    String observaciones
    String actualizadoPor
    String creadoPor
    String otroServicioAtencion
    String otroTipoPersonal
    String institucionCedula
    String telegramChatId
    Integer programa
    Integer servicioAtencion
    Integer tipoPersonal

    boolean enabled = true
    boolean accountExpired = false
    boolean accountLocked = false
    boolean passwordExpired = false

    Date dateCreated
    Date lastUpdated

    static hasMany = [roles: Role]

    static constraints = {
        username blank: false, unique: true
        password blank: false
        nombres blank: false
        apellidoPaterno blank: false
        correo blank: false
        sexo blank: false
        actualizadoPor nullable: true
        creadoPor nullable: true
        cedula nullable: true
        curp nullable: true
        especialidad nullable: true
        observaciones nullable: true
        otroServicioAtencion nullable: true
        otroTipoPersonal nullable: true
        institucionCedula nullable: true
        telegramChatId nullable: true
        programa nullable: true
        servicioAtencion nullable: true
        tipoPersonal nullable: true
    }

    static mapping = {
        table 'usuarios'
        password column: '`password`'
        apellidoPaterno column: 'apellido_paterno'
        apellidoMaterno column: 'apellido_materno'
        correo column: 'correo'
        actualizadoPor column: 'actualizado_por'
        creadoPor column: 'creado_por'
        accountExpired column: 'account_expired'
        accountLocked column: 'account_locked'
        passwordExpired column: 'password_expired'
        dateCreated column: 'date_created'
        lastUpdated column: 'last_updated'
        otroServicioAtencion column: 'otro_servicio_atencion'
        otroTipoPersonal column: 'otro_tipo_personal'
        servicioAtencion column: 'servicio_atencion'
        tipoPersonal column: 'tipo_personal'
        institucionCedula column: 'institucion_cedula'
        telegramChatId column: 'telegram_chat_id'
    }
}
