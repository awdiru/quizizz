package ru.programm_shcool.quizizz.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.programm_shcool.quizizz.repository.TokenRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Разрешаем все GET запросы к корню и статическим ресурсам
        if (isStatic(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Пропускаем публичные API эндпоинты
        if (isPublicApi(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Для всех остальных запросов проверяем аутентификацию
        String username = request.getHeader("X-Username");
        String token = request.getHeader("X-Token");

        if (username == null || token == null) {
            sendError(response, "Missing authentication headers", 401);
            return;
        }

        if (!tokenRepository.validateToken(username, token)) {
            sendError(response, "Invalid or expired token", 401);
            return;
        }

        request.setAttribute("username", username);
        filterChain.doFilter(request, response);
    }

    private boolean isStatic(String path, String method) {
        return "GET".equalsIgnoreCase(method) &&
                ((path.equals("/")
                        || path.isEmpty())
                        || path.endsWith(".html")
                        || path.endsWith(".css")
                        || path.endsWith(".js"));

    }

    private boolean isPublicApi(String path) {
        return path.equals("/login") ||
                path.startsWith("/public/") ||
                path.equals("/health");
    }

    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\", \"status\": " + status + "}");
    }
}