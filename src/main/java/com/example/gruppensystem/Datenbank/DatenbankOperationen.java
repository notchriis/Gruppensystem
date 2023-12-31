package com.example.gruppensystem.Datenbank;
import com.example.gruppensystem.Gruppe;
import com.example.gruppensystem.SpielerDaten;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;

public class DatenbankOperationen {

    public static void addSpieler(String uuid, String spielerName) throws DatenbankError {
        try(Connection con = DatenbankZugang.getConnection();
            PreparedStatement statementSpieler = con.prepareStatement(
                    "INSERT INTO spieler(spieler_id, ingame_name) VALUES(?, ?);");
            PreparedStatement statementSpiGru = con.prepareStatement(
                    "INSERT INTO spielergruppe(spieler_id, gruppe_id, beitrittsdatum) " +
                            "VALUES(?, 1, NOW());");){
            statementSpieler.setString(1, uuid);
            statementSpieler.setString(2, spielerName);
            statementSpieler.executeUpdate();

            statementSpiGru.setString(1, uuid);
            statementSpiGru.executeUpdate();

        }catch(SQLException e){
            throw new DatenbankError("Konnte keinen Spieler hinzufügen weil: " + e.getMessage(), e.getErrorCode());
        }
    }

    public static HashMap<String, Object> getGruppeSpieler(String uuid) throws DatenbankError{
        HashMap<String, Object> map = new HashMap<String, Object>();

        try(Connection con = DatenbankZugang.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT sg.spieler_id, sg.gruppe_id, " +
                    "sg.beitrittsdatum, sg.austrittsdatum, g.name, g.prefix " +
                    "FROM spielergruppe sg " +
                    "JOIN gruppe g " +
                    "ON sg.gruppe_id = g.gruppe_id " +
                    "WHERE sg.spieler_id = ? " +
                    "ORDER BY sg.beitrittsdatum DESC LIMIT 1;");

            ) {

            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            ResultSetMetaData metaData = result.getMetaData();
            while (result.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String spaltenName = metaData.getColumnName(i);
                    Object spaltenWert = result.getObject(i);
                    map.put(spaltenName, spaltenWert);
                }
            }
        } catch (SQLException e) {
            throw new DatenbankError("Fehler bei der Suche der Gruppe vom Spieler: " + e.getMessage(), e.getErrorCode());
        }
        return map;
    }

    public static HashMap<String, Gruppe> getGruppen(){
        HashMap<String, Gruppe> map = new HashMap<String, Gruppe>();
        try(Connection con = DatenbankZugang.getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT * from gruppe;");
            ResultSet result = statement.executeQuery()){
            while (result.next()) {
                Gruppe gr = new Gruppe(result.getInt("gruppe_id"), result.getString("name"),result.getString("prefix"));
                map.put(result.getString("name"), gr);
            }
        }catch(SQLException e){
            return null;
        }
        return map;
    }

    public static Gruppe getGruppe(String gruppenName){
        Gruppe gr = null;
        HashMap<String, Gruppe> gruppen =  DatenbankOperationen.getGruppen();
        if(gruppen.containsKey(gruppenName)){
            gr = gruppen.get(gruppenName);
        }
        return gr;
    }

    public static void upsertSpielerGruppe(String uuid, int gruppeId, LocalDateTime austrittsdatum) throws DatenbankError{
        try(Connection con = DatenbankZugang.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO spielergruppe(spieler_id, gruppe_id, beitrittsdatum, austrittsdatum) VALUES(?, ?, NOW(), ?) " +
                            "ON DUPLICATE KEY UPDATE beitrittsdatum = NOW(), austrittsdatum = ?");){
            statement.setString(1, uuid);
            statement.setInt(2, gruppeId);
            statement.setTimestamp(3, austrittsdatum == null ? null : Timestamp.valueOf(austrittsdatum));
            statement.setTimestamp(4, austrittsdatum == null ? null : Timestamp.valueOf(austrittsdatum));
            statement.executeUpdate();
        }catch(SQLException e){
            throw new DatenbankError("Fehler beim upsert des Spieler zur Gruppe: ", e.getErrorCode());
        }

    }

    public static void addGruppe(String gruppenName, String prefix){
        try(Connection con = DatenbankZugang.getConnection();
            PreparedStatement statement = con.prepareStatement("INSERT INTO gruppe(name, prefix) VALUES(?, ?);")){
            statement.setString(1, gruppenName);
            statement.setString(2, prefix);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.getStackTrace();
        }
    }
    public static Gruppe getVorherigeGruppe(String uuid, LocalDateTime beitrittsdatum){
        //SELECT spieler_id, spieler_gruppe FROM `spielergruppe` WHERE spieler_id = ? AND beitrittsdatum < ? ORDER BY beitrittsdatum DESC LIMIT 1;
        try(Connection con = DatenbankZugang.getConnection();
            PreparedStatement stm = con.prepareStatement("SELECT gruppe_id FROM spielergruppe " +
                    "WHERE spieler_id = ? AND beitrittsdatum < ? ORDER BY beitrittsdatum DESC LIMIT 1;")){
            stm.setString(1, uuid);
            stm.setString(2, beitrittsdatum.toString());
            ResultSet rs = stm.executeQuery();
            int gruppeId = 0;
            if(rs.next()){
                gruppeId =  rs.getInt("gruppe_id");
                try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM gruppe WHERE gruppe_id = ?")){
                    stmt.setInt(1, gruppeId);
                    ResultSet res = stmt.executeQuery();
                    if(res.next()){
                        return new Gruppe(res.getInt("gruppe_id"), res.getString("name"), res.getString("prefix"));
                    }
                }

            }
        } catch (SQLException e) {
            e.getStackTrace();
        }
        return null;
    }
    public static SpielerDaten getSpieler(String name){
        try(Connection con = DatenbankZugang.getConnection();
            PreparedStatement stm = con.prepareStatement(" SELECT s.spieler_id, s.ingame_name, sg.gruppe_id, " +
                    "sg.beitrittsdatum, sg.austrittsdatum, g.name, g.prefix " +
                    "FROM spieler s " +
                    "JOIN spielergruppe sg " +
                    "ON sg.spieler_id = s.spieler_id " +
                    "JOIN gruppe g " +
                    "ON sg.gruppe_id = g.gruppe_id " +
                    "WHERE s.ingame_name = ? " +
                    "ORDER BY sg.beitrittsdatum DESC " +
                    "LIMIT 1;")){
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();

            if(rs.next()){
                Gruppe gr = new Gruppe(rs.getInt("gruppe_id"), rs.getString("name"), rs.getString("prefix"));
                LocalDateTime beitrittsdatum = (LocalDateTime) rs.getObject("beitrittsdatum");
                LocalDateTime austrittsdatum = (LocalDateTime) rs.getObject("austrittsdatum");

                SpielerDaten sd = new SpielerDaten(rs.getString("spieler_id"), rs.getString("ingame_name"), gr, beitrittsdatum, austrittsdatum);

                return sd;
            }else{
                return null;
            }
        } catch (SQLException e) {
            return null;
        }

    }
}
