package gift.filter;

import gift.domain.AuthToken;
import gift.repository.token.TokenRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

import static gift.utils.FilterConstant.*;

public class OAuthTokenFilter implements Filter {

    private final TokenRepository tokenRepository;
    public OAuthTokenFilter(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.equals(HOME_URL) || path.equals(KAKAO_TOKEN_RENEW_URL) || path.startsWith(LOGIN_URL_PREFIX) || path.startsWith(LOGIN_OAUTH_URL_PREFIX) || path.startsWith(H2_DB_URL)
                || path.equals("/swagger-ui.html") // 변경
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs") // 추가
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthToken authToken = (AuthToken) httpRequest.getAttribute("AuthToken");

        if(isOAuthAccessTokenExpired(authToken)){
            if(isOAuthRefreshTokenValid(authToken)){
                httpResponse.sendRedirect("/oauth/renew/kakao?token="+authToken.getId());
                return;
            }
            tokenRepository.deleteById(authToken.getId());
            httpResponse.sendRedirect(NO_AUTHORIZATION_REDIRECT_URL);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isOAuthRefreshTokenValid(AuthToken authToken) {
        return authToken.getCreatedDate().plusSeconds(authToken.getRefreshTokenTime()).isAfter(LocalDateTime.now());
    }

    private boolean isOAuthAccessTokenExpired(AuthToken authToken) {
        return authToken.getCreatedDate().plusSeconds(authToken.getTokenTime()).isBefore(LocalDateTime.now());
    }

}
