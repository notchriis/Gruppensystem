package com.example.gruppensystem.Datenbank;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatenbankZugangTest {

    @Test
    void datenBankZugangFunktioniert() throws SQLException {
        DatenbankZugang.initDbZugang();
        Connection con = DatenbankZugang.getConnection();
        assertTrue(con.isValid(2));
        con.close();
        DatenbankZugang.schliesseDbZugang();
    }
}