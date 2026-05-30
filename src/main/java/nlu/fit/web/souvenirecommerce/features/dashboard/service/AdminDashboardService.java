package nlu.fit.web.souvenirecommerce.features.dashboard.service;

import nlu.fit.web.souvenirecommerce.legacy.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.legacy.dao.ProductDAO;
import nlu.fit.web.souvenirecommerce.legacy.dao.impl.UserDAOImpl;
import nlu.fit.web.souvenirecommerce.features.dashboard.dto.CategorySalesDTO;
import nlu.fit.web.souvenirecommerce.features.dashboard.dto.DashboardMetricsDTO;
import nlu.fit.web.souvenirecommerce.features.dashboard.dto.LowStockProductDTO;
import nlu.fit.web.souvenirecommerce.legacy.model.Order;
import nlu.fit.web.souvenirecommerce.model.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardService {

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAOImpl userDAO = new UserDAOImpl();

    public DashboardMetricsDTO buildDashboardMetrics() {
        return buildDashboardMetrics(6, 5, 10, 10, 7, 5);
    }

    public DashboardMetricsDTO buildDashboardMetrics(int months,
                                                    int recentOrdersLimit,
                                                    int topProductsLimit,
                                                    int lowStockThreshold,
                                                    int newCustomerDays,
                                                    int topCategoriesLimit) {
        DashboardMetricsDTO dto = new DashboardMetricsDTO();

        dto.setTotalProducts(productDAO.getTotalProducts());
        dto.setTotalCustomers(userDAO.getTotalCustomers());
        dto.setTotalRevenue(orderDAO.getTotalRevenue());
        dto.setTotalOrders(orderDAO.getTotalOrders());

        dto.setTopProducts(productDAO.getTopSellingProducts(topProductsLimit));
        dto.setRecentOrders(orderDAO.getRecentOrders(recentOrdersLimit));

        dto.setMonthlyRevenue(orderDAO.getMonthlyRevenueData(months));
        dto.setMonthlyOrders(orderDAO.getMonthlyOrdersData(months));
        dto.setOrderStatusCounts(orderDAO.getOrderStatusCounts());
        dto.setLowStockProducts(mapLowStockProducts(productDAO.getLowStockProducts(lowStockThreshold), lowStockThreshold));
        dto.setNewCustomersLast7Days(userDAO.getNewCustomerCount(newCustomerDays));
        dto.setTopCategoriesBySales(productDAO.getTopCategoriesBySales(topCategoriesLimit));

        return dto;
    }

    public Map<String, Integer> getOrderStatusCounts() {
        return orderDAO.getOrderStatusCounts();
    }

    public List<Integer> getMonthlyOrdersData(int months) {
        return orderDAO.getMonthlyOrdersData(months);
    }

    public List<LowStockProductDTO> getLowStockProducts(int threshold) {
        return mapLowStockProducts(productDAO.getLowStockProducts(threshold), threshold);
    }

    private List<LowStockProductDTO> mapLowStockProducts(List<Product> products, int threshold) {
        List<LowStockProductDTO> lowStock = new ArrayList<>();
        for (Product product : products) {
            LowStockProductDTO dto = new LowStockProductDTO();
            dto.setProductId(product.getId());
            dto.setName(product.getName());
            dto.setStockQuantity(product.getStockQuantity());
            dto.setThreshold(threshold);
            lowStock.add(dto);
        }
        return lowStock;
    }

    public List<CategorySalesDTO> getTopCategoriesBySales(int limit) {
        return productDAO.getTopCategoriesBySales(limit);
    }
}
