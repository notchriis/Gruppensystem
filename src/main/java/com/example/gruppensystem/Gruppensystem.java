package com.example.gruppensystem;

import com.example.gruppensystem.Befehle.GruppenBefehl;
import com.example.gruppensystem.Befehle.SchildBefehl;
import com.example.gruppensystem.Datenbank.DatenbankZugang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class Gruppensystem extends JavaPlugin implements Listener {

    private HashMap<String, SpielerDaten> spielerDaten;
    private ScoreboardManager scoreBoardManager;

    @Override
    public void onEnable() {
        DatenbankZugang.initDbZugang();
        this.spielerDaten = new HashMap<String, SpielerDaten>();
        this.scoreBoardManager = new ScoreboardManager(this);
        getCommand("gruppe").setExecutor(new GruppenBefehl(this));
        getCommand("schild").setExecutor(new SchildBefehl(this));
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new SchildListener(this), this);
    }
    @Override
    public void onDisable() {
        DatenbankZugang.schliesseDbZugang();

        Bukkit.getScheduler().getPendingTasks().clear();
        this.spielerDaten = null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        SpielerDaten spieler = new SpielerDaten(this, p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        this.spielerDaten.remove(p.getName());
    }

    public HashMap<String, SpielerDaten> getSpielerDaten(){
        return this.spielerDaten;
    }

    public ScoreboardManager getScoreboardManager(){
        return this.scoreBoardManager;
    }
}
