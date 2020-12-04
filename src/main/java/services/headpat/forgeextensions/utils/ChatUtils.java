package services.headpat.forgeextensions.utils;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import services.headpat.forgeextensions.ColorCode;

public class ChatUtils {
	/**
	 * @param str The original string using `&` color codes.
	 * @return The string with all color codes using `&` convert to `§`.
	 */
	@Contract(pure = true)
	public static @NotNull String covertColorCodes(@NotNull String str) {
		return str.replaceAll("&(?=[0-9a-fkl-mr])", "§");
	}

	/**
	 * Wraps text that will displayed in a lore to the best of its ability.
	 *
	 * @param lore          The full length lore with no breaks.
	 * @param lineLength    The maximum length for a single line.
	 * @param loreColorCode Color code to make the lore
	 * @return The text-wrapped lore NBT Tag.
	 */
	public static @NotNull NBTTagList wrapLore(@NotNull String lore, int lineLength, ColorCode loreColorCode) {
		String[] words = lore.split(" ");

		NBTTagList nbtLore = new NBTTagList();
		StringBuilder builder = new StringBuilder();
		for (String word : words) {
			if (builder.length() == 0 || ((builder.length() + 1 + word.length()) <= lineLength)) {
				if (builder.length() > 0) {
					builder.append(' ');
				}
			} else {
				nbtLore.appendTag(new NBTTagString(loreColorCode.toString() + ColorCode.ITALIC.getColorCodeString() + builder.toString()));
				builder.setLength(0);
			}
			builder.append(word);
		}
		if (builder.length() != 0) {
			nbtLore.appendTag(new NBTTagString(loreColorCode.toString() + ColorCode.ITALIC.getColorCodeString() + builder.toString()));
		}

		return nbtLore;
	}

	/**
	 * Wraps text that will displayed in a lore to the best of its ability.
	 *
	 * @param lore The full length lore with no breaks.
	 * @return The text-wrapped lore.
	 */
	public static @NotNull NBTTagList wrapLore(String lore) {
		return (ChatUtils.wrapLore(lore, 25, ColorCode.DARK_PURPLE));
	}

	/**
	 * Wraps text that will displayed in a lore to the best of its ability.
	 *
	 * @param lore          The full length lore with no breaks.
	 * @param loreColorCode The color of the lore.
	 * @return The text-wrapped lore.
	 */
	public static @NotNull NBTTagList wrapLore(String lore, ColorCode loreColorCode) {
		return (ChatUtils.wrapLore(lore, 25, loreColorCode));
	}
}
