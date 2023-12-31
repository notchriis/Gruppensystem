package com.example.gruppensystem;

import com.example.gruppensystem.Datenbank.DatenbankOperationen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScoreboardManager {

    private Gruppensystem main;

    public ScoreboardManager(Gruppensystem main){
        this.main = main;
    }

    public void initScoreboard(SpielerDaten p){

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> {


            for(Map.Entry<String, Gruppe> gruppe : DatenbankOperationen.getGruppen().entrySet()){
                Gruppe g = gruppe.getValue();

                Team tGruppe = board.registerNewTeam(g.getName());
                tGruppe.setPrefix(g.getName());
            }
            board.getTeam(p.getGruppe().getName()).addEntry(p.getName());
            p.getPlayer().setScoreboard(board);
            this.updateScoreboard("");
        });

    }

    public void updateScoreboard(String neueGruppeName){
        Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> {
            //Wenn eine neue Gruppe erstellt wurde, muss diese jedem Spieler im Scoreaboard hinzugefügt werden
            if(!neueGruppeName.isEmpty()){
                for(Map.Entry<String, SpielerDaten> spieler : main.getSpielerDaten().entrySet()){
                    SpielerDaten sp = spieler.getValue();
                    sp.getPlayer().getScoreboard().registerNewTeam(neueGruppeName).setPrefix(neueGruppeName);
                }
            }

            for(Player target : Bukkit.getOnlinePlayers()){
                for(Player t : Bukkit.getOnlinePlayers()){
                    target.getScoreboard().getTeam(main.getSpielerDaten().get(t.getName()).getGruppe().getName()).addEntry(t.getName());
                }
            }
        });


    }

}
