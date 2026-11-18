package rhumanos

import grails.plugin.springsecurity.SpringSecurityUtils

class BootStrap {

    def init = { servletContext ->
        // Crear roles si no existen
        def adminRole = Role.findByAuthority('ROLE_RH_ADMIN') ?: new Role(authority: 'ROLE_RH_ADMIN').save(failOnError: true)
        def userRole = Role.findByAuthority('ROLE_RH_USUARIO') ?: new Role(authority: 'ROLE_RH_USUARIO').save(failOnError: true)

        // Crear usuario admin si no existe
        def adminUser = User.findByUsername('admin')
        if (!adminUser) {
            adminUser = new User(
                username: 'admin',
                password: SpringSecurityUtils.encodePassword('admin123'),
                enabled: true
            ).save(failOnError: true)

            // Asignar rol admin
            UserRole.create(adminUser, adminRole, true)
        }

        println "Bootstrap completado. Usuario: admin / Contraseña: admin123"
    }

    def destroy = {
    }
}
