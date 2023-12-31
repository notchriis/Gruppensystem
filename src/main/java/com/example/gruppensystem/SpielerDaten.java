package com.example.gruppensystem;
import com.example.gruppensystem.Datenbank.DatenbankError;
import com.example.gruppensystem.Datenbank.DatenbankOperationen;
import com.example.gruppensystem.Utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SpielerDaten {
    private String uuid;
    private String spielerName;
    private Gruppe gruppe;
    private LocalDateTime beitrittsdatum;
    private LocalDateTime austrittsdatum;
    private final Player p;
    private Gruppensystem main;
    private final boolean online;

    private void initTask(){
        BukkitScheduler bs = Bukkit.getScheduler();

        LocalDateTime vorherigesBeitrittsDatum = this.beitrittsdatum;//Sonst wird ein falsches Beitrittsdatum benutzt.
        bs.runTaskLaterAsynchronously(this.main,
                () -> {
            Gruppe vorherigeGruppe = DatenbankOperationen.getVorherigeGruppe(this.uuid,vorherigesBeitrittsDatum);
            this.setGruppe(vorherigeGruppe, null);
            },20L*DateUtils.dauerZwischen(LocalDateTime.now(), this.austrittsdatum));
    }
    private void spielerJoint(){
        try{
            HashMap<String, Object> resultat = DatenbankOperationen.getGruppeSpieler(this.uuid);
            if(!resultat.isEmpty()){
                this.gruppe = new Gruppe((int)resultat.get("gruppe_id"), (String)resultat.get("name"), (String)resultat.get("prefix"));
                this.beitrittsdatum = (LocalDateTime)resultat.get("beitrittsdatum");
                this.austrittsdatum = (LocalDateTime)resultat.get("austrittsdatum");
                if(austrittsdatum != null){//Falls der Spieler tempörar in einer Gruppe ist.
                    initTask();
                    String msg = String.format("Hi %s du bist ein [%s]%s für %s!", this.spielerName, this.gruppe.getPrefix(), this.gruppe.getName(), DateUtils.formatierteDauerBisAustritt(austrittsdatum));
                    this.p.sendMessage(msg);
                }else{
                    String msg = String.format("Hi %s du bist ein [%s]%s!", this.spielerName, this.gruppe.getPrefix(), this.gruppe.getName());
                    this.p.sendMessage(msg);
                }

            }else{
                //Wenn ein Spieler das erste mal joint.
                DatenbankOperationen.addSpieler(this.uuid, this.spielerName);
                this.gruppe = new Gruppe(1, "Besucher", "Bes");
                this.beitrittsdatum = LocalDateTime.now();
                this.austrittsdatum = null;
                this.p.sendMessage("Willkommen ["+this.gruppe.getName()+"]" + this.spielerName +"!");
            }

            this.main.getSpielerDaten().put(this.spielerName, this);
            this.main.getScoreboardManager().initScoreboard(this);

        }catch(DatenbankError e) {
            this.p.kickPlayer("Die Verbindung zur Datenbank ist fehlgeschlagen!");
        }
    }
    public SpielerDaten(Gruppensystem main, Player spieler){
        this.p = spieler;
        this.main = main;
        this.uuid = this.p.getUniqueId().toString();
        this.spielerName = this.p.getName();
        this.online = true;
        spielerJoint();
    }

    public SpielerDaten(String uuid, String spielerName, Gruppe gr, LocalDateTime beitrittsdatum, LocalDateTime austrittsdatum){
        this.spielerName = spielerName;
        this.uuid = uuid;
        this.gruppe = gr;
        this.beitrittsdatum = beitrittsdatum;
        this.austrittsdatum = austrittsdatum;
        this.online = false;
        this.main = null;
        this.p = null;
    }
    public void setGruppe(Gruppe gr, LocalDateTime austrittsdatum){
        try{
            DatenbankOperationen.upsertSpielerGruppe(this.uuid, gr.getId(), austrittsdatum);

            if(this.istOnline()){
                this.gruppe = gr;
                this.austrittsdatum = austrittsdatum;

                if(this.austrittsdatum != null){
                    System.out.println(gr.getName() +" "+ austrittsdatum);
                    initTask();
                    String msg = String.format("Du bist nun ein [%s]%s für %s!", this.gruppe.getPrefix(), this.gruppe.getName(), DateUtils.formatierteDauerBisAustritt(austrittsdatum));
                    this.p.sendMessage(msg);
                }else{
                    p.sendMessage("Du bist nun ein " + gr.getName() + "!");
                }

                this.beitrittsdatum = LocalDateTime.now();
                this.main.getScoreboardManager().updateScoreboard("");
            }else{
                if(this.austrittsdatum != null){
                    initTask();
                }
            }

        }catch(DatenbankError e){
            for(Map.Entry<String, SpielerDaten> spieler : main.getSpielerDaten().entrySet()){
                spieler.getValue().getPlayer().kickPlayer("Achtung! Die Verbindung zur Datenbank ist fehlgeschlagen!");
            }
        }
    }


    public Gruppe getGruppe(){
        return this.gruppe;
    }

    public LocalDateTime getBeitrittsdatum(){
        return this.beitrittsdatum;
    }

    public LocalDateTime getAustrittsdatum(){
        return this.austrittsdatum;
    }

    public Player getPlayer(){return this.p;}

    public boolean istBereitsInGruppe(Gruppe neueGruppe, LocalDateTime austrittsdatum){

        return (this.getGruppe().equals(neueGruppe) && (DateUtils.dauerZwischen(this.getAustrittsdatum(), austrittsdatum) < 5));
    }

    public String getName(){
        return this.spielerName;
    }

    public Gruppensystem getMain(){
        return this.main;
    }

    public void setMain(Gruppensystem main){
        this.main = main;
    }

    public boolean istOnline(){
        return this.online;
    }

}
