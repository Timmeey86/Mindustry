package io.anuke.mindustry.world.blocks.power;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Queue;
import io.anuke.mindustry.world.Tile;

import static io.anuke.mindustry.Vars.threads;

public class PowerGraph{
    private final static Queue<Tile> queue = new Queue<>();
    private final static Array<Tile> outArray1 = new Array<>();
    private final static Array<Tile> outArray2 = new Array<>();
    private final static IntSet closedSet = new IntSet();

    private final ObjectSet<Tile> producers = new ObjectSet<>();
    private final ObjectSet<Tile> consumers = new ObjectSet<>();
    private final ObjectSet<Tile> all = new ObjectSet<>();

    private long lastFrameUpdated;
    private final int graphID;
    private static int lastGraphID;

    {
        graphID = lastGraphID++;
    }

    public int getID(){
        return graphID;
    }

<<<<<<< HEAD
    public void update(){
        if(threads.getFrameID() == lastFrameUpdated || consumers.size == 0 || producers.size == 0){
            return;
        }

        lastFrameUpdated = threads.getFrameID();

        boolean charge = false;

        float totalInput = 0f;
        float bufferInput = 0f;
=======
    public float getPowerProduced(){
        float powerProduced = 0f;
>>>>>>> 8218cfc7... cleaned up PowerGraph
        for(Tile producer : producers){
            if(producer.block().consumesPower){
                bufferInput += producer.entity.power.amount;
            }else{
                totalInput += producer.entity.power.amount;
            }
        }
        return powerProduced;
    }

    public float getPowrerNeeded(){
        float powerNeeded = 0f;
        for(Tile consumer : consumers){
            if(consumer.block().outputsPower){
                bufferOutput += consumer.block().powerCapacity - consumer.entity.power.amount;
            }else{
                maxOutput += consumer.block().powerCapacity - consumer.entity.power.amount;
            }
        }
        return powerNeeded;
    }

    public float getBatteryStored(){
        float totalAccumulator = 0f;
        for(Tile battery : batteries){
            totalAccumulator += battery.entity.power.satisfaction * battery.block().basePowerUse;
        }
    }

    public float getBatteryCapacity(){
        float totalCapacity = 0f;
        for(Tile battery : batteries){
            totalCapacity += (1f - battery.entity.power.satisfaction) * battery.block().basePowerUse;
        }
    }

    public float useBatteries(float needed){
        float stored = getBatteryStored();
        float used = Math.min(stored, needed);
        float thing = 1f - (used / stored);
        for(Tile battery : batteries){
            battery.entity.power.satisfaction *= thing;
        }
        return used;
    }

    public float chargeBatteries(float excess){
        float capacity = getBatteryCapacity();
        float thing = Math.min(1, excess / capacity);
        for(tile battery : batteries){
            battery.power.satisfaction += (1 - battery.power.satisfaction) * thing;
        }
        return Math.min(excess, capacity);
    }

    public void distributePower(float needed, float produced){
        float satisfaction = Math.min(1, produced / needed);
        for(Tile consumer : consumers){
            if(consumer.block().bufferedPowerConsumer){
                consumer.power.satisfaction += (1 - consumer.power.satisfaction) * satisfaction;
            }else{
                consumer.power.satisfaction = satisfaction;
            }
            producer.entity.power.amount -= producer.entity.power.amount * inputUsed;
        }
    }

    public void update(){
        if(threads.getFrameID() == lastFrameUpdated || consumers.size == 0 || producers.size == 0){
            return;
        }

        lastFrameUpdated = threads.getFrameID();

        float powerNeeded = getPowrerNeeded();
        float powerProduced = getPowerProduced();

        if(powerNeeded > powerProduced){
            powerProduced += useBatteries(powerNeeded - powerProduced);
        }else if(powerProduced > powerNeeded){
            powerProduced -= chargeBatteries(powerProduced - powerNeeded);
        }

        distributePower(powerNeeded, powerProduced);
    }

    public void add(PowerGraph graph){
        for(Tile tile : graph.all){
            add(tile);
        }
    }

    public void add(Tile tile){
        tile.entity.power.graph = this;
        all.add(tile);

        if(tile.block().outputsPower){
            producers.add(tile);
        }

        if(tile.block().consumesPower){
            consumers.add(tile);
        }
    }

    public void clear(){
        for(Tile other : all){
            if(other.entity != null && other.entity.power != null) other.entity.power.graph = null;
        }
        all.clear();
        producers.clear();
        consumers.clear();
    }

    public void reflow(Tile tile){
        queue.clear();
        queue.addLast(tile);
        closedSet.clear();
        while(queue.size > 0){
            Tile child = queue.removeFirst();
            child.entity.power.graph = this;
            add(child);
            for(Tile next : child.block().getPowerConnections(child, outArray2)){
                if(next.entity.power != null && next.entity.power.graph == null && !closedSet.contains(next.packedPosition())){
                    queue.addLast(next);
                    closedSet.add(next.packedPosition());
                }
            }
        }
    }

    public void remove(Tile tile){
        clear();
        closedSet.clear();

        for(Tile other : tile.block().getPowerConnections(tile, outArray1)){
            if(other.entity.power == null || other.entity.power.graph != null) continue;
            PowerGraph graph = new PowerGraph();
            queue.clear();
            queue.addLast(other);
            while(queue.size > 0){
                Tile child = queue.removeFirst();
                child.entity.power.graph = graph;
                graph.add(child);
                for(Tile next : child.block().getPowerConnections(child, outArray2)){
                    if(next != tile && next.entity.power != null && next.entity.power.graph == null && !closedSet.contains(next.packedPosition())){
                        queue.addLast(next);
                        closedSet.add(next.packedPosition());
                    }
                }
            }
        }
    }

    @Override
    public String toString(){
        return "PowerGraph{" +
        "producers=" + producers +
        ", consumers=" + consumers +
        ", all=" + all +
        ", lastFrameUpdated=" + lastFrameUpdated +
        ", graphID=" + graphID +
        '}';
    }
}
