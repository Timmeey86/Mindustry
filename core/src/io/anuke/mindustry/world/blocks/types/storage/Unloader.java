package io.anuke.mindustry.world.blocks.types.storage;

import io.anuke.mindustry.resource.Item;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

public class Unloader extends Block {
    protected final int timerUnload = timers++;

    public Unloader(String name){
        super(name);
        update = true;
        solid = true;
        health = 70;
    }

    @Override
    public void update(Tile tile){
        if(tile.entity.totalItems() == 0 && tile.entity.timer.get(timerUnload, 5)){
            tile.allNearby(other -> {
                if(tile.entity.totalItems() == 0 && other.block() instanceof StorageBlock &&
                        other.entity.totalItems() > 0){
                    offloadNear(tile, ((StorageBlock)other.block()).removeItem(other));
                }
            });
        }

        if(tile.entity.totalItems() > 0){
            tryDump(tile);
        }
    }

    @Override
    public boolean canDump(Tile tile, Tile to, Item item) {
        Block block = to.target().block();
        return !(block instanceof StorageBlock);
    }
}