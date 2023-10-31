package com.mtg.mtgtest.card.catalog.model.Impl;

import com.mtg.mtgtest.card.catalog.model.Card;
import com.mtg.mtgtest.card.catalog.model.Color;
import org.immutables.value.Value;

import java.util.Set;

public class TestCard implements Card {

    private final String name;
    private final Set<Color> colorIdentity;
    private final int convertedManaCost;

    public TestCard(String name, Set<Color> colorIdentity, int convertedManaCost) {
        this.name = name;
        this.colorIdentity = colorIdentity;
        this.convertedManaCost = convertedManaCost;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Set<Color> colorIdentity() {
        return colorIdentity;
    }

    @Override
    public int convertedManaCost() {
        return convertedManaCost;
    }

}