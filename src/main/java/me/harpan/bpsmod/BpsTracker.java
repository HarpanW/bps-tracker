package me.harpan.bpsmod;

import net.minecraft.core.BlockPos;
import java.util.LinkedHashSet;

public class BpsTracker {
    private static int firstBrokenTick = 0;
    private static int lastBrokenTick = 0;
    private static int currentTickCounter = 0;
    private static int blocksBroken = 0;
    private static final LinkedHashSet<BlockPos> recentBlocks = new LinkedHashSet<>();

    public static void onClientTick() {
        currentTickCounter++;
    }

    public static void onBlockBroken(BlockPos pos) {
        if (recentBlocks.contains(pos)) {
            return;
        }

        recentBlocks.add(pos);

        if (recentBlocks.size() > 100) {
            BlockPos oldest = recentBlocks.getFirst();
            recentBlocks.remove(oldest);
        }

        blocksBroken++;
        if (blocksBroken == 5) {
            firstBrokenTick = currentTickCounter;
        }

        lastBrokenTick = currentTickCounter;
    }

    public static void reset() {
        firstBrokenTick = 0;
        lastBrokenTick = 0;
        blocksBroken = 0;
        recentBlocks.clear();
    }

    public static double getBps() {
        return getBlocksBroken() / getTimeElapsed();
    }

    public static double getTimeElapsed() {
        if (blocksBroken <= 5) { // after 5th
            return 0.0;
        }
        return (lastBrokenTick - firstBrokenTick) / 20.0;
    }

    public static int getBlocksBroken() {
        return Math.max(0, blocksBroken - 5); // after 5th
    }
}