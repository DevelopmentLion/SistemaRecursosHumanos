package rhumanos

class Role {
    String authority
    String descripcion

    static constraints = {
        authority blank: false, unique: true
        descripcion blank: false
    }

    static mapping = {
        table 'roles'
        cache true
    }
}
