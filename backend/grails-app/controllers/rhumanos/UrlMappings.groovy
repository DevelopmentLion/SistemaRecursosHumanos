class UrlMappings {

    static mappings = {
        "/"(controller: "rhumanos", action: "index")

        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }
    }
}
