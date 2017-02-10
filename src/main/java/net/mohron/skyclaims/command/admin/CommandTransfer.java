/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.command.admin;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandTransfer implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static final String HELP_TEXT = "used to transfer island ownership to another player.";

	public static final Text OWNER = Text.of("owner");
	public static final Text USER = Text.of("user");

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_TRANSFER)
			.description(Text.of(HELP_TEXT))
			.arguments(GenericArguments.seq(
					GenericArguments.optional(GenericArguments.user(OWNER)),
					GenericArguments.user(USER)
			))
			.executor(new CommandTransfer())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandTransfer");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandTransfer");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player) && !args.hasAny(OWNER))
			throw new CommandException(Text.of(TextColors.RED, "You must supply a owner & user to use this command."));

		User owner = args.<User>getOne(OWNER).orElse(null);
		User user = args.<User>getOne(USER).orElse(null);
		//if (user == null || Island.hasIsland(user.getUniqueId()))
		//	throw new CommandException(Text.of(TextColors.RED, "Unable to complete island transfer to ", user.getName(), ": player has reached the max islands owned!"));

		Player player = (src instanceof Player) ? (Player) src : null;
		Island island = (player != null) ?
				Island.getByOwner(owner.getUniqueId()).orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "The owner supplied must have an island!"))) :
				Island.get(player.getLocation()).orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "This command must be run on the island you wish to transfer!")));

		src.sendMessage(Text.of(TextColors.GREEN, "Completed transfer of ", TextColors.GOLD, island.getOwnerName(), TextColors.GREEN, "'s island to ", TextColors.GOLD, user.getName(), TextColors.GREEN, "."));
		island.transfer(user);

		return CommandResult.success();
	}
}