package nlu.fit.web.souvenirecommerce.controller.cloudinary;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import nlu.fit.web.souvenirecommerce.service.CloudinaryService;

import java.io.IOException;

@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Part filePart = request.getPart("file");
            byte[] fileBytes = filePart.getInputStream().readAllBytes();

            CloudinaryService.uploadImage(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("dashboard");
    }
}