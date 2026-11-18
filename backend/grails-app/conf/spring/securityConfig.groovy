import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

beans = {
    // CORS Filter configuration
    corsFilter(CorsFilter) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        CorsConfiguration config = new CorsConfiguration()
        config.setAllowCredentials(true)
        config.addAllowedOrigin("http://localhost:5173")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        delegate = source
    }

    // Register CORS filter with high precedence
    corsFilterRegistration(FilterRegistrationBean) {
        filter = ref('corsFilter')
        urlPatterns = ['/*']
        order = 1
    }
}
