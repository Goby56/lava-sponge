package com.goby56.lavasponge.world;

public class ModWorldEvents {

    /**
     * A wet lava sponge cools down on top of blue ice. Spawns cloud particles.
     * Called by WetLavaSpongeBlock#onBlockAdded
     */
    public static final int WET_LAVA_SPONGE_COOLS_DOWN = 5609;

    /**
     * A wet lava sponge is placed in water which evaporates. Spawn cloud particles.
     * Called by WetLavaSpongeBlock#onBlockAdded
     */
    public static final int WET_LAVA_SPONGE_EVAPORATES_WATER = 5610;

}
