package com.hazebyte.crate.cratereloaded.component.impl;

import com.hazebyte.crate.BukkitTest;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.result.RewardExecutorResult;
import com.hazebyte.crate.api.util.ItemBuilder;
import com.hazebyte.crate.cratereloaded.component.GivePlayerItemsComponent;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.model.RewardImpl;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

public class CrateOpenComponentImplTest extends BukkitTest {

    private static OpenCrateComponentImpl rewardExecutor;

    @BeforeAll
    public static void setupRewardExecutor() {
        PluginSettingComponent settings = Mockito.mock(PluginSettingComponent.class);
        Mockito.when(settings.isHandlingClaims()).thenReturn(true);
        GivePlayerItemsComponent givePlayerItemsComponent = new GivePlayerItemsComponentImpl(plugin, settings);
        rewardExecutor = new OpenCrateComponentImpl(plugin, givePlayerItemsComponent);
    }

    @ParameterizedTest
    @MethodSource("provideForRewardExecutorTest")
    public void verifyRewardExecutor(Reward reward, Player player, Set<RewardExecutorResult> expectedResult) {
        Set<RewardExecutorResult> result = rewardExecutor.executeReward(player, reward);
        Assertions.assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> provideForRewardExecutorTest() {
        Reward rewardWithItem = new RewardImpl();
        rewardWithItem.setItems(Arrays.asList(new ItemBuilder(Material.STONE).asItemStack()));

        Reward rewardWithCommand = new RewardImpl();
        rewardWithCommand.setCommands(Arrays.asList("/test"));

        Reward rewardWithMessage = new RewardImpl();
        rewardWithMessage.setOpenMessage(Arrays.asList(""));

        return Stream.of(
                Arguments.of(
                        rewardWithItem, server.addPlayer(), EnumSet.of(RewardExecutorResult.ITEMS_GIVEN_TO_PLAYER)),
                Arguments.of(rewardWithCommand, server.addPlayer(), EnumSet.of(RewardExecutorResult.COMMANDS_EXECUTED)),
                Arguments.of(
                        rewardWithMessage, server.addPlayer(), EnumSet.of(RewardExecutorResult.MESSAGES_EXECUTED)));
    }
}
