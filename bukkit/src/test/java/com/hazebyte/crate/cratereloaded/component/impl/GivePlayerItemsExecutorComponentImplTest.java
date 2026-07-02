package com.hazebyte.crate.cratereloaded.component.impl;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.hazebyte.crate.BukkitTest;
import com.hazebyte.crate.constants.ItemConstants;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.model.GiveItemExecutorResult;
import com.hazebyte.crate.test.PlayerMockData;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

public class GivePlayerItemsExecutorComponentImplTest extends BukkitTest {

    private static GivePlayerItemsComponentImpl executor;

    private static ItemStack oneItem = new ItemStack(Material.STONE, 1);
    private static ItemStack oneInventory = new ItemStack(Material.STONE, 64 * ItemConstants.SLOTS_IN_INVENTORY);
    private static ItemStack oneInventoryPlusOne =
            new ItemStack(Material.STONE, 64 * ItemConstants.SLOTS_IN_INVENTORY + 1);

    // test contents with minecart stack

    @BeforeAll
    public static void setupExecutor() {
        PluginSettingComponent settings = Mockito.mock(PluginSettingComponent.class);
        Mockito.when(settings.isHandlingClaims()).thenReturn(true);
        executor = new GivePlayerItemsComponentImpl(plugin, settings);
    }

    @ParameterizedTest
    @MethodSource("provideArgsForItemExecutorResult")
    public void execute_returns_correctItemExecutorResult(
            List<ItemStack> items, Set<GiveItemExecutorResult> expected, PlayerMock playerMock) {
        Set<GiveItemExecutorResult> results = executor.giveItems(items, playerMock);

        Assertions.assertEquals(expected, results);
    }

    private static Stream<Arguments> provideArgsForItemExecutorResult() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(oneItem),
                        EnumSet.of(GiveItemExecutorResult.PUT_INTO_PLAYER_INVENTORY),
                        server.addPlayer()),
                Arguments.of(
                        Arrays.asList(oneInventory),
                        EnumSet.of(GiveItemExecutorResult.PUT_INTO_PLAYER_INVENTORY),
                        server.addPlayer()),
                Arguments.of(
                        Arrays.asList(oneInventoryPlusOne),
                        EnumSet.of(
                                GiveItemExecutorResult.PUT_INTO_PLAYER_INVENTORY,
                                GiveItemExecutorResult.PUT_INTO_PLAYER_CLAIM),
                        server.addPlayer()),
                Arguments.of(
                        Arrays.asList(oneItem),
                        EnumSet.of(GiveItemExecutorResult.PUT_INTO_PLAYER_CLAIM),
                        PlayerMockData.createPlayerWithFullInventory(server)));
    }
}
