package rhumanos

class BootStrap {
    def init = { servletContext ->
        println "Bootstrap completado."
    }
    def destroy = {
    }
}
