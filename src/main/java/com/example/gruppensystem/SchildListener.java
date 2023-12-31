package com.example.gruppensystem;

import com.example.gruppensystem.Datenbank.DatenbankError;
import com.example.gruppensystem.Datenbank.DatenbankOperationen;
import com.example.gruppensystem.Utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class SchildListener implements Listener {
    private final Gruppensystem main;
    public SchildListener(Gruppensystem main){
        this.main = main;
    }
    @EventHandler
    public void onSchildPlatziert(BlockPlaceEvent e){
        if(e.getPlayer().isOp()){
            if(e.getBlockPlaced().getType() == Material.OAK_SIGN){
                Player spieler = e.getPlayer();
                ItemMeta im = spieler.getInventory().getItemInMainHand().getItemMeta();

                if(im != null && im.getDisplayName().startsWith(ChatColor.AQUA + "Spieler Infos von ")){
                    Location posSchild = e.getBlockPlaced().getLocation();
                    spieler.getWorld().getBlockAt(posSchild).setType(Material.OAK_SIGN);
                    Sign schild = (Sign) spieler.getWorld().getBlockAt(posSchild).getState();
                    SignSide seite = schild.getSide(Side.FRONT);

                    SpielerDaten sd = DatenbankOperationen.getSpieler(im.getDisplayName().split(" ")[3]);
                    seite.setLine(0, sd.getName());
                    seite.setLine(1, sd.getGruppe().getName() + " " + sd.getGruppe().getPrefix());
                    if(sd.getAustrittsdatum() != null){
                        seite.setLine(2, DateUtils.formatierteDauerBisAustritt(sd.getAustrittsdatum()));
                    }
                    schild.setWaxed(true);
                    schild.update();
                }
            }
        }
    }
    @EventHandler
    public void onSchildEditiert(SignChangeEvent e){
        if(e.getPlayer().isOp()){
            Player spieler = e.getPlayer();
            ItemMeta im = spieler.getInventory().getItemInMainHand().getItemMeta();
            if(im != null && im.getDisplayName().startsWith(ChatColor.AQUA + "Spieler Infos von ")){
                e.setCancelled(true);
            }
        }
    }
}
