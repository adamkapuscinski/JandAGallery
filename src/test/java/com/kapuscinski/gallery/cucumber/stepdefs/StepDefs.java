package com.kapuscinski.gallery.cucumber.stepdefs;

import com.kapuscinski.gallery.JandAGalleryApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = JandAGalleryApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
