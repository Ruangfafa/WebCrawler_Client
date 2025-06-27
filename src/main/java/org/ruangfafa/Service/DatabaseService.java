package org.ruangfafa.Service;

import org.ruangfafa.Model.Classificate;
import org.ruangfafa.Model.Product;
import org.ruangfafa.Model.ProductTag;
import org.ruangfafa.Model.Seller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class DatabaseService {

    private static String URL, USER, PASSWORD, DEDDB;
    private static final String WORKDIC = "DatabaseService.java";

    static {
        try (InputStream input = DatabaseService.class.getClassLoader().getResourceAsStream("LocalVars.properties")) {
            Properties props = new Properties();
            if (input != null) {
                props.load(input);
                URL = props.getProperty("db.url");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");
                DEDDB = USER.replace("WebCrawler_", "");
            } else {
                Logger.log("❌ 找不到 LocalVars.properties", WORKDIC);
                throw new IOException("找不到 LocalVars.properties");
            }
        } catch (IOException e) {
            Logger.log("❌ 配置加载失败: " + e.getMessage(), WORKDIC);
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Logger.log("✅ 成功连接到数据库！", WORKDIC);
            return conn;
        } catch (SQLException e) {
            Logger.log("❌ 无法连接数据库: " + e.getMessage(), WORKDIC);
            return null;
        }
    }

    public static int getState(Connection conn, boolean isServer) {
        String schemaName = isServer ? "ServerDB" : DEDDB;
        String sql = "SELECT state FROM " + schemaName + ".State";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("state");
            }
        } catch (Exception e) {
            Logger.log("❌ 查询 " + schemaName + ".State 状态失败: " + e.getMessage(), "Application.java");
        }
        return 0; // 默认返回 0 表示“运行中”或“未知状态”
    }

    public static String popUrl(Connection conn) {
        String url = null;
        String selectSql = "SELECT url FROM " + DEDDB + ".Task LIMIT 1";
        String deleteSql = "DELETE FROM " + DEDDB + ".Task WHERE url = ? LIMIT 1";

        try (
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                ResultSet rs = selectStmt.executeQuery()
        ) {
            if (rs.next()) {
                url = rs.getString("url");

                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setString(1, url);
                    deleteStmt.executeUpdate();
                } catch (SQLException deleteEx) {
                    Logger.log("❌ 删除任务 URL 失败: " + deleteEx.getMessage(), WORKDIC);
                }
            }
        } catch (SQLException e) {
            Logger.log("❌ 提取任务 URL 失败: " + e.getMessage(), WORKDIC);
        }

        return url; // 如果为 null 表示没有任务
    }

    public static void taskDone(Connection conn) {
        String sql = "UPDATE " + DEDDB + ".State SET state = 0";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                Logger.log("✅ 任务完成，已将状态置为 0", WORKDIC);
            } else {
                Logger.log("⚠️ taskDone 执行无更新（State 表可能为空）", WORKDIC);
            }
        } catch (SQLException e) {
            Logger.log("❌ taskDone 状态更新失败: " + e.getMessage(), WORKDIC);
        }
    }

    public static void insertSeller(Connection conn, Seller seller) {
        String sql = "INSERT INTO ServerDB.Sellers " +
                "(identifier, name, location, pageType, subscribe, qualityScore, garenteeScore, logisticsScore) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seller.getIdentifier());
            stmt.setString(2, seller.getName());
            stmt.setString(3, seller.getLocation());
            stmt.setString(4, seller.getPageType());
            stmt.setString(5, seller.getSubscribe() );
            stmt.setString(6, seller.getQualityScore());
            stmt.setString(7, seller.setGarenteeScore());
            stmt.setString(8, seller.getLogisticsScore() );
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.log("❌ 插入 Seller 数据失败: " + e.getMessage(), "DatabaseService.java");
        }
    }

    public static void insertClassificate(Connection conn, Classificate obj) {
        String sql = "INSERT INTO ServerDB.Classificate (pageType, identifier, category_pv, cName) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getPageType());
            stmt.setString(2, obj.getIdentifier());
            stmt.setString(3, obj.getCategory_pv());
            stmt.setString(4, obj.getCName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.log("❌ 插入 Classificate 数据失败: " + e.getMessage(), "DatabaseService.java");
        }
    }

    public static void insertProductTag(Connection conn, ProductTag obj) {
        String sql = "INSERT INTO ServerDB.ProductTag (pageType, identifier, id, soldAmount, tag) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getPageType());
            stmt.setString(2, obj.getIdentifier());
            stmt.setString(3, obj.getId());
            stmt.setString(4, obj.getSoldAmount());
            stmt.setString(5, obj.getTag());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.log("❌ 插入 ProductTag 数据失败: " + e.getMessage(), "DatabaseService.java");
        }
    }

    public static void insertProduct(Connection conn, Product obj) {
        String sql = "INSERT INTO ServerDB.Product (pageType, id, skuid, orgnPrice, diskPrice, title, soldAmount365, " +
                "storageAddress, guarantee, pattern, storageLeft, parameter, patternCom, image, imageSet) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getPageType());
            stmt.setString(2, obj.getId());
            stmt.setString(3, obj.getSkuid());
            stmt.setString(4, obj.getOrgnPrice());
            stmt.setString(5, obj.getDiskPrice());
            stmt.setString(6, obj.getTitle());
            stmt.setString(7, obj.getSoldAmount365());
            stmt.setString(8, obj.getStorageAddress());
            stmt.setString(9, obj.getGuarantee());
            stmt.setString(10, obj.getPattern());
            stmt.setString(11, obj.getStorageLeft());
            stmt.setString(12, obj.getParameter());
            stmt.setString(13, obj.getPatternCom());
            stmt.setString(14, obj.getImage());
            stmt.setString(15, obj.getImageSet());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.log("❌ 插入 Product 数据失败: " + e.getMessage(), "DatabaseService.java");
        }
    }
}
