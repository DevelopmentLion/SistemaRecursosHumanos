package rhumanos

class UserRole implements Serializable {
    User user
    Role role

    static belongsTo = [user: User]

    static constraints = {
        role unique: 'user'
    }

    static mapping = {
        id composite: ['user', 'role']
        version false
    }
}
