package com.example.gruppensystem.Datenbank;
import com.example.gruppensystem.Gruppe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DatenbankOperationenTest {

    @BeforeAll
    public static void setUp(){
        DatenbankZugang.initDbZugang();
    }
    @AfterAll
    public static void cleanUp(){
        DatenbankZugang.schliesseDbZugang();
    }

    @Test
    void addSpielerSchmeisstError() {
        assertThrows(DatenbankError.class, () -> {DatenbankOperationen.addSpieler("Test", "Test");});
    }

    @Test
    void getGruppeGibtRichtigeGruppe() {
        Gruppe gr = DatenbankOperationen.getGruppe("Admin");
        assertEquals(2, gr.getId());
        assertEquals("Admin", gr.getName());
        assertEquals("OP", gr.getPrefix());
    }

    @Test
    void getGruppeSpielerGibtRichtigeMap() throws DatenbankError {
        HashMap<String, Object> map = DatenbankOperationen.getGruppeSpieler("Test");
        assertEquals("Test", map.get("spieler_id"));
        assertEquals(2, map.get("gruppe_id"));
        assertEquals("2023-12-23T00:17:08", map.get("beitrittsdatum").toString());
        assertEquals("Admin", map.get("name").toString());
        assertEquals("OP", map.get("prefix").toString());
        assertNull(map.get("austrittsdatum"));
    }


}