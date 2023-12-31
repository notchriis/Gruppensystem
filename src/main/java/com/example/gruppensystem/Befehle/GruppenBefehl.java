package com.example.gruppensystem.Befehle;
import com.example.gruppensystem.Datenbank.DatenbankOperationen;
import com.example.gruppensystem.Gruppe;
import com.example.gruppensystem.Gruppensystem;
import com.example.gruppensystem.SpielerDaten;
import com.example.gruppensystem.Utils.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.time.LocalDateTime;
import java.util.Map;

public class GruppenBefehl implements CommandExecutor {
    private Gruppensystem main;
    public GruppenBefehl(Gruppensystem main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(commandSender instanceof Player){
            Player befehlshaber = (Player) commandSender;
            if(args.length == 1 && args[0].equals("info")){
                SpielerDaten spieler = this.main.getSpielerDaten().get(befehlshaber.getName());
                if(spieler.getAustrittsdatum() != null){
                    String msg = String.format("Du bist ein [%s]%s für %s!", spieler.getGruppe().getPrefix(), spieler.getGruppe().getName(), DateUtils.formatierteDauerBisAustritt(spieler.getAustrittsdatum()));
                    befehlshaber.sendMessage(msg);
                }else{
                    String msg = String.format("Du bist ein [%s]%s!", spieler.getGruppe().getPrefix(), spieler.getGruppe().getName());
                    befehlshaber.sendMessage(msg);
                }
                return false;
            }

            if(befehlshaber.isOp()){
                if(args.length > 2 && args[0].equals("erstellen")){
                    String gruppeName = args[1];
                    String prefix = args[2];
                    if(DatenbankOperationen.getGruppen().containsKey(gruppeName)){
                        befehlshaber.sendMessage("Diese Gruppe gibt es schon!");
                        return false;
                    }
                    DatenbankOperationen.addGruppe(gruppeName, prefix);

                    this.main.getScoreboardManager().updateScoreboard(gruppeName);
                    befehlshaber.sendMessage("Du hast erfolgreich eine neue Gruppe erstellt!");
                    return false;
                }
                if(args.length >= 2 && args.length <= 3){
                    String spielerName = args[0];
                    String gruppenName = args[1];

                    SpielerDaten befoerderter = DatenbankOperationen.getSpieler(spielerName);

                    if(befoerderter != null){

                        //Muss gemacht werden, weil online Spieler nicht gleich offline Spieler sind.
                        if(this.main.getSpielerDaten().containsKey(spielerName)){
                            //Online Spieler
                            befoerderter = this.main.getSpielerDaten().get(spielerName);
                        }else{
                            //Offline Spieler
                            //Muss gemacht werden damit der Task der für temporäre Gruppen zuständig ist funktioniert.
                            befoerderter.setMain(this.main.getSpielerDaten().get(befehlshaber.getName()).getMain());
                        }

                        LocalDateTime austrittsdatum = null;
                        if(args.length == 3){
                            String dauer = args[2];
                            if(DateUtils.dauerIstRichtigFormatiert(dauer)){
                                austrittsdatum = DateUtils.rechneAustrittsdatum(dauer);
                            }else{
                                befehlshaber.sendMessage("Falsche Benutzung! Die Dauer muss in dem Format: Tage:Stunden:Minuten:Sekunden angegeben werden!");
                                return false;
                            }
                        }

                        Gruppe neueGr = DatenbankOperationen.getGruppe(gruppenName);
                        if(neueGr == null){
                            befehlshaber.sendMessage("Diese Gruppe gibt es nicht, du kannst aber durch einen Befehl eine Gruppe erstellen.");
                            return false;
                        }

                        if(befoerderter.istBereitsInGruppe(neueGr, austrittsdatum)){
                            befehlshaber.sendMessage("Der Spieler ist bereits in der Gruppe " + neueGr.getName());
                            return false;
                        }

                        if(austrittsdatum != null){
                            String msg = String.format("Du hast %s zum %s für %s gemacht!",befoerderter.getName(), neueGr.getName(), DateUtils.formatierteDauerBisAustritt(austrittsdatum));
                            befehlshaber.sendMessage(msg);
                        }else{
                            befehlshaber.sendMessage("Du hast " + befoerderter.getName() +" zum " + neueGr.getName() +" gemacht!");
                        }

                        befoerderter.setGruppe(neueGr, austrittsdatum);
                    }else{
                        befehlshaber.sendMessage("Dieser Spieler hat noch nicht auf diesem Server gespielt!");
                    }
                }else{
                    befehlshaber.sendMessage("Falsche Benutzung! /gruppe <Spielername> <Gruppe> <Dauer im Format: Tage:Stunden:Minuten:Sekunden>");
                }
            }else{
                befehlshaber.sendMessage(ChatColor.DARK_RED + "Du musst OP sein!");
            }
        }

        return false;
    }

}

