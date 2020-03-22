/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package xyz.xy718.poster.util;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import xyz.xy718.poster.config.I18N;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.InventoryTransformations;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.translation.Translatable;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nullable;

public class Util {

    private Util() {
    }

    public static final DateTimeFormatter FULL_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
            .withZone(ZoneId.systemDefault());

    public static final Text SPACE = Text.of(" ");

    private static final TextTemplate CHAT_TEMPLATE = TextTemplate.of(TextTemplate.arg(MessageEvent.PARAM_MESSAGE_HEADER).build(),
            TextTemplate.arg(MessageEvent.PARAM_MESSAGE_BODY).build(), TextTemplate.arg(MessageEvent.PARAM_MESSAGE_FOOTER).build());

    public static final String usernameRegexPattern = "[0-9a-zA-Z_]{3,16}";
    public static final Pattern usernameRegex = Pattern.compile(usernameRegexPattern);

    public static final UUID consoleFakeUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static Text applyChatTemplate(MessageEvent.MessageFormatter formatter) {
        return applyChatTemplate(formatter.getHeader(), formatter.getBody(), formatter.getFooter());
    }

    public static Text applyChatTemplate(TextRepresentable header, TextRepresentable body, TextRepresentable footer) {
        return CHAT_TEMPLATE.apply(
                ImmutableMap.of(
                MessageEvent.PARAM_MESSAGE_HEADER, header,
                MessageEvent.PARAM_MESSAGE_BODY, body,
                MessageEvent.PARAM_MESSAGE_FOOTER, footer)).build();
    }

    public static UUID getUUID(CommandSource src) {
        if (src instanceof Identifiable) {
            return ((Identifiable) src).getUniqueId();
        }

        return consoleFakeUUID;
    }

    public static Optional<User> getUserFromUUID(UUID uuid) {
        return Sponge.getServiceManager().provideUnchecked(UserStorageService.class)
                .get(uuid).map(x -> x.isOnline() ? ((User)x.getPlayer().get()) : x);
    }

