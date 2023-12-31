package com.example.gruppensystem;
import com.example.gruppensystem.Datenbank.DatenbankError;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;

public class DatenbankOLD {
    private final String url;
    private final String benutzer;
    private final String passwort;
    private Connection con;

    public DatenbankOLD(String url, String benutzer, String passwort){
        this.url = url;
        this.benutzer = benutzer;
        this.passwort = passwort;
        this.con = null;
    }

    public Boolean istVerbunden(){
        return (this.con != null);
    }

    public void verbinden() throws DatenbankError {
        try{
            this.con = DriverManager.getConnection(url, benutzer, passwort);
        }catch(SQLException e){
            this.con = null;
            throw new DatenbankError("Konnte nicht zur Datenbank verbinden weil: " + e.getMessage(), e.getErrorCode());
        }

    }

    public Connection getVerbindung(){
        return this.con;
    }
    public void schliessen(){
        try{
            if(this.con != null){
                this.con.close();
                this.con = null;
            }
        }catch(SQLException e){
            e.getStackTrace();
        }
    }

    public void addSpieler(String uuid, String spielerName) throws DatenbankError{
        if(!this.istVerbunden()){
            this.verbinden();
        }
        try{
            PreparedStatement statementSpieler = con.prepareStatement("INSERT INTO spieler(spieler_id, ingame_name) VALUES(?, ?);");
            statementSpieler.setString(1, uuid);
            statementSpieler.setString(2, spielerName);
            statementSpieler.executeUpdate();

            PreparedStatement statementSpiGru = con.prepareStatement("INSERT INTO spielergruppe(spieler_id, gruppe_id, beitrittsdatum) VALUES(?, 1, NOW());");
            statementSpiGru.setString(1, uuid);
            statementSpiGru.executeUpdate();

        }catch(SQLException e){
            this.schliessen();
            throw new DatenbankError("Konnte keinen Spieler hinzufügen weil: " + e.getMessage(), e.getErrorCode());
        }
        this.schliessen();
    }

    public HashMap<String, Object> getGruppeSpieler(String uuid) throws DatenbankError{
        if(!this.istVerbunden()){
            this.verbinden();
        }
        HashMap<String, Object> map = new HashMap<String, Object>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT sg.spieler_id, sg.gruppe_id, sg.beitrittsdatum, sg.austrittsdatum, g.name, g.prefix " +
                                                                    "FROM spielergruppe sg " +
                                                                    "JOIN gruppe g " +
                                                                    "ON sg.gruppe_id = g.gruppe_id " +
                                                                    "WHERE sg.spieler_id = ? " +
                                                                    "ORDER BY sg.beitrittsdatum DESC LIMIT 1;");
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
            this.schliessen();
            throw new DatenbankError("Fehler bei der Suche der Gruppe vom Spieler: " + e.getMessage(), e.getErrorCode());
        }
        this.schliessen();
        return map;
    }

    public HashMap<String, Gruppe> getGruppen() throws DatenbankError{
        if(!this.istVerbunden()){
            this.verbinden();
        }
        HashMap<String, Gruppe> map = new HashMap<String, Gruppe>();
        try{
            PreparedStatement statement = this.con.prepareStatement("SELECT * from gruppe;");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Gruppe gr = new Gruppe(result.getInt("gruppe_id"), result.getString("name"),result.getString("prefix"));
                map.put(result.getString("name"), gr);
            }

        }catch(SQLException e){
            this.schliessen();
            throw new DatenbankError("Fehler beim Laden der Gruppen", e.getErrorCode());
        }
        this.schliessen();
        return map;
    }

    public Gruppe getGruppe(String gruppenName){
        Gruppe gr = null;
        try {
            HashMap<String, Gruppe> gruppen =  this.getGruppen();
            if(gruppen.containsKey(gruppenName)){
                gr = gruppen.get(gruppenName);
            }
        } catch (DatenbankError e) {
            return gr;
        }
        return gr;
    }
    public void fuegeSpielerZurGruppe(String uuid, int gruppeId, LocalDateTime austrittsdatum) throws DatenbankError{
        if(!this.istVerbunden()){
            this.verbinden();
        }
        try{
            PreparedStatement statement = con.prepareStatement("SELECT * FROM spielergruppe WHERE spieler_id = ? AND gruppe_id = ?;");
            statement.setString(1, uuid);
            statement.setInt(2, gruppeId);
            ResultSet set = statement.executeQuery();
            //Der Spieler war bereits in der Gruppe drin --> update beitrittsdatum und austrittsdatum ggbf.
            if(set.next()){
                PreparedStatement stmUpdate = con.prepareStatement("UPDATE spielergruppe SET beitrittsdatum = NOW(), austrittsdatum = ? WHERE spieler_id = ? AND gruppe_id = ?;");
                stmUpdate.setTimestamp(1, austrittsdatum == null ? null : Timestamp.valueOf(austrittsdatum));
                stmUpdate.setString(2, uuid);
                stmUpdate.setInt(3, gruppeId);
                stmUpdate.executeUpdate();
            }else{
                PreparedStatement statementSpiGru = con.prepareStatement("INSERT INTO spielergruppe(spieler_id, gruppe_id, beitrittsdatum) VALUES(?, ?, NOW());");
                statementSpiGru.setString(1,uuid);
                statementSpiGru.setInt(2,gruppeId);
                statementSpiGru.executeUpdate();

            }
            set.close();

        }catch(SQLException e){
            this.schliessen();
            throw new DatenbankError("Fehler beim hinzufügen des Spieler zur Gruppe: " , e.getErrorCode());
        }
        this.schliessen();
    }
}

