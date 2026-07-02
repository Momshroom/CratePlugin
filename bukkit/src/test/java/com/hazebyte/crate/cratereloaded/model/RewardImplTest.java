package com.hazebyte.crate.cratereloaded.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RewardImplTest {

    @Test
    public void hasPostParsing_returnsFalse_whenRewardHasNoSourceLine() {
        RewardImpl reward = new RewardImpl();

        Assertions.assertFalse(reward.hasPostParsing());
    }
}
