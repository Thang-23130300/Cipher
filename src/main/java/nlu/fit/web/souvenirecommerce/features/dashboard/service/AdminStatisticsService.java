package nlu.fit.web.souvenirecommerce.features.dashboard.service;

import nlu.fit.web.souvenirecommerce.features.dashboard.dao.AdminStatisticsDAO;
import java.util.*;

public class AdminStatisticsService {

    private final AdminStatisticsDAO statisticsDAO = new AdminStatisticsDAO();

    public Map<String, Object> getStatistics(String entity, String metric, String period) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. Phân tích khoảng thời gian
        int days = 30;
        int months = 6;

        if ("7days".equalsIgnoreCase(period)) {
            days = 7;
        } else if ("30days".equalsIgnoreCase(period)) {
            days = 30;
        } else if ("6months".equalsIgnoreCase(period)) {
            months = 6;
            days = 180;
        } else if ("1year".equalsIgnoreCase(period)) {
            months = 12;
            days = 365;
        }

        // 2. Lấy dữ liệu và cấu hình biểu đồ theo thực thể (entity) và tiêu chí (metric)
        Map<String, Double> statData = new LinkedHashMap<>();
        String chartType = "line";
        String title = "Thống kê";
        String labelName = "Số lượng";
        String valueType = "number"; // number | currency

        if ("orders".equalsIgnoreCase(entity)) {
            if ("value".equalsIgnoreCase(metric)) {
                title = "Thống kê doanh thu bán hàng (" + getPeriodLabel(period) + ")";
                labelName = "Doanh thu";
                valueType = "currency";
                chartType = "line";
                if ("6months".equalsIgnoreCase(period) || "1year".equalsIgnoreCase(period)) {
                    statData = statisticsDAO.getOrderStatsByMonth(months, "value");
                } else {
                    statData = statisticsDAO.getOrderStatsByDay(days, "value");
                }
            } else if ("quantity".equalsIgnoreCase(metric)) {
                title = "Thống kê lượng đơn đặt hàng (" + getPeriodLabel(period) + ")";
                labelName = "Đơn đặt hàng";
                valueType = "number";
                chartType = "line";
                if ("6months".equalsIgnoreCase(period) || "1year".equalsIgnoreCase(period)) {
                    statData = statisticsDAO.getOrderStatsByMonth(months, "quantity");
                } else {
                    statData = statisticsDAO.getOrderStatsByDay(days, "quantity");
                }
            } else { // status
                title = "Tỉ lệ đơn hàng theo trạng thái (" + getPeriodLabel(period) + ")";
                labelName = "Tổng số đơn";
                valueType = "number";
                chartType = "doughnut";
                statData = statisticsDAO.getOrderStatsByStatus(days);
            }
        } 
        else if ("products".equalsIgnoreCase(entity)) {
            if ("value".equalsIgnoreCase(metric)) {
                title = "Top 10 sản phẩm đem lại doanh thu cao nhất (" + getPeriodLabel(period) + ")";
                labelName = "Doanh thu mang lại";
                valueType = "currency";
                chartType = "bar";
                statData = statisticsDAO.getProductSalesRevenue(days);
            } else if ("quantity".equalsIgnoreCase(metric)) {
                title = "Top 10 sản phẩm bán chạy nhất (" + getPeriodLabel(period) + ")";
                labelName = "Số lượng đã bán";
                valueType = "number";
                chartType = "bar";
                statData = statisticsDAO.getProductSalesQuantity(days);
            } else { // status
                title = "Cơ cấu tồn kho của các sản phẩm";
                labelName = "Số sản phẩm";
                valueType = "number";
                chartType = "pie";
                statData = statisticsDAO.getProductStockStatus();
            }
        } 
        else if ("customers".equalsIgnoreCase(entity)) {
            if ("value".equalsIgnoreCase(metric)) {
                title = "Top 10 khách hàng chi tiêu nhiều nhất (" + getPeriodLabel(period) + ")";
                labelName = "Tổng chi tiêu";
                valueType = "currency";
                chartType = "bar";
                statData = statisticsDAO.getCustomerSpendings(days);
            } else if ("status".equalsIgnoreCase(metric)) {
                title = "Trạng thái tài khoản người dùng";
                labelName = "Số tài khoản";
                valueType = "number";
                chartType = "pie";
                statData = statisticsDAO.getCustomerActiveStatus();
            } else { // quantity / default
                title = "Số lượng khách hàng đăng ký mới (" + getPeriodLabel(period) + ")";
                labelName = "Khách hàng mới";
                valueType = "number";
                chartType = "line";
                if ("6months".equalsIgnoreCase(period) || "1year".equalsIgnoreCase(period)) {
                    statData = statisticsDAO.getCustomerRegistrationsByMonth(months);
                } else {
                    statData = statisticsDAO.getCustomerRegistrationsByDay(days);
                }
            }
        } 
        else if ("signatures".equalsIgnoreCase(entity)) {
            if ("status".equalsIgnoreCase(metric)) {
                title = "Tỉ lệ chữ ký số theo trạng thái xác minh (" + getPeriodLabel(period) + ")";
                labelName = "Chữ ký";
                valueType = "number";
                chartType = "doughnut";
                statData = statisticsDAO.getSignatureStatsByStatus(days);
            } else if ("value".equalsIgnoreCase(metric)) {
                title = "Giá trị đơn hàng hoàn thành theo trạng thái ký số (" + getPeriodLabel(period) + ")";
                labelName = "Tổng giá trị";
                valueType = "currency";
                chartType = "pie";
                statData = statisticsDAO.getOrderSignatureValueStats(days);
            } else { // quantity / default
                title = "Số lượng đơn hàng đã ký số thành công (" + getPeriodLabel(period) + ")";
                labelName = "Số chữ ký";
                valueType = "number";
                chartType = "line";
                if ("6months".equalsIgnoreCase(period) || "1year".equalsIgnoreCase(period)) {
                    statData = statisticsDAO.getSignedOrdersCountByMonth(months);
                } else {
                    statData = statisticsDAO.getSignedOrdersCountByDay(days);
                }
            }
        }

        // 3. Đóng gói kết quả dạng List phục vụ JSON
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (Map.Entry<String, Double> entry : statData.entrySet()) {
            labels.add(entry.getKey());
            values.add(entry.getValue());
        }

        result.put("labels", labels);
        result.put("data", values);
        result.put("chartType", chartType);
        result.put("title", title);
        result.put("labelName", labelName);
        result.put("valueType", valueType);

        return result;
    }

    private String getPeriodLabel(String period) {
        if ("7days".equalsIgnoreCase(period)) return "7 ngày qua";
        if ("30days".equalsIgnoreCase(period)) return "30 ngày qua";
        if ("6months".equalsIgnoreCase(period)) return "6 tháng qua";
        if ("1year".equalsIgnoreCase(period)) return "1 năm qua";
        return "30 ngày qua";
    }
}
