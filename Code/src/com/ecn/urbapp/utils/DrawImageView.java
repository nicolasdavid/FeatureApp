/*--------------------------------------------------------------------

Copyright Jonathan Cozzo and Patrick Rannou (22/03/2013)

This software is an Android application whose purpose is to select 
and characterize zones on a photography (type, material, color...).

This software is governed by the CeCILL license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

-----------------------------------------------------------------------*/

package com.ecn.urbapp.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import com.ecn.urbapp.zones.*;

/**
 * This class is used to draw the zones on the image
 * 
 * @author patrick
 * 
 */
public class DrawImageView extends Drawable {

	/** Zones that need to be drawn */
	public SetOfZone zones;

	/**
	 * Constructor of this class
	 * 
	 * @param zones
	 *            the set of zones the need to be drawn
	 */
	public DrawImageView(SetOfZone zones) {
		super();
		this.zones = zones;
	}

	/**
	 * This method is called to draw the zones
	 */
	@Override
	public void draw(Canvas canvas) {
		// Paint for unfinished points
		Paint unfinishedPaint = new Paint();
		unfinishedPaint.setColor(Color.RED);
		unfinishedPaint.setStyle(Paint.Style.STROKE);

		// Paint for finished points
		Paint finishedPaint = new Paint();
		finishedPaint.setColor(Color.GREEN);
		finishedPaint.setStyle(Paint.Style.STROKE);

		Paint fillPaint = new Paint();
		fillPaint.setColor(Color.GREEN);
		fillPaint.setStyle(Style.FILL);
		fillPaint.setAlpha(50);

		// For all the zones
		for (int i = 0; i < zones.getZones().size(); i++) {
			// If the zone is not selected, only draw the lines
			if (!zones.getZones().get(i).isSelected()) {
				// Paint in a different color depending on its state
				if (zones.getZones().get(i).isFinished()) {
					if (zones.getZones().get(i).getColor() != 0) {
						finishedPaint.setColor(zones.getZones().get(i).getColor());
					}
					// Add all the lines of the polygon
					for (int j = 0; j < zones.getZones().get(i).points
							.size() - 1; j++) {
						canvas.drawLine(
								zones.getZones().get(i).points.get(j).x,
								zones.getZones().get(i).points.get(j).y,
								zones.getZones().get(i).points
										.get(j + 1).x, zones.getZones()
										.get(i).points.get(j + 1).y,
								finishedPaint);
					}
					finishedPaint.setColor(Color.GREEN);
				} else {
					// Add all the lines of the polygon
					for (int j = 0; j < zones.getZones().get(i).points.size() - 1; j++) {
						canvas.drawLine(
								zones.getZones().get(i).points.get(j).x,
								zones.getZones().get(i).points.get(j).y,
								zones.getZones().get(i).points
										.get(j + 1).x, zones.getZones()
										.get(i).points.get(j + 1).y,
								unfinishedPaint);
					}

				}

				// If the zone is selected, draw a filled polygon
			} else {
				// Create a closed path for the polygon
				Path polyPath = new Path();
				polyPath.moveTo(
						zones.getZones().get(i).points.get(0).x,
						zones.getZones().get(i).points.get(0).y);
				for (int j = 0; j < zones.getZones().get(i).points
						.size(); j++) {
					polyPath.lineTo(
							zones.getZones().get(i).points.get(j).x,
							zones.getZones().get(i).points.get(j).y);
				}

				if (zones.getZones().get(i).getColor() != 0) {
					fillPaint.setColor(zones.getZones().get(i).getColor());
					fillPaint.setAlpha(50);
				}
				// Draw the polygon
				canvas.drawPath(polyPath, fillPaint);
				fillPaint.setColor(Color.GREEN);
				fillPaint.setAlpha(50);
			}
		}
	}

	@Override
	public void setAlpha(int arg0) {
	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
	}

	@Override
	public int getOpacity() {
		return 0;
	}
}
