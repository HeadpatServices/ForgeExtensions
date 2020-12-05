package services.headpat.forgeextensions.utils;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerUtils {
	/**
	 * Quick shortcut. Works on both server and client.
	 *
	 * @return returns Server list players.
	 */
	@NotNull
	public static PlayerList getPlayerList() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
	}

	/**
	 * Gets nearby players only, removing the time to find regular entities. Server side only!
	 *
	 * @param world         World of location.
	 * @param location      Location of where to search.
	 * @param radius        Radius to search for players.
	 * @param sortByClosest Whether to sort Set by proximity.
	 * @return Players near the specified location.
	 */
	public static @Nullable LinkedHashSet<EntityPlayerMP> getNearbyPlayers(World world, BlockPos location, double radius, boolean sortByClosest) {
		if (FMLCommonHandler.instance().getSide().isClient() || FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer())
			return null;

		Stream<? extends EntityPlayerMP> stream = PlayerUtils.getPlayerList().getPlayers().stream().filter((player) -> {
			if (!player.world.equals(world)) {
				return (false);
			}
			return (location.distanceSq(location) <= (radius * radius));
		});
		if (sortByClosest) {
			stream = stream.sorted(Comparator.comparingDouble(p -> p.getPosition().distanceSq(location)));
		}
		return (stream.collect(Collectors.toCollection(LinkedHashSet::new)));
	}
}
