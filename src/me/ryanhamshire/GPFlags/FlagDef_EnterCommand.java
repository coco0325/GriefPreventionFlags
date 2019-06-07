package me.ryanhamshire.GPFlags;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.List;


public class FlagDef_EnterCommand extends PlayerMovementFlagDefinition {

    @Override
    public boolean allowMovement(Player player, Location lastLocation) {
        if (lastLocation == null) return true;
        Location to = player.getLocation();
        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        if (flag == null) return true;

        if (flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        String[] commandLines = flag.parameters.replace("%owner%", playerData.lastClaim.getOwnerName()).replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString()).split(";");
        for (String commandLine : commandLines) {
            GPFlags.AddLogEntry("Entrance command: " + commandLine);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandLine);
        }

        return true;
    }

    public FlagDef_EnterCommand(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        String[] commandLines = flag.parameters.replace("%owner%", playerData.lastClaim.getOwnerName()).replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString()).split(";");

        for (String commandLine : commandLines) {
            GPFlags.AddLogEntry("Entrance command: " + commandLine);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandLine);
        }
    }

    @Override
    String getName() {
        return "EnterCommand";
    }

    @Override
    SetFlagResult ValidateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.ConsoleCommandRequired));
        }

        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.AddedEnterCommand, parameters);
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.RemovedEnterCommand);
    }

}
