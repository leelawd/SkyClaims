package net.mohron.skyclaims.command;

import com.google.common.collect.Lists;
import net.mohron.skyclaims.Island;
import net.mohron.skyclaims.Permissions;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;


public class CommandInfo {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "display detailed information on your island";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_INFO)
			.description(Text.of("info"))
			.arguments(GenericArguments.optional(GenericArguments.user(Text.of("player"))))
			.executor(new CommandCreate())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().info("Registered command: CommandInfo");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandInfo");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player) && !args.hasAny(Text.of("player"))) {
			throw new CommandException(Text.of(TextColors.RED, "You must supply a player to use this command."));
		} else {
			if (args.hasAny(Text.of("player"))) {
				if (!src.hasPermission(Permissions.COMMAND_INFO_OTHERS))
					throw new CommandPermissionException(Text.of(TextColors.RED, "You do not have permission to use this command!"));
			}
			User user = (!(src instanceof Player)) ? (User) args.getOne(Text.of("player")).get() : (User) src;

			if (!PLUGIN.dataStore.hasIsland(user.getUniqueId()))
				throw new CommandException(Text.of(TextColors.RED, "You do not have an island!"));

			Island island = PLUGIN.dataStore.getIsland(user.getUniqueId());

			List<Text> infoTextList = Lists.newArrayList();
			infoTextList.add(Text.of(
					TextColors.YELLOW, "Owner", TextColors.WHITE, " : ", TextColors.GRAY, island.getOwner(), "\n",
					TextColors.YELLOW, "Size", TextColors.WHITE, " : ", TextColors.GRAY, island.getRadius() * 2, "x", island.getRadius() * 2, "\n",
					TextColors.YELLOW, "Claim", TextColors.WHITE, " : ", TextColors.GRAY, island.getClaim().getID()
			));

			return CommandResult.success();
		}
	}
}