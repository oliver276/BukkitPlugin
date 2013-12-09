package me.oliver276.server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class SMain extends JavaPlugin implements Listener {
    ArrayList<String> Penabled = new ArrayList<String>();
    Player pla;

    public BufferedImage scaleImage(int WIDTH, int HEIGHT, String filename) {
        BufferedImage bi = null;
        try {
            ImageIcon ii = new ImageIcon(filename);//path to image
            bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) bi.createGraphics();
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY));
            g2d.drawImage(ii.getImage(), 0, 0, WIDTH, HEIGHT, null);
            g2d.drawImage(bi,null,64,64);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bi;
    }




    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String ip = e.getPlayer().getAddress().getAddress().getHostAddress();
        String ipvv = ip.toString();
        String ipv = ipvv.replace(".", ",");
        String pl = e.getPlayer().getDisplayName();
        getConfig().set("IP" + ipv,pl);
        Player p = e.getPlayer();
        String motd = getConfig().getString("motd.ingame");
        motd = motd.replaceAll("&", "§");
        p.sendMessage(motd);
        saveConfig();
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent e) {
        String add = e.getAddress().getHostAddress();
        String ipvv = add.replace(".",",") ;
        String ipv = "IP" + ipvv;

        if (getConfig().contains(ipv)) {
            String cfg = getConfig().getString(ipv);
            for (String pl : Penabled){
                try{
                    pla= Bukkit.getPlayer(pl);
                }catch(Exception ex){

                }
                if (!(pla == null)){
                    pla.sendMessage(ChatColor.GOLD + "Someone has this server on their multiplayer list and just pinged it from " + cfg + ChatColor.GOLD + "'s IP!");
                }
            }

            String motd = getConfig().getString("motd.systembefore");
            motd = motd.replaceAll("&", "\u00A7");
            if (motd.contains("%player%")){
                motd = motd.replaceAll("%player%",cfg);
            }
            e.setMotd(motd);
            BufferedImage img = null;
            if (getConfig().getBoolean("Image.faces")){
                try {
                    String thi = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                    String str = thi.substring(0, thi.lastIndexOf('/'));
                    if (new File(str + "/MOTD/faces/" + cfg + ".png").exists()){
                        if (new ImageIcon(str + "/MOTD/faces/" + cfg + ".png").getImage().getWidth(null) == 64) {

                            img = ImageIO.read(new File(str + "/MOTD/faces/" + cfg + ".png"));

                            e.setServerIcon(getServer().loadServerIcon(img));
                        }else{
                            Bukkit.getLogger().warning(ChatColor.DARK_RED + cfg + "'s face was not a 64x64 image!");
                        }
                    }else{
                        URL url = new URL("https://minotar.net/avatar/"+ cfg + "/64.png");
                        Image im = ImageIO.read(url);
                        InputStream in = new BufferedInputStream(url.openStream());
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        int n = 0;
                        while (-1!=(n=in.read(buf)))
                        {
                            out.write(buf, 0, n);
                        }
                        out.close();
                        in.close();
                        byte[] response = out.toByteArray();
                        FileOutputStream fos = new FileOutputStream(str + "/MOTD/faces/" + cfg + ".png");
                        fos.write(response);
                        fos.close();
                        img = ImageIO.read(new File(str + "/MOTD/faces/" + cfg + ".png"));

                        e.setServerIcon(getServer().loadServerIcon(img));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }




            return;
        }

        BufferedImage img = null;
        try {
            String thi = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String str = thi.substring(0, thi.lastIndexOf('/'));
            if (new File(str + "/MOTD/image.png").exists()){
                if (new ImageIcon(str + "/MOTD/image.png").getImage().getWidth(null) == 64 && new ImageIcon(str + "/MOTD/image.png").getImage().getHeight(null) == 64) {

                    img = ImageIO.read(new File(str + "/MOTD/image.png"));

                    e.setServerIcon(getServer().loadServerIcon(img));
                }else{
                    Bukkit.getLogger().warning(ChatColor.DARK_RED + "The image selected was not a 64x64 image!");
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }





        for (String pl : Penabled){
            try{
                pla= Bukkit.getPlayer(pl);
            }catch(Exception ex){

            }
            if (!(pla == null)) {
                pla.sendMessage(ChatColor.GREEN + "Someone new has just pinged this server!");
            }
        }
        String motd = getConfig().getString("motd.system");
        motd = motd.replaceAll("&", "\u00A7");
        e.setMotd(motd);
        return;

    }

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        if (getConfig().getBoolean("AutoUpdate.Enabled")){
            Updater updater = new Updater(this, 58416, this.getFile(), Updater.UpdateType.DEFAULT, true);
        }
        Bukkit.getServer().getLogger().info("ColorMOTD enabled!");
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (cmd.getName().equalsIgnoreCase("setserverimage")){
            if (!(sender.hasPermission("MOTD.setserveriage"))){
                sender.sendMessage(ChatColor.DARK_RED + "You do not have access to this command!");
                return true;
            }
            if (args.length != 1){
                sender.sendMessage(ChatColor.DARK_RED + "You need to enter 1 URL of an image!");
                return true;
            }

            try{

                URL url = new URL(args[0]);
                Image im = ImageIO.read(url);
                if (im.getHeight(null) != 64 || im.getWidth(null) != 64){
                    sender.sendMessage(ChatColor.RED + "That image was not 64x64 pixels - Changing it to 64x64");
                }
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1!=(n=in.read(buf)))
                {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();
                byte[] response = out.toByteArray();
                String thi = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                String str = thi.substring(0, thi.lastIndexOf('/'));
                FileOutputStream fos = new FileOutputStream(str + "/MOTD/image.png");
                fos.write(response);
                fos.close();
                BufferedImage bufim= scaleImage(64,64,str + "/MOTD/image.png");
                File outputfile = new File("saved.png");
                ImageIO.write(bufim, "png", outputfile);

            }catch(Exception ex){
                sender.sendMessage(ChatColor.DARK_RED + "Oops, something went wrong!  Make sure you've got a valid image URL!");

                return true;
            }
            reloadConfig();
            saveConfig();

            sender.sendMessage(ChatColor.GREEN + "Image saved");

        }

        if (cmd.getName().equalsIgnoreCase("toggleping")){
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Sorry, the console cannot use this!");
            }
            Player p = (Player) sender;
            if (Penabled.contains(p.getName())){
                Penabled.remove(p.getName());
                p.sendMessage(ChatColor.GOLD + "Toggled " + ChatColor.RED + "OFF");
                return true;
            }
            if (!(Penabled.contains(p.getName()))){
                Penabled.add(p.getName());
                p.sendMessage(ChatColor.GOLD + "Toggled " + ChatColor.GREEN + "ON");
            }
        }
        if (cmd.getName().equalsIgnoreCase("motd")) {
            if (!sender.hasPermission("motd.check")) {
                sender.sendMessage(ChatColor.RED + "You are not permitted to do this!");
                return true;
            }
            String motd = getConfig().getString("motd.ingame");
            motd = motd.replaceAll("&", "§");
            String system = getConfig().getString("motd.system");
            system = system.replaceAll("&", "§");
            String before = getConfig().getString("motd.systembefore");
            before = before.replaceAll("&", "§");
            sender.sendMessage(ChatColor.GREEN + "In-Game MOTD: " + motd);
            sender.sendMessage(ChatColor.GREEN + "System MOTD: " + system);
            sender.sendMessage(ChatColor.GREEN + "The system MOTD for a previous player: " + before);
            return true;
        }


        if (cmd.getName().equalsIgnoreCase("setmotd")) {
            if (!sender.hasPermission("motd.set")) {
                sender.sendMessage(ChatColor.RED + "You are not permitted to do this!");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Please specify a message!");
                return true;
            }
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i] + " ");
            }
            String motd = str.toString();
            getConfig().set("motd.ingame", motd);
            saveConfig();
            String newmotd = getConfig().getString("motd.ingame");
            motd = motd.replaceAll("&", "§");
            sender.sendMessage(ChatColor.GREEN + "MOTD set to: " + newmotd);
            sender.sendMessage(ChatColor.GREEN + "And will show as: " + motd);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("setsystemmotd")) {
            if (!sender.hasPermission("motd.setsystem")) {

                sender.sendMessage(ChatColor.RED + "You are not permitted to do this!");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Please specify a message!");
                return true;
            }
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i] + " ");
            }
            String motd = str.toString();
            getConfig().set("motd.system", motd);
            saveConfig();
            String system = getConfig().getString("motd.system");
            system = system.replaceAll("&", "§");
            sender.sendMessage(ChatColor.GREEN + "MOTD set to: " + system);

            Player p = (Player) sender;
            if (p.getInventory().contains(new ItemStack(Material.MAP))){

            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("getpingplayer")){
            StringBuilder str = new StringBuilder();

            for (String pl:Penabled){
                str.append(pl + ", ");
            }
            sender.sendMessage(str.toString());
        }

        if (cmd.getName().equalsIgnoreCase("setbeforesystemmotd")) {
            if (!sender.hasPermission("motd.setsystemafter")) {

                sender.sendMessage(ChatColor.RED + "You are not permitted to do this!");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Please specify a message!");
                return true;
            }
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i] + " ");
            }
            String motd = str.toString();
            getConfig().set("motd.systembefore", motd);
            saveConfig();
            String system = getConfig().getString("motd.systembefore");
            system = system.replaceAll("&", "§");
            sender.sendMessage(ChatColor.GREEN + "MOTD set to: " + system);
            return true;
        }
        return true;
    }

}
