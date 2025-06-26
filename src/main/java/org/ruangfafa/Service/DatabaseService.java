package org.ruangfafa.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
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

}
