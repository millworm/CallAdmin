package com.calladmin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class mainListener extends JavaPlugin implements Listener {
	FileConfiguration config;

	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean adminOnline = false;
		if (cmd.getName().equalsIgnoreCase("calladmin")) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.getName().equalsIgnoreCase("admin")) {
					adminOnline = true;
					break;
				}
			}
			if (!adminOnline) {
				URL url;
				String hash = config.getString("hash");
				List<String> id = config.getStringList("id");
				String serverName = config.getString("server_name");
				try {
					if (args.length == 0) {
						for (String p : id) {
							url = new URL("https://api.telegram.org/bot" + hash + "/sendMessage?chat_id=" + p + "&text="
									+ "[" + serverName + "] " + sender.getName()
									+ URLEncoder.encode(" зовет админа", "UTF-8"));
							URLConnection conn = url.openConnection();
							conn.getInputStream();
						}
					} else {
						if (args.length == 1 && args[0].equalsIgnoreCase("Reload")) {

							if (sender instanceof Player) {
								Player player = (Player) sender;
								if (player.hasPermission("bukkit.command.reload")) {
									this.reloadConfig();
									player.sendMessage(ChatColor.GREEN + "[CallAdmin] Config reloaded!");
									System.out.println("[CallAdmin] Config reloaded!");
									return true;
								} else
									return false;
							} else {
								this.reloadConfig();
								Bukkit.getLogger().info("[CallAdmin] Config Reloaded! ");
								return true;
							}

						} else {
							String s = new String();
							for (int i = 0; i < args.length; i++)
								s += args[i] + " ";
							for (String p : id) {
								url = new URL("https://api.telegram.org/bot" + hash + "/sendMessage?chat_id=" + p
										+ "&text=" + "[" + serverName + "] " + sender.getName()
										+ URLEncoder.encode(" зовет админа:", "UTF-8") + URLEncoder.encode(s, "UTF-8"));
								URLConnection conn = url.openConnection();
								conn.getInputStream();
							}
						}

					}

					sender.sendMessage("Почтовый голубь отправлен");
					return true;
				} catch (MalformedURLException e) {
					sender.sendMessage("Ошибка");
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					sender.sendMessage("Ошибка");
					return false;
				}
			} else {
				sender.sendMessage("Админ на сервере");
				return true;
			}
		}

		else {
			sender.sendMessage("Ошибка");
			return false;
		}
	}

	@Override
	public void onEnable() {
		createConfig();
	}

	private void createConfig() {
		try {
			if (!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			File file = new File(getDataFolder(), "config.yml");
			if (!file.exists()) {
				getLogger().info("Config.yml not found, creating!");

				this.getConfig().set("hash", "bot api hash");
				this.getConfig().set("id", new String[] { "id1" });
				this.getConfig().set("server_name", "Server name");
				this.saveConfig();

			} else {
				getLogger().info("Config.yml found, loading!");
				config = getConfig();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
