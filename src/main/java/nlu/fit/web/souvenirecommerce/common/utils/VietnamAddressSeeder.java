package nlu.fit.web.souvenirecommerce.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

public class VietnamAddressSeeder {

    private static final String BASE_URL = "https://provinces.open-api.vn/api/v2";

    private static final String DB_URL = ApplicationLoader.get("db.url");
    private static final String DB_USER = ApplicationLoader.get("db.username");
    private static final String DB_PASSWORD = ApplicationLoader.get("db.password");

    public static void main(String[] args) {
        try {
            seed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void seed() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            createTables(conn);

            String provincesJson = get(client, BASE_URL + "/p/");
            JsonNode provinces = mapper.readTree(provincesJson);

            int provinceCount = 0;
            int wardCount = 0;

            for (JsonNode provinceNode : provinces) {
                int provinceCode = provinceNode.get("code").asInt();

                String detailJson = get(client, BASE_URL + "/p/" + provinceCode + "?depth=2");
                JsonNode provinceDetail = mapper.readTree(detailJson);

                insertProvince(conn, provinceDetail);
                provinceCount++;

                JsonNode wards = provinceDetail.get("wards");

                if (wards != null && wards.isArray()) {
                    for (JsonNode wardNode : wards) {
                        insertWard(conn, wardNode, provinceCode);
                        wardCount++;
                    }
                }

                System.out.println("Imported: " + provinceDetail.get("name").asText()
                        + " - wards: " + (wards == null ? 0 : wards.size()));
            }

            conn.commit();

            System.out.println("=================================");
            System.out.println("Seed completed!");
            System.out.println("Provinces imported: " + provinceCount);
            System.out.println("Wards imported: " + wardCount);
            printStats(conn);
            System.out.println("=================================");
        }
    }

    private static String get(HttpClient client, String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("API error: " + response.statusCode() + " - " + response.body());
        }

        return response.body();
    }

    private static void createTables(Connection conn) throws SQLException {
        String provinceSql = """
            CREATE TABLE IF NOT EXISTS provinces (
                code INT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                full_name VARCHAR(255)
            )
        """;

        String wardSql = """
            CREATE TABLE IF NOT EXISTS wards (
                code INT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                full_name VARCHAR(255),
                province_code INT NOT NULL,
            
                CONSTRAINT fk_wards_province
                    FOREIGN KEY (province_code)
                    REFERENCES provinces(code)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE
            )
        """;

        try (Statement st = conn.createStatement()) {
            st.execute(provinceSql);
            st.execute(wardSql);
        }
    }

    private static void insertProvince(Connection conn, JsonNode province) throws SQLException {
        String sql = """
            INSERT INTO provinces(code, name, full_name)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                name = VALUES(name),
                full_name = VALUES(full_name)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, province.get("code").asInt());
            ps.setString(2, province.get("name").asText());
            ps.setString(3, getText(province, "full_name", province.get("name").asText()));
            ps.executeUpdate();
        }
    }

    private static void insertWard(Connection conn, JsonNode ward, int provinceCode) throws SQLException {
        String sql = """
            INSERT INTO wards(code, name, full_name, province_code)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                name = VALUES(name),
                full_name = VALUES(full_name),
                province_code = VALUES(province_code)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ward.get("code").asInt());
            ps.setString(2, ward.get("name").asText());
            ps.setString(3, getText(ward, "full_name", ward.get("name").asText()));
            ps.setInt(4, provinceCode);
            ps.executeUpdate();
        }
    }

    private static String getText(JsonNode node, String field, String defaultValue) {
        return node.hasNonNull(field) ? node.get(field).asText() : defaultValue;
    }

    private static void printStats(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM provinces")) {
                if (rs.next()) {
                    System.out.println("DB provinces count: " + rs.getInt(1));
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM wards")) {
                if (rs.next()) {
                    System.out.println("DB wards count: " + rs.getInt(1));
                }
            }
        }
    }
}