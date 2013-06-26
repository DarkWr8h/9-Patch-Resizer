/*
 * Copyright 2012 redwarp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.redwarp.tool.resizer.worker;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScreenDensity {
    private float scale;
    private String name;
    private boolean active;

    private static List<ScreenDensity> list = null;
    private static ScreenDensity defaultInputDensity = null;

    static {
        try {
            byte[] data = ByteStreams.toByteArray(ScreenDensity.class.getClassLoader().getResourceAsStream("misc/densities.json"));
            String input = new String(data, Charsets.UTF_8);

            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonObject densitiesObject = parser.parse(input).getAsJsonObject();
            JsonArray densitiesArray = densitiesObject.get("densities").getAsJsonArray();

            Type listType = new TypeToken<List<ScreenDensity>>() {
            }.getType();
            list = gson.fromJson(densitiesArray, listType);
            String defaultDensityName = densitiesObject.get("source").getAsString();
            for (ScreenDensity density : list) {
                if (density.getName().equals(defaultDensityName)) {
                    defaultInputDensity = density;
                    break;
                }
            }
            if (defaultInputDensity == null) {
                defaultInputDensity = list.get(0);
            }
        } catch (IOException e) {
            list = new ArrayList<ScreenDensity>();
            list.add(new ScreenDensity("xhdpi", 2.0f, true));
            defaultInputDensity = list.get(0);
        }
    }

    public static final int LDPI = 0;
    public static final int MDPI = 1;
    public static final int HDPI = 2;
    public static final int XHDPI = 3;

    private ScreenDensity(String name, float density, boolean active) {
        this.scale = density;
        this.name = name;
        this.active = active;
    }

    @Override
    public String toString() {
        return this.name + String.format(" (%.2f", this.scale) + " - active = " + active + ")";
    }

    public String getName() {
        return this.name;
    }

    public float getScale() {
        return this.scale;
    }


    public static ScreenDensity getDensity(int density)
            throws UnsupportedDensityException {
        if (density == LDPI) {
            return new ScreenDensity("ldpi", 0.75f, true);
        }
        if (density == MDPI) {
            return new ScreenDensity("mdpi", 1f, true);
        }
        if (density == HDPI) {
            return new ScreenDensity("hdpi", 1.5f, true);
        }
        if (density == XHDPI) {
            return new ScreenDensity("xhdpi", 2f, true);
        }
        throw new UnsupportedDensityException();
    }

    public static List<ScreenDensity> getSupportedScreenDensity() {
        return list;
    }

    public static ScreenDensity getDefaultInputDensity() {
        return defaultInputDensity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ScreenDensity) {
            return this.name.equals(((ScreenDensity) obj).getName());
        }
        return false;
    }

    public boolean isActive() {
        return active;
    }
}
