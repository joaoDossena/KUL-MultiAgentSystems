package environment.world.gradient;

import java.util.Collection;

import com.google.common.eventbus.EventBus;

import environment.World;
import environment.world.energystation.EnergyStation;
import environment.world.energystation.EnergyStationWorld;
import environment.world.packet.Packet;
import environment.world.packet.PacketWorld;
import environment.world.wall.Wall;
import environment.world.wall.WallWorld;

import static java.lang.Math.min;

public class GradientWorld extends World<Gradient> {

    /**
     * Initialize the GradientWorld
     */
    public GradientWorld(EventBus eventBus) {
        super(eventBus);
    }


    /**
     * Place a collection of gradients inside the Gradient World.
     *
     * @param gradients The collection of gradients.
     */
    @Override
    public void placeItems(Collection<Gradient> gradients) {
        gradients.forEach(this::placeItem);
    }

    /**
     * Place a single gradient in the Gradient World.
     *
     * @param item The gradient.
     */
    @Override
    public void placeItem(Gradient item) {
        putItem(item);
    }

    // TODO: improve the algorithm
    public void addGradientsWithStartLocation(int x, int y, int val) {

        int height = getEnvironment().getHeight();
        int width = getEnvironment().getWidth();

        int MAX_GRAD = 16;
        int limit = min(MAX_GRAD, min(width, height));

        if (x < 0 || y < 0 || x >= width || y >= height || val > limit) {
            return;
        }

        Wall wall = getEnvironment().getWorld(WallWorld.class).getItem(x, y);
        if (wall != null) {
            return;
        }

        Packet packet = getEnvironment().getWorld(PacketWorld.class).getItem(x, y);
        if (packet != null) {
            return;
        }

        EnergyStation energyStation = getEnvironment().getWorld(EnergyStationWorld.class).getItem(x, y);
        if (energyStation != null && val > 0) {
            return;
        }

        Gradient existing = getItem(x, y);
        if (existing != null && existing.getValue() < val) {
            return;
        }

        placeItem(new Gradient(x, y, val));

        addGradientsWithStartLocation(x - 1, y, val + 1);
        addGradientsWithStartLocation(x - 1, y - 1, val + 1);
        addGradientsWithStartLocation(x, y - 1, val + 1);
        addGradientsWithStartLocation(x + 1, y - 1, val + 1);
        addGradientsWithStartLocation(x + 1, y, val + 1);
        addGradientsWithStartLocation(x + 1, y + 1, val + 1);
        addGradientsWithStartLocation(x, y + 1, val + 1);
        addGradientsWithStartLocation(x - 1, y + 1, val + 1);
    }
}
