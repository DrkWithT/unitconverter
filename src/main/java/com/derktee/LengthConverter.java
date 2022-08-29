package com.derktee;

/**
 * This object converts from a unit named value to another unit named value. Validation is done externally in the caller, <code>App</code>.
 * Methods of this object not only initialize its data, but also set units, determine units and conversion "directions", and do the calculations.
 * NOTE: Unit Relation Graph below!
 * km, dm, cm, mm <-> m <-> ft <-> mi, yd, in
 */

import java.util.HashMap;

public class LengthConverter {
  private static final double METERS_TO_FT = 3.28084; // factor to relate metric to imperial

  private String fallbackUnit; // default unit on reset
  private String startUnit;    // unit to convert from
  private String endUnit;      // unit to convert to
  private double startValue;   // initial measurement to convert

  /**
   * A mapping going from Metric meters to other metric lengths. The graph modeled by this represents conversion routes from meters to any other metric length unit.
   */
  private HashMap<String, Double> metricMap;

  /**
   * A mapping going from Imperial feet to any other imperial length. Note that to convert from metric to imperial or vice versa, cross conversions must use the <code>METERS_TO_FT</code> constant. Direction of the cross convert affects if the constant itself or its reciprocal is used.
   */
  private HashMap<String, Double> imperialMap;

  public LengthConverter(String defaultUnit) {
    // set preset unit for both conversion values
    fallbackUnit = defaultUnit;

    // default other fields
    defaultData();

    // initialize metric scale: based on meters
    metricMap = new HashMap<>();
    metricMap.put("m", 1.0);
    metricMap.put("km", 1000.0);
    metricMap.put("dm", 0.1);
    metricMap.put("cm", 0.01);
    metricMap.put("mm", 0.001);

    // initalize imperial scale: based on feet
    imperialMap = new HashMap<>();
    imperialMap.put("ft", 1.0);
    imperialMap.put("mi", 5280.0);
    imperialMap.put("yd", 3.0);
    imperialMap.put("in", 0.08333);
  }

  public void setUnits(String startUnitName, String endUnitName) {
    startUnit = startUnitName;
    endUnit = endUnitName;
  }

  public void setStartValue(double startVal) {
    startValue = startVal;
  }

  public void defaultData() {
    startUnit = fallbackUnit;
    endUnit = fallbackUnit;
    startValue = 0.0;
  }

  private double convImpToMetric() {
    double startFactor = imperialMap.get(startUnit);
    double endFactor = metricMap.get(endUnit);

    double toFeet = startValue * startFactor;
    double toMeters = toFeet * (1.0 / METERS_TO_FT);

    return toMeters / endFactor;
  }

  private double convMetricToImp() {
    double startFactor = metricMap.get(startUnit);
    double endFactor = imperialMap.get(endUnit);

    double toMeters = startValue * startFactor;
    double toFeet = toMeters * METERS_TO_FT;

    return toFeet / endFactor;
  }

  private double convertInMetric() {
    double startFactor = metricMap.get(startUnit);
    double endFactor = metricMap.get(endUnit);
    double toMeters = startValue * startFactor;

    if (startFactor < endFactor)
      return toMeters / endFactor;
    
    return toMeters * endFactor;
  }

  private double convertInImperial() {
    double startFactor = imperialMap.get(startUnit);
    double endFactor = imperialMap.get(endUnit);
    double toFeet = startValue * startFactor;

    if (startFactor < endFactor)
      return toFeet / endFactor;
    
    return toFeet * endFactor;
  }

  public double getConversion() {
    // check direction of conversion along the unit relations
    boolean startInImp = imperialMap.containsKey(startUnit);
    boolean endInImp = imperialMap.containsKey(endUnit);
    boolean startInMet = metricMap.containsKey(startUnit);
    boolean endInMet = metricMap.containsKey(endUnit);

    if (startInImp && endInMet) // conversion cases across systems
      return convImpToMetric();
    else if (startInMet && endInImp)
      return convMetricToImp();
    else if (startInImp && endInImp) // conversion cases within a system
      return convertInImperial();
    else if (startInMet && endInMet)
      return convertInMetric();
    
    return 0.0;
  }
}
