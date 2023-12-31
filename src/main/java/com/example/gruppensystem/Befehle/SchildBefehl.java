package com.example.gruppensystem.Befehle;

import com.example.gruppensystem.Datenbank.DatenbankOperationen;
import com.example.gruppensystem.Gruppensystem;
import com.example.gruppensystem.SpielerDaten;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SchildBefehl implements CommandExecutor{
    private Gruppensystem main;
    public SchildBefehl(Gruppensystem main){
        this.main = main;
    }
    private ItemStack getSchild(String spielerName){
        ItemStack schild = new ItemStack(Material.OAK_SIGN, 1);
        ItemMeta im = schild.getItemMeta();
        String displayName = ChatColor.AQUA + "Spieler Infos von " + spielerName;
        im.setDisplayName(displayName);
        schild.setItemMeta(im);
        return schild;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){

            Player befehlshaber = (Player) commandSender;
            if(befehlshaber.isOp()){

                if(args.length == 1){
                    Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> {
                        SpielerDaten sd = DatenbankOperationen.getSpieler(args[0]);

                        if(sd != null){
                            befehlshaber.getInventory().setItem(0, getSchild(args[0]));
                        }else{
                            befehlshaber.sendMessage("Diesen Spieler gibt es nicht");

                        }
                    });
                    return false;
                }else{
                    befehlshaber.sendMessage("Falsche Benutzung! /schild <Spielername>");
                }
            }

        }
        return true;
    }
}
