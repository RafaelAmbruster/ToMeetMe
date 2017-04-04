package com.app.tomeetme.helper.background;

public abstract interface BackgroundProcessEvent {

    public abstract void postProcess();

    public abstract void process();
}

