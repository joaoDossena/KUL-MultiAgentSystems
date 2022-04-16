package environment.world.gradient;

import java.util.Collection;

import com.google.common.eventbus.EventBus;

import environment.World;

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

        int limit = min(16, min(width, height));
        if (x < 0 || y < 0 || x >= width || y >= height || val > limit) {
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
