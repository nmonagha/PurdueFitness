package com.moufee.boilerfit

import com.google.gson.JsonParser
import com.moufee.boilerfit.api.AllergensTypeConverter
import org.junit.Assert.assertThat
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as Is

class JsonConverterTest {

    @Test
    fun allergenConverterTest() {
        val allergensJson = """[
                                {
                                    "Name": "Eggs",
                                    "Value": true
                                },
                                {
                                    "Name": "Fish",
                                    "Value": false
                                },
                                {
                                    "Name": "Gluten",
                                    "Value": true
                                },
                                {
                                    "Name": "Milk",
                                    "Value": true
                                },
                                {
                                    "Name": "Peanuts",
                                    "Value": false
                                },
                                {
                                    "Name": "Shellfish",
                                    "Value": false
                                },
                                {
                                    "Name": "Soy",
                                    "Value": true
                                },
                                {
                                    "Name": "Tree Nuts",
                                    "Value": false
                                },
                                {
                                    "Name": "Vegetarian",
                                    "Value": true
                                },
                                {
                                    "Name": "Vegan",
                                    "Value": false
                                },
                                {
                                    "Name": "Wheat",
                                    "Value": true
                                }
                            ]"""
        val element = JsonParser().parse(allergensJson)
        val parsedAllergens = AllergensTypeConverter().deserialize(element, null, null)
        assertThat(parsedAllergens.hasEggs(), Is(true))
        assertThat(parsedAllergens.hasFish(), Is(false))
        assertThat(parsedAllergens.hasGluten(), Is(true))
        assertThat(parsedAllergens.hasWheat(), Is(true))
        assertThat(parsedAllergens.hasMilk(), Is(true))
        assertThat(parsedAllergens.hasSoy(), Is(true))
        assertThat(parsedAllergens.hasPeanuts(), Is(false))
        assertThat(parsedAllergens.hasShellfish(), Is(false))
        assertThat(parsedAllergens.hasTreeNuts(), Is(false))
        assertThat(parsedAllergens.isVegetarian, Is(true))
        assertThat(parsedAllergens.isVegan, Is(false))


    }
}