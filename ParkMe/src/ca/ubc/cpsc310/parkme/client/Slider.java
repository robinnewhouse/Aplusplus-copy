package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.kiouri.sliderbar.client.view.SliderBarHorizontal;

public class Slider extends SliderBarHorizontal{
	Image less = new Image("/images/Slider/minush.png");
	Image scaleH = new Image("/images/Slider/scalehthinblack.png");
	Image more = new Image("/images/Slider/plush.png");
	Image drag = new Image("/images/Slider/draghthin.png");
	
	public Slider(int maxValue) {
		boolean showRows = true;
		if (showRows){
			setLessWidget(less);
			setScaleWidget(scaleH, 10);
			setMoreWidget(more);
		} else {
		    setScaleWidget(scaleH, 10);
		}
		setDragWidget(drag);
		this.setWidth("200px");
		this.drawMarks("white",6);
		this.setMaxValue(maxValue);
	}

}

