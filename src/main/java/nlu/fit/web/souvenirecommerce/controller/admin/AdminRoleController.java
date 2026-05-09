package nlu.fit.web.souvenirecommerce.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.dao.AuthorizationDAO;
import nlu.fit.web.souvenirecommerce.dao.UserDAO;
import nlu.fit.web.souvenirecommerce.model.PermissionGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/roles")
public class AdminRoleController extends HttpServlet {
    private AuthorizationDAO authorizationDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        authorizationDAO = new AuthorizationDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("roles", authorizationDAO.getAllRoleGroups());
        req.setAttribute("permissions", authorizationDAO.getAllPermissions());
        req.setAttribute("users", userDAO.getAllUsers());

        String editId = req.getParameter("editId");
        if (editId != null && !editId.isBlank()) {
            try {
                int roleId = Integer.parseInt(editId);
                PermissionGroup role = authorizationDAO.getRoleById(roleId);
                if (role != null) {
                    req.setAttribute("editRole", role);
                    req.setAttribute("editPermissionIds", authorizationDAO.getPermissionIdsByRoleId(roleId));
                    List<String> editUserIds = new ArrayList<>();
                    for (Long userId : authorizationDAO.getUserIdsByRoleId(roleId)) {
                        editUserIds.add(String.valueOf(userId));
                    }
                    req.setAttribute("editUserIds", editUserIds);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String message = (String) req.getSession().getAttribute("message");
        String messageType = (String) req.getSession().getAttribute("messageType");
        if (message != null) {
            req.setAttribute("message", message);
            req.setAttribute("messageType", messageType);
            req.getSession().removeAttribute("message");
            req.getSession().removeAttribute("messageType");
        }

        req.getRequestDispatcher("/admin/roles.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("delete".equalsIgnoreCase(action)) {
                int roleId = Integer.parseInt(req.getParameter("id"));
                if (authorizationDAO.deleteRole(roleId)) {
                    req.getSession().setAttribute("message", "Xóa nhóm quyền thành công!");
                    req.getSession().setAttribute("messageType", "success");
                } else {
                    req.getSession().setAttribute("message", "Không thể xóa nhóm quyền hệ thống!");
                    req.getSession().setAttribute("messageType", "error");
                }
            } else if ("assignUsers".equalsIgnoreCase(action)) {
                int roleId = Integer.parseInt(req.getParameter("roleId"));
                List<Long> userIds = parseLongList(req.getParameterValues("userIds"));
                if (authorizationDAO.assignUsersToRole(roleId, userIds)) {
                    req.getSession().setAttribute("message", "Cập nhật người dùng cho nhóm quyền thành công!");
                    req.getSession().setAttribute("messageType", "success");
                } else {
                    req.getSession().setAttribute("message", "Cập nhật người dùng cho nhóm quyền thất bại!");
                    req.getSession().setAttribute("messageType", "error");
                }
            } else {
                Integer roleId = null;
                String idParam = req.getParameter("id");
                if (idParam != null && !idParam.isBlank()) {
                    roleId = Integer.parseInt(idParam);
                }

                String name = req.getParameter("name");
                String description = req.getParameter("description");
                List<Integer> permissionIds = parseIntegerList(req.getParameterValues("permissionIds"));

                if (authorizationDAO.saveRole(roleId, name, description, permissionIds)) {
                    req.getSession().setAttribute("message", roleId == null
                            ? "Tạo nhóm quyền thành công!"
                            : "Cập nhật nhóm quyền thành công!");
                    req.getSession().setAttribute("messageType", "success");
                } else {
                    req.getSession().setAttribute("message", "Không thể lưu nhóm quyền!");
                    req.getSession().setAttribute("messageType", "error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            req.getSession().setAttribute("messageType", "error");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/roles");
    }

    private List<Integer> parseIntegerList(String[] values) {
        List<Integer> list = new ArrayList<>();
        if (values == null) {
            return list;
        }
        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }
            list.add(Integer.parseInt(value));
        }
        return list;
    }

    private List<Long> parseLongList(String[] values) {
        List<Long> list = new ArrayList<>();
        if (values == null) {
            return list;
        }
        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }
            list.add(Long.parseLong(value));
        }
        return list;
    }
}
