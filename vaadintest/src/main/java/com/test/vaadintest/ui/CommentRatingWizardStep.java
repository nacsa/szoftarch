package com.test.vaadintest.ui;

import org.vaadin.teemu.ratingstars.RatingStars;
import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class CommentRatingWizardStep implements WizardStep{

	TextArea commentArea;
	RatingStars rating;
	
	public CommentRatingWizardStep(TextArea commentArea, RatingStars rating) {
		this.commentArea = commentArea;
		this.rating = rating;
		
	}
	
	@Override
	public String getCaption() {
		return "Comment and rating";
	}

	@Override
	public Component getContent() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);

		commentArea.setWidth("100%");
		commentArea.setHeight("400px");
		
		mainLayout.addComponent(commentArea);
		mainLayout.addComponent(rating);

		return mainLayout;
	}

	@Override
	public boolean onAdvance() {
		return true;
	}

	@Override
	public boolean onBack() {
		return true;
	}

}
