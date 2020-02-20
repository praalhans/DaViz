package com.aexiz.daviz.ui.plaf;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.plaf.ComponentUI;

import com.aexiz.daviz.ui.JCarousel;

public abstract class CarouselUI extends ComponentUI {
	
	public abstract int locationToIndex(JCarousel c, Point location);
	
	public abstract Point indexToLocation(JCarousel c, int index);

	public abstract Rectangle getCellBounds(JCarousel c, int from, int to);
	
}
