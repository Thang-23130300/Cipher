package nlu.fit.web.souvenirecommerce.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.dao.ProductDAO;
import nlu.fit.web.souvenirecommerce.dto.SearchSuggestionDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Product;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/search-suggestions")
public class SearchSuggestionController extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Đồng bộ tham số với JavaScript (thường là 'q')
        String keyword = request.getParameter("q");
        if (keyword == null) {
            keyword = request.getParameter("keyword");
        }

        // Kiểm tra từ khóa: Cho phép tìm từ 1 ký tự để tăng trải nghiệm người dùng
        if (keyword == null || keyword.trim().isEmpty()) {
            response.getWriter().print("[]");
            return;
        }

        keyword = keyword.trim();

        // Lấy danh sách sản phẩm từ database
        List<Product> products = productDAO.searchProductsByName(keyword, 8);
        List<SearchSuggestionDTO> result = new ArrayList<>();

        for (Product p : products) {
            SearchSuggestionDTO dto = new SearchSuggestionDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());

            // QUAN TRỌNG: Xác định giá sẽ hiển thị trên giao diện gợi ý
            double displayPrice = (p.getSalePrice() != null && p.getSalePrice() > 0)
                    ? p.getSalePrice()
                    : p.getOriginalPrice();

            // Gán vào trường 'price' (Bạn đã thêm vào DTO ở bước trước)
            // để file SearchAutocomplete.js gọi được p.price
            dto.setPrice(displayPrice);

            dto.setSalePrice(p.getSalePrice());
            dto.setOriginalPrice(p.getOriginalPrice());

            // Chuẩn hóa đường dẫn ảnh
            dto.setImage(formatImagePath(p.getImage()));

            result.add(dto);
        }

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }

    private String formatImagePath(String image) {
        if (image == null || image.trim().isEmpty()) {
            return "/assets/images/placeholder.png";
        }

        String cleanPath = image.trim().replaceAll("\\s+", "");
        if (!cleanPath.startsWith("/")) {
            cleanPath = "/" + cleanPath;
        }
        return cleanPath;
    }
}