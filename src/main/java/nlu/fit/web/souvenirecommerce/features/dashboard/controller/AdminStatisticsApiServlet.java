package nlu.fit.web.souvenirecommerce.features.dashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.features.dashboard.service.AdminStatisticsService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet(name = "AdminStatisticsApiServlet", urlPatterns = {"/admin/api/statistics"})
public class AdminStatisticsApiServlet extends HttpServlet {

    private final AdminStatisticsService statisticsService = new AdminStatisticsService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Kiểm tra đăng nhập và phân quyền tối thiểu
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            currentUser = (User) request.getSession().getAttribute("userInSession");
        }
        if (currentUser == null) {
            currentUser = (User) request.getSession().getAttribute("currentUser");
        }

        if (currentUser == null || currentUser.getId() == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        // 2. Phân tích các bộ lọc
        String entity = request.getParameter("entity");     // orders | products | customers | signatures
        String metric = request.getParameter("metric");     // value | quantity | status
        String period = request.getParameter("period");     // 7days | 30days | 6months | 1year

        // Gán mặc định nếu thiếu
        if (entity == null || entity.isBlank()) entity = "orders";
        if (metric == null || metric.isBlank()) metric = "value";
        if (period == null || period.isBlank()) period = "6months";

        try {
            // 3. Lấy dữ liệu từ Service
            Map<String, Object> statistics = statisticsService.getStatistics(entity, metric, period);

            // 4. Xuất dữ liệu JSON
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.write(objectMapper.writeValueAsString(statistics));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
