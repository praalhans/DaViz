package com.aexiz.daviz.ui.plaf;

import com.aexiz.daviz.ui.JCarousel;

import javax.swing.plaf.ComponentUI;
import java.awt.*;

public abstract class CarouselUI extends ComponentUI {

    public abstract int locationToIndex(JCarousel c, Point location);

    public abstract Point indexToLocation(JCarousel c, int index);

    public abstract Rectangle getCellBounds(JCarousel c, int from, int to);

}
