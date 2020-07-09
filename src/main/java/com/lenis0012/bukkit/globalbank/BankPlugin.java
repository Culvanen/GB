package com.lenis0012.bukkit.globalbank;

import com.lenis0012.bukkit.globalbank.banker.Banker;
import com.lenis0012.bukkit.globalbank.banker.BankerManager;
import com.lenis0012.bukkit.globalbank.util.sorting.Sort;

import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Lenny on 14-7-2014.
 */
public class BankPlugin extends JavaPlugin {
    private BankerManager bankerManager;
    public NPCLib library;
    private Economy economy;
    private Sort sort;

    @Override
    public void onEnable() {
    	
    	this.library = new NPCLib(this);
        
        PluginManager pm = getServer().getPluginManager();

        //Load config
        saveDefaultConfig();

        //Load sort
        this.sort = Sort.valueOf(getConfig().getString("settings.sorting").toUpperCase());

        //Load bankers
        this.bankerManager = new BankerManager(this);
        

        //Vault
        if(getConfig().getBoolean("settings.economy.enabled")) {
            if (pm.isPluginEnabled("Vault")) {
                if (setupEconomy()) {
                    getLogger().info("Hooked with " + economy.getName() + " using Vault.");
                } else {
                    getLogger().warning("Vault was found, but no economy plugins are registered.");
                }
            } else {
                getLogger().info("Vault was not found, economy support will be disabled.");
            }
        }

        //Register command & listeners
        pm.registerEvents(new BankListener(this), this);
        getCommand("globalbank").setExecutor(new BankCommandExecutor(bankerManager));
    }

    @Override
    public void onDisable() {
        //Save all bankers
        bankerManager.save();

        //Kill all bankers
        for(Banker banker : bankerManager.getBankers()) {
            //banker.getNpc().getBukkitEntity().remove();
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            return;
        }

        NPC npc = library.createNPC();
        npc.setLocation(event.getPlayer().getLocation());
        npc.create();
        npc.show(event.getPlayer());
    }
    
    public BankerManager getBankerManager() {
        return bankerManager;
    }

    public Sort getSort() {
        return sort;
    }

    public Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            this.economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    
    public NPCLib getNPCLib() {
        return library;
    }
}
