package speedith.core.reasoning.rules.test;

import speedith.core.lang.Region;
import speedith.core.lang.Zone;
import speedith.core.lang.util.RegionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HabitatBuilder {
  private final HashMap<String, Region> spiderHabitats = new HashMap<>();

  public Map<String, Region> get() {
    return spiderHabitats;
  }

  public HabitatBuilder addHabitat(String spider, ArrayList<Zone> habitat) {
    spiderHabitats.put(spider, new Region(habitat));
    return this;
  }

  public HabitatBuilder addHabitat(String spider, RegionBuilder region) {
    spiderHabitats.put(spider, region.get());
    return this;
  }

  public static HabitatBuilder emptyHabitat() {return new HabitatBuilder();}
}