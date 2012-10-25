package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class SliderUI extends HorizontalPanel {
	Image min = new Image("/images/flags-american_samoa.png");
	Image decrement = new Image("/images/flags-american_samoa.png");
	Image increment = new Image("/images/flags-american_samoa.png");
	Image max = new Image("/images/flags-american_samoa.png");
	MinMaxIncrementControl control;

	SliderUI(MinMaxIncrementControl ctrl){
		assert(ctrl!=null);
		control=ctrl;
		this.add(min);
		this.add(decrement);
		this.add(increment);
		this.add(max);
		setupListeners();
	}
	
	private void setupListeners(){
		min.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				control.setMin();
			}
		});
		decrement.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				control.decrement();
			}
		});
		increment.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				control.increment();
			}
		});
		max.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				control.setMax();
			}
		});
	}
}