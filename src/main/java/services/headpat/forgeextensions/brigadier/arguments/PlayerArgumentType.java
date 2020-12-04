package services.headpat.forgeextensions.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import services.headpat.forgeextensions.utils.PlayerUtils;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PlayerArgumentType implements ArgumentType<EntityPlayerMP> {
	/**
	 * Shortcut to create a new {@link PlayerArgumentType} instance.
	 *
	 * @return {@link PlayerArgumentType} instance.
	 */
	@Contract(value = " -> new", pure = true)
	public static @NotNull
	PlayerArgumentType player() {
		return new PlayerArgumentType();
	}

	/**
	 * Quick shortcut of {@link CommandContext#getArgument(String, Class)} for a player argument.
	 *
	 * @param context Command context.
	 * @param name    Name of the argument.
	 * @return The player specified by the argument name in the command context.
	 */
	public static EntityPlayerMP getPlayer(@NotNull CommandContext<?> context, String name) {
		return context.getArgument(name, EntityPlayerMP.class);
	}

	@Override
	public EntityPlayerMP parse(StringReader reader) throws CommandSyntaxException {
		EntityPlayerMP player = PlayerUtils.getPlayerList().getPlayerByUsername(reader.readUnquotedString());
		if (player == null) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("player not found.");
		} else
			return player;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		PlayerUtils.getPlayerList().getPlayers().forEach(player -> {
			if (player.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
				builder.suggest(player.getName());
		});
		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return PlayerUtils.getPlayerList().getPlayers().stream().map(EntityPlayer::getName).collect(Collectors.toList());
	}
}
