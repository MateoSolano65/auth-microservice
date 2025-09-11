package co.com.pragma.jwt;

public class PublicRoutes {
    
    public static final String[] PUBLIC_PATHS = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/swagger-ui/**",
            "/api/v1/login",
            "/api/v1/token",
            "/v3/api-docs/swagger-config"
//            ,"/api/users/validate"
//             ,"/api/users"
    };
    
    public static boolean isPublic(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            String normalizedPath = publicPath.replace("/**", "");
            if (path.startsWith(normalizedPath)) {
                return true;
            }
        }
        return false;
    }
}