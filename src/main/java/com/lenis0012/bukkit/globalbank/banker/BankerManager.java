package com.lenis0012.bukkit.globalbank.banker;

import com.lenis0012.bukkit.globalbank.BankPlugin;
import com.lenis0012.bukkit.globalbank.util.BConfig;
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenny on 14-7-2014.
 *
 * Banker storage
 */
public class BankerManager {
    private final Map<String, Banker> bankers = new HashMap<>();
    private final BankPlugin plugin;
    private final NPCLib npcFactory;
    private final BConfig config;
    public NPCLib library;

    public BankerManager(BankPlugin plugin) {
        this.plugin = plugin;
        this.npcFactory = new NPCLib(plugin);
        this.config = new BConfig(new File(plugin.getDataFolder(), "bankers.yml"));
        loadAllBankers();
    }

    private void loadAllBankers() {
        if(config.contains("bankers")) {
            for (String sectionKey : config.getConfigurationSection("bankers").getKeys(false)) {
                ConfigurationSection section = config.getConfigurationSection("bankers." + sectionKey);
                //Banker banker = Banker.loadFromConfig(npcFactory, section);
                //bankers.put(banker.getName(), banker);
            }
        }
    }

    /**
     * Create a new banker
     *
     * @param name Name of bank
     * @param location Location of banker
     * @return Banker
     */
    public Banker createBanker(String name, Location location) {
        FileConfiguration config = plugin.getConfig();
        String tag = config.getString("settings.banker-nametag");
//        String skin = config.getString("settings.banker-skin");

//        NPCProfile profile = new NPCProfile(tag, skin);
        NPC npc = library.createNPC();
        npc.setLocation(location);
        npc.create();
        //npc.show(event.getPlayer());
        Banker banker = new Banker(npc, name);
        bankers.put(name, banker);

        //npc.setEntityCollision(false);
        //npc.setYaw(location.getYaw());
        return banker;
    }

    public void deleteBanker(Banker banker) {
        
        bankers.remove(banker.getName());
    }

    /**
     * Save all bankers
     */
    public void save() {
        for(Banker banker : bankers.values()) {
            banker.save(config);
        }

        config.save();
    }

    /**
     * Get banker by bank name
     *
     * @param name Name of bank
     * @return Banker
     */
    public Banker getBanker(String name) {
        return bankers.get(name);
    }

    public Banker getBanker(NPC npc) {
        for(Banker banker : bankers.values()) {
            if(banker.getNpc().equals(npc)) {
                return banker;
            }
        }

        return null;
    }

    /**
     * Get a collection of all bankers
     *
     * @return All bankers
     */
    public Collection<Banker> getBankers() {
        return bankers.values();
    }

    public NPCLib getNPCFactory() {
        return npcFactory;
    }
}
