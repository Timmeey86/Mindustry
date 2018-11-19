import io.anuke.mindustry.Vars;
import io.anuke.mindustry.core.ContentLoader;
import io.anuke.mindustry.world.blocks.power.PowerGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PowerTests{

    @BeforeAll
    static void initializeDependencies(){
        Vars.content = new ContentLoader();
        Vars.content.load();
    }

    @BeforeEach
    void initTest(){
    }

    @Test
    void testInitialization(){
        PowerGraph powerGraph = new PowerGraph();
    }
}
