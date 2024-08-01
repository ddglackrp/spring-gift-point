package gift.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FilterErrorResponse {

    public static final String CODE = "401";
    public static final String MESSAGE = "인증되지 않은 사용자 입니다.";

    public static void sendErrorResponse(HttpServletResponse response, int statusCode) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(statusCode);
        response.setContentType("application/json");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", CODE);
        errorResponse.put("message", MESSAGE);

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

}
