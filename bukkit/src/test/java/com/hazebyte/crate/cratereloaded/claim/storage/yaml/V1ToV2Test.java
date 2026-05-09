package com.hazebyte.crate.cratereloaded.claim.storage.yaml;

import static com.hazebyte.crate.cratereloaded.claim.storage.yaml.YamlClaimConstants.CONFIG_PLAYER_KEY;
import static com.hazebyte.crate.cratereloaded.claim.storage.yaml.YamlClaimConstants.CONFIG_REWARDS_KEY;
import static com.hazebyte.crate.cratereloaded.claim.storage.yaml.YamlClaimConstants.CONFIG_TIMESTAMP_KEY;
import static com.hazebyte.crate.cratereloaded.claim.storage.yaml.YamlClaimConstants.CONFIG_VERSION_KEY;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.hazebyte.crate.BukkitTest;
import com.hazebyte.crate.api.claim.Claim;
import com.hazebyte.crate.cratereloaded.claim.ClaimExecutor;
import com.hazebyte.crate.cratereloaded.component.OpenCrateComponent;
import com.hazebyte.crate.cratereloaded.model.Config;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class V1ToV2Test extends BukkitTest {

    private Collection<Claim> claims;

    private PlayerMock playerMock;

    private String zeroResults = "100";

    private ClaimExecutor claimExecutor;

    @BeforeEach
    public void setup() {
        playerMock = server.addPlayer();
        OpenCrateComponent mockOpenCrateComponent = Mockito.mock(OpenCrateComponent.class);
        Logger mockLogger = Mockito.mock(Logger.class);
        claimExecutor = new ClaimExecutor(plugin, mockOpenCrateComponent, mockLogger);
    }

    @Test
    public void integrationTest() throws Exception {
        YamlClaimStorage storage = new YamlClaimStorage(plugin, claimExecutor);
        YamlClaimLineParser parser = new V1YamlClaimLineParser(claimExecutor);
        Config config = storage.getConfig(playerMock);
        List<String> rewardStrings = Arrays.asList("chance:(0)", "chance:(0)");
        config.getConfig().set(zeroResults, rewardStrings);
        V1ToV2 v1ToV2 = new V1ToV2(config, zeroResults, playerMock);
        Claim claim = v1ToV2.migrate(storage, parser);

        Assertions.assertNotNull(claim);
        Assertions.assertEquals(2, claim.getRewards().size());
        Assertions.assertEquals(1, config.getConfig().getKeys(false).size());
        String newKey = config.getConfig().getKeys(false).stream().findFirst().get();
        Assertions.assertEquals(claim.getId().toString(), newKey);
        Assertions.assertTrue(config.getConfig().isSet(String.format(CONFIG_VERSION_KEY, newKey)));
        Assertions.assertTrue(config.getConfig().isSet(String.format(CONFIG_PLAYER_KEY, newKey)));
        Assertions.assertTrue(config.getConfig().isSet(String.format(CONFIG_REWARDS_KEY, newKey)));
        Assertions.assertTrue(config.getConfig().isSet(String.format(CONFIG_TIMESTAMP_KEY, newKey)));
        Assertions.assertEquals(
                playerMock.getUniqueId().toString(),
                config.getConfig().getString(String.format(CONFIG_PLAYER_KEY, newKey)));
        Assertions.assertEquals(
                2,
                config.getConfig()
                        .getStringList(String.format(CONFIG_REWARDS_KEY, newKey))
                        .size());
        Assertions.assertEquals(
                String.valueOf(claim.getTimestamp()),
                config.getConfig().getString(String.format(CONFIG_TIMESTAMP_KEY, newKey)));
    }

    @Test
    public void verifyIsOutOfDate() {
        Config config = Mockito.mock(Config.class, Mockito.RETURNS_DEEP_STUBS);
        String versionKey = String.format(CONFIG_VERSION_KEY, zeroResults);
        Mockito.when(config.getConfig().getString(versionKey)).thenReturn("v2");
        V1ToV2 v1ToV2 = new V1ToV2(config, zeroResults, playerMock);
        Assertions.assertFalse(v1ToV2.isOutOfDate(config, zeroResults));
    }

    @Test
    public void verifyIsNotOutOfDate() {
        Config config = Mockito.mock(Config.class, Mockito.RETURNS_DEEP_STUBS);
        String versionKey = String.format(CONFIG_VERSION_KEY, zeroResults);
        Mockito.when(config.getConfig().getString(versionKey)).thenReturn(null);
        V1ToV2 v1ToV2 = new V1ToV2(config, zeroResults, playerMock);
        Assertions.assertTrue(v1ToV2.isOutOfDate(config, zeroResults));
    }
}
