package com.example.gruppensystem.Datenbank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatenbankZugang {
    private static HikariConfig hikariConfig = new HikariConfig();
    private static HikariDataSource hikariDataSource;
    public static void initDbZugang(){
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/gruppensystem");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("0101");
        hikariConfig.setMaxLifetime(600000L);
        hikariConfig.setLeakDetectionThreshold(10000L);
        hikariConfig.setConnectionTimeout(10000L);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("userServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize" , "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");
        hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public static void schliesseDbZugang(){
        hikariDataSource.close();
    }

    public static Connection getConnection() throws SQLException {
        if(hikariDataSource.isClosed()){
            DatenbankZugang.initDbZugang();
        }
        return hikariDataSource.getConnection();
    }
}
