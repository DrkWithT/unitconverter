package com.derktee;

/**
 * Unit Converter
 * @author Derek Tan
 * @version 0.1.0
 */

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class App extends JFrame implements ActionListener {
  // Constants
  private static final int WINDOW_WIDTH = 320;
  private static final int WINDOW_HEIGHT = 320;
  private static final String WINDOW_TITLE = "UnitConverter";

  private static final int UNIT_COUNT = 9;
  private static final String DEFAULT_UNIT = "m";
  private static final String[] UNIT_NAMES = {"m", "km", "dm", "cm", "mm", "ft", "mi", "yd", "in"};

  // state
  private boolean[] menuUsageFlags;

  // GUI
  private JLabel sourceUnitLabel;
  private JLabel targetUnitLabel;
  private JTextField sourceUnitField;
  private JTextField targetUnitField;

  private JMenuBar appMenus;

  private JMenu actionMenu;
  private JMenuItem calcItem;
  private JMenuItem resetItem;

  private JMenu sourceUnitMenu;
  private JMenuItem[] sourceUnitItems;
  
  private JMenu targetUnitMenu;
  private JMenuItem[] targetUnitItems;

  // others
  private DecimalFormat numFormatter;
  private LengthConverter unitConverter;

  public App() {
    // initialize state
    menuUsageFlags = new boolean[2];
    menuUsageFlags[0] = false;
    menuUsageFlags[1] = false;

    // initialize form components
    sourceUnitLabel = new JLabel("m");
    sourceUnitField = new JTextField("0.0");
    targetUnitLabel = new JLabel("m");
    targetUnitField = new JTextField("0.0");
    targetUnitField.setEditable(false);

    // initialize menus
    appMenus = new JMenuBar();

    actionMenu = new JMenu("Actions");
    calcItem = new JMenuItem("Convert");
    resetItem = new JMenuItem("Clear");
    
    actionMenu.add(calcItem);
    actionMenu.add(resetItem);

    sourceUnitMenu = new JMenu("Start Unit");
    targetUnitMenu = new JMenu("Result Unit");

    sourceUnitItems = new JMenuItem[UNIT_COUNT];
    targetUnitItems = new JMenuItem[UNIT_COUNT];

    for(int i = 0; i < UNIT_COUNT; i++) {
      sourceUnitItems[i] = new JMenuItem(UNIT_NAMES[i]);
      sourceUnitMenu.add(sourceUnitItems[i]);

      targetUnitItems[i] = new JMenuItem(UNIT_NAMES[i]);
      targetUnitMenu.add(targetUnitItems[i]);
    }

    // initialize menu bar
    appMenus.add(actionMenu);
    appMenus.add(sourceUnitMenu);
    appMenus.add(targetUnitMenu);

    // initialize other objects
    numFormatter = new DecimalFormat(".0000");
    unitConverter = new LengthConverter(DEFAULT_UNIT);

    setupApp();
  }

  private void setupApp() {
    // 1a. populate and setup GUI layout
    setJMenuBar(appMenus);
    
    add(sourceUnitLabel);
    add(sourceUnitField);
    add(targetUnitLabel);
    add(targetUnitField);
    
    setLayout(new GridLayout(2, 2, 10, 10));

    // 1b. setup listeners
    calcItem.addActionListener(this);
    resetItem.addActionListener(this);

    for(int i = 0; i < UNIT_COUNT; i++) {
      sourceUnitItems[i].addActionListener(this);
      targetUnitItems[i].addActionListener(this);
    }

    // 2. prepare window
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    setTitle(WINDOW_TITLE);
  }

  public void showWindow() {
    setVisible(true);
  }

  private boolean isUsingUnitMenu(int menuNumber) {
    if (menuNumber >= 0 && menuNumber < menuUsageFlags.length)
      return menuUsageFlags[menuNumber];
    
    return false;
  }

  private void toggleUnitMenuFlag(int menuNumber) {
    if (menuNumber >= 0 && menuNumber < menuUsageFlags.length)
      menuUsageFlags[menuNumber] = !menuUsageFlags[menuNumber];
  }

  /**
   * This is a helper method for validation prior to calculations!
   * @implSpec The inputs must be non-negative decimal literals.
   */
  private boolean areValuesValid() {
    return sourceUnitField.getText().charAt(0) != '-' && targetUnitField.getText().charAt(0) != '-';
  }

  private void convertValues() throws NumberFormatException, Exception {
    unitConverter.setUnits(sourceUnitLabel.getText(), targetUnitLabel.getText());
    
    if (!areValuesValid())
      throw new Exception("Invalid lengths. Cannot have negatives.");

    double sourceValue = Double.parseDouble(sourceUnitField.getText());
    
    unitConverter.setStartValue(sourceValue);

    double result = unitConverter.getConversion();
    targetUnitField.setText(numFormatter.format(result));
  }

  private void resetValues() {
    // reset converter state
    unitConverter.defaultData();

    // reset form
    sourceUnitLabel.setText("m");
    sourceUnitField.setText("0.0");
    targetUnitLabel.setText("m");
    targetUnitField.setText("0.0");
  }

  public void actionPerformed(ActionEvent e) {
    Object eventTarget = e.getSource();
    boolean shouldReset = false;

    try {
      if (eventTarget == calcItem)
        convertValues();
      else if (eventTarget == resetItem)
        resetValues();
      else if (eventTarget.getClass().getName() == "javax.swing.JMenuItem")
        ;
    } catch (NumberFormatException formatEx) {
      System.err.println(formatEx.getMessage());
      shouldReset = true;

    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      shouldReset = true;

    } finally {
      if (shouldReset)
        resetValues(); // clear bad inputs on any error within conversion and formatting!
    }
  }

  public static void main( String[] args ) {
    App application = new App();
    application.showWindow();
  }
}
