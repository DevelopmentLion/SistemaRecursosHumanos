package rhumanos

class UserRole implements Serializable {
    User user
    Role role

    static belongsTo = [user: User]

    static constraints = {
        role unique: 'user'
    }

    static mapping = {
        table 'usuarios_roles'
        id composite: ['user', 'role']
        user column: 'usuarios_id'
        role column: 'roles_id'
        version false
    }
}