    public static Object getObjectFromUUID(UUID uuid) {
        Optional<Object> user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class)
                .get(uuid).map(x -> x.isOnline() ? x.getPlayer().get() : x);
        return user.orElseGet(() -> Sponge.getServer().getConsole());

    }

    public static String getTimeToNow(Instant time) {
        return getTimeToNow(time, "standard.now");
    }

    public static String getTimeToNow(Instant time, String nowkey) {
        return getTimeStringFromSeconds(Instant.now().getEpochSecond() - time.getEpochSecond(), nowkey);
    }

    public static String getTimeStringFromSeconds(long time) {
        return getTimeStringFromSeconds(time, "standard.now");
    }

    public static String getTimeStringFromSeconds(long time, String nowkey) {
        time = Math.abs(time);
        long sec = time % 60;
        long min = (time / 60) % 60;
        long hour = (time / 3600) % 24;
        long day = time / 86400;

        if (time == 0) {
            return I18N.getString(nowkey);
        }

        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append(" ");
            if (day > 1) {
                sb.append(I18N.getString("standard.days"));
            } else {
                sb.append(I18N.getString("standard.day"));
            }
        }

        if (hour > 0) {
            appendComma(sb);
            sb.append(hour).append(" ");
            if (hour > 1) {
                sb.append(I18N.getString("standard.hours"));
            } else {
                sb.append(I18N.getString("standard.hour"));
            }
        }

        if (min > 0) {
            appendComma(sb);
            sb.append(min).append(" ");
            if (min > 1) {
                sb.append(I18N.getString("standard.minutes"));
            } else {
                sb.append(I18N.getString("standard.minute"));
            }
        }

        if (sec > 0) {
            appendComma(sb);
            sb.append(sec).append(" ");
            if (sec > 1) {
                sb.append(I18N.getString("standard.seconds"));
            } else {
                sb.append(I18N.getString("standard.second"));
            }
        }

        if (sb.length() > 0) {
            return sb.toString();
        } else {
            return I18N.getString("standard.unknown");
        }
    }

    public static String getTimeFromTicks(long ticks) {
        if (ticks < 0 || ticks > 23999) {
            // Normalise
            ticks = ticks % 24000;
        }

        int mins = (int) ((ticks % 1000) / (100. / 6.));
        long hours = (ticks / 1000 + 6) % 24;

        NumberFormat m = NumberFormat.getIntegerInstance();
        m.setMinimumIntegerDigits(2);

        if (hours < 12) {
            long ahours = hours == 0 ? 12 : hours;
            return MessageFormat.format(I18N.getString("standard.time.am"), ahours, hours, m.format(mins));
        } else {
            hours -= 12;
            long ahours = hours == 0 ? 12 : hours;
            return MessageFormat.format(I18N.getString("standard.time.pm"), ahours, hours, m.format(mins));
        }
    }

    private static void appendComma(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.append(", ");
        }
    }

    public static boolean isFirstPlay(User player) {
        try {
            Instant firstPlayed = player.get(JoinData.class).get().firstPlayed().get();
            Instant lastPlayed = player.get(JoinData.class).get().lastPlayed().get();

            // lastPlayed is always ticking - give three seconds.
            // TODO: Better way of doing this.
            return firstPlayed.isAfter(lastPlayed.minus(10, ChronoUnit.SECONDS));
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Utility method for getting the first available option from an {@link Subject}
     *
     * @param player The {@link User} to get the subject from.
     * @param options The option keys to check.
     * @return An {@link Optional} that might contain a value.
     */
    public static Optional<String> getOptionFromSubject(Subject player, String... options) {
        for (String option : options) {
            String o = option.toLowerCase();

            // Option for context.
            Optional<String> os = player.getOption(player.getActiveContexts(), o);
            if (os.isPresent()) {
                return os.map(r -> r.isEmpty() ? null : r);
            }

            // General option
            os = player.getOption(o);
            if (os.isPresent()) {
                return os.map(r -> r.isEmpty() ? null : r);
            }
        }

        return Optional.empty();
    }

    /**
     * Gets the {@link ItemType} or {@link BlockState} for an ID.
     *
     * @param id The ID to check.
     * @return An {@link Optional} containing the intended {@link CatalogType}
     */
    public static Optional<CatalogType> getCatalogTypeForItemFromId(String id) {
        // Check for ItemType.
        Optional<CatalogType> oit = Sponge.getRegistry().getAllOf(ItemType.class).stream().filter(x -> x.getId().equalsIgnoreCase(id))
                .findFirst().map(x -> x);
        return oit.map(Optional::of)
                .orElseGet(() -> Sponge.getRegistry().getAllOf(BlockState.class).stream().filter(x -> x.getId().equalsIgnoreCase(id)).findFirst()
                        .map(x -> (CatalogType) x));

        // BlockState, you're up next!

    }

    public static <T extends CatalogType> String getTranslatableIfPresentOnCatalogType(T ct) {
        if (ct instanceof ItemType) {
            return ItemStack.of((ItemType)ct, 1).getTranslation().get();
        } else if (ct instanceof BlockState) {
            return ItemStack.builder().fromBlockState(((BlockState) ct)).build().getTranslation().get();
        } else  if (ct instanceof Translatable) {
            return getTranslatableIfPresent((Translatable & CatalogType)ct);
        }

        return ct.getName();
    }

    /**
     * As some {@link Translatable#getTranslation()} methods have not been implemented yet, this allows us to try to use
     * the method in a safer manner for {@link CatalogType}s.
     *
     * @param translatable The {@link Translatable} to get the translation from, if appropriate.
     * @param <T> The {@link CatalogType} that is also a {@link Translatable}
     * @return A {@link String} that represents the item.
     */
    public static <T extends Translatable & CatalogType> String getTranslatableIfPresent(T translatable) {
        try {
            String result = translatable.getTranslation().get();

            if (!result.isEmpty()) {
                return result;
            }
        } catch (AbstractMethodError e) {
            //
        }

        return translatable.getName();
    }

    /**
     * Gets a key from a map based on a case insensitive key.
     *
     * @param map The {@link Map} to check.
     * @param key The {@link String} key.
     * @return An {@link Optional}, which contains the key if it exists.
     */
    public static Optional<String> getKeyIgnoreCase(Map<String, ?> map, String key) {
        return getKeyIgnoreCase(map.keySet(), key);
    }

    /**
     * Gets a key from a map based on a case insensitive key.
     *
     * @param collection The {@link Collection} to check.
     * @param key The {@link String} key.
     * @return An {@link Optional}, which contains the key if it exists.
     */
    public static Optional<String> getKeyIgnoreCase(Collection<String> collection, String key) {
        return collection.stream().filter(x -> x.equalsIgnoreCase(key)).findFirst();
    }

    /**
     * Gets a value from a map based on a case insensitive key.
     *
     * @param map The {@link Map} to check.
     * @param key The {@link String} key.
     * @param <T> The type of values in the map.
     * @return An {@link Optional}, which contains a value if the key exists in some case form.
     */
    public static <T> Optional<T> getValueIgnoreCase(Map<String, T> map, String key) {
        return map.entrySet().stream().filter(x -> x.getKey().equalsIgnoreCase(key))
                .map(Map.Entry::getValue).findFirst();
    }

    /**
     * Tests to see if the supplied {@link Location} is within the world's {@link org.spongepowered.api.world.WorldBorder}
     *
     * @param location The {@link Location} to test.
     * @return <code>true</code> if the location is within the border.
     */
    public static boolean isLocationInWorldBorder(Location<World> location) {
        return isLocationInWorldBorder(location.getPosition(), location.getExtent());
    }

    public static boolean isLocationInWorldBorder(Vector3d location, World world) {

        // Diameter, not radius - we'll want the radius later. We use long, we want the floor!
        long radius = (long)Math.floor(world.getWorldBorder().getDiameter() / 2.0);

        // We get the current position and subtract the border centre. This gives us an effective distance from the
        // centre in all three dimensions. We just care about the magnitude in the x and z directions, so we get the
        // positive amount.
        Vector3d displacement = location.sub(world.getWorldBorder().getCenter()).abs();

        // Check that we're not too far out.
        return !(displacement.getX() > radius || displacement.getZ() > radius);
    }

    /**
     * Gets all of the subject's parent {@link Subject}s for the given {@link Context}
     *
     * @param pl The {@link Subject} to get the parents of
     * @return The {@link List} of {@link Subject}s, or an empty list if there nothing was found.
     */
    public static CompletableFuture<List<Subject>> getParentSubjects(Subject pl) {
        Set<Context> contextSet = pl.getActiveContexts();

        return CompletableFuture.supplyAsync(() -> {
            Map<Subject, Integer> subjects = Maps.newHashMap();

            // Try to cache already known values
            Function<Subject, Integer> subjectIntegerFunction = subject -> subjects.computeIfAbsent(subject, k -> k.getParents(contextSet).size());

            return pl.getParents(contextSet).stream().distinct()
                    .map(x -> {
                        try {
                            return x.resolve().get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(subjectIntegerFunction::apply))
                    .collect(Collectors.toList());
        });

    }

    public static void compressAndDeleteFile(Path from) throws IOException {
        // Get the file.
        if (Files.exists(from)) {
            Path to = Paths.get(from.toString() + ".gz");
            try (OutputStream os = new GZIPOutputStream(new FileOutputStream(to.toFile()))) {
                Files.copy(from, os);
                os.flush();
                Files.delete(from);
            }

        }

    }

    public static Inventory.Builder getKitInventoryBuilder() {
        return Inventory.builder().of(InventoryArchetypes.CHEST).property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 4));
    }


    public static ItemStack dropItemOnFloorAtLocation(ItemStackSnapshot itemStackSnapshotToDrop, Location<World> location) {
        return dropItemOnFloorAtLocation(itemStackSnapshotToDrop, location.getExtent(), location.getPosition());
    }

    public static ItemStack dropItemOnFloorAtLocation(ItemStackSnapshot itemStackSnapshotToDrop, World world, Vector3d position) {
        Entity entityToDrop = world.createEntity(EntityTypes.ITEM, position);
        entityToDrop.offer(Keys.REPRESENTED_ITEM, itemStackSnapshotToDrop);
        world.spawnEntity(entityToDrop);
        return itemStackSnapshotToDrop.createStack();
    }

    public static Inventory getStandardInventory(Carrier player) {
        return player.getInventory()
                .query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class))
                .transform(InventoryTransformations.PLAYER_MAIN_HOTBAR_FIRST);
    }

    public static <T extends Event> void onPlayerSimulatedOrPlayer(T event, BiConsumer<T, Player> eventConsumer) {
        // If we're simulating a player, we should use them instead.
        @Nullable Player cs = checkSimulated(event).orElseGet(() -> {
            Object root = event.getCause().root();
            if (root instanceof Player) {
                return (Player) root;
            }

            return null;
        });

        if (cs != null) {
            eventConsumer.accept(event, cs);
        }

    }

    public static <T extends Event> void onSourceSimulatedOr(T event, Function<T, Optional<CommandSource>> orElse,
            BiConsumer<T, CommandSource> eventConsumer) {
        // If we're simulating a player, we should use them instead.
        @Nullable CommandSource cs = checkSimulated(event).map(x -> (CommandSource) x).orElseGet(() -> orElse.apply(event).orElse(null));
        if (cs != null) {
            eventConsumer.accept(event, cs);
        }
    }

    private static Optional<Player> checkSimulated(Event event) {
        return event.getContext().get(EventContextKeys.PLAYER_SIMULATED).map(x -> Sponge.getServer().getPlayer(x.getUniqueId()).orElse(null));
    }
}
