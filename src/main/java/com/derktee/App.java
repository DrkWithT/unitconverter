package com.derktee;

/**
 * App.java
 * Unit Converter
 * @author Derek Tan
 * @version 0.1.10 See README.md for history of changes.
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class App extends JFrame implements ActionListener {
  /// Constants
  private static final int WINDOW_WIDTH = 320;
  private static final int WINDOW_HEIGHT = 320;
  private static final String WINDOW_TITLE = "UnitConverter";

  private static final int UNIT_COUNT = 9;
  private static final String DEFAULT_UNIT = "m";
  private static final String[] UNIT_NAMES = {"m", "km", "dm", "cm", "mm", "ft", "mi", "yd", "in"};

  private static final String CALC_BTN_LABEL = "calcbtn";
  private static final String RESET_BTN_LABEL = "resetbtn";
  private static final String SOURCE_UNIT_LABEL = "sourceunit";
  private static final String TARGET_UNIT_LABEL = "targetunit";

  private static final int SOURCE_UNIT_MENU = 0;
  private static final int TARGET_UNIT_MENU = 1;
  private static final int OTHER_MENU = 2;

  /// GUI
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

  /// Other
  private LengthConverter unitConverter;

  /**
   * Initializes the application data.
   */
  public App() {
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
    calcItem.setName(CALC_BTN_LABEL);

    resetItem = new JMenuItem("Clear");
    resetItem.setName(RESET_BTN_LABEL);

    actionMenu.add(calcItem);
    actionMenu.add(resetItem);

    sourceUnitMenu = new JMenu("Start Unit");
    targetUnitMenu = new JMenu("Result Unit");

    sourceUnitItems = new JMenuItem[UNIT_COUNT];
    targetUnitItems = new JMenuItem[UNIT_COUNT];

    for(int i = 0; i < UNIT_COUNT; i++) {
      // create named menu items to discern the calculate / clear vs. unit JMenuItems!
      sourceUnitItems[i] = new JMenuItem(UNIT_NAMES[i]);
      sourceUnitItems[i].setName(SOURCE_UNIT_LABEL);
      sourceUnitMenu.add(sourceUnitItems[i]);

      targetUnitItems[i] = new JMenuItem(UNIT_NAMES[i]);
      targetUnitItems[i].setName(TARGET_UNIT_LABEL);
      targetUnitMenu.add(targetUnitItems[i]);
    }

    // initialize menu bar
    appMenus.add(actionMenu);
    appMenus.add(sourceUnitMenu);
    appMenus.add(targetUnitMenu);

    // initialize other objects
    unitConverter = new LengthConverter(DEFAULT_UNIT);

    setupApp();
  }

  /**
   * Sets up the application's GUI.
   */
  private void setupApp() {
    // 1a. setup GUI layout: put menus and form
    setJMenuBar(appMenus);
    
    add(sourceUnitLabel);
    add(sourceUnitField);
    add(targetUnitLabel);
    add(targetUnitField);
    
    setLayout(new GridLayout(2, 2, 10, 10));

    // 1b. setup listeners: add menu event handlers
    actionMenu.addActionListener(this);
    sourceUnitMenu.addActionListener(this);
    targetUnitMenu.addActionListener(this);

    calcItem.addActionListener(this);
    resetItem.addActionListener(this);

    for(int i = 0; i < UNIT_COUNT; i++) {
      sourceUnitItems[i].addActionListener(this);
      targetUnitItems[i].addActionListener(this);
    }

    // 2. prepare window: closing operation and appearance
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    setTitle(WINDOW_TITLE);
  }

  public void showWindow() {
    setVisible(true);
  }

  /**
   * This is a helper method for validation prior to calculations!
   * @note The inputs must be non-negative decimal literals.
   */
  private boolean areValuesValid() {
    return sourceUnitField.getText().charAt(0) != '-' && targetUnitField.getText().charAt(0) != '-';
  }

  /**
   * Invokes the LengthConverter object within App to convert units.
   * @throws NumberFormatException Thrown previously from <code>LengthConverter.getConversion()</code>.
   * @throws Exception A general runtime error.
   */
  private void convertValues() throws NumberFormatException, Exception {
    if (!areValuesValid())
      throw new Exception("Invalid lengths. Cannot have negatives.");

    unitConverter.setUnits(sourceUnitLabel.getText(), targetUnitLabel.getText());

    double sourceValue = Double.parseDouble(sourceUnitField.getText());
    unitConverter.setStartValue(sourceValue);

    double result = unitConverter.getConversion();
    targetUnitField.setText(String.format("%.3f", result));

    unitConverter.defaultData();
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

  /**
   * Returns the active menu based on the name attributes of any clicked JMenuItem.
   * @param item The focused <code>JMenuItem</code>.
   * @return An int for the menu code. See "Constants" in <strong>App.java</strong>.
   */
  private int getMenuUsed(JMenuItem item) {
    String componentName = item.getName();

    if (componentName.compareTo(SOURCE_UNIT_LABEL) == 0)
      return SOURCE_UNIT_MENU;
    else if (componentName.compareTo(TARGET_UNIT_LABEL) == 0) 
      return TARGET_UNIT_MENU;

    return OTHER_MENU;
  }

  /**
   * Updates the initial or target unit for the conversion based on which menu is active.
   * @param item The focused <code>JMenuItem</code>.
   */
  private void updateUnitData(JMenuItem item) {
    switch (getMenuUsed(item)) {
      case SOURCE_UNIT_MENU:
        sourceUnitLabel.setText(item.getText());
        break;
      case TARGET_UNIT_MENU:
        targetUnitLabel.setText(item.getText());
        break;
      case OTHER_MENU:
      default:
        break;
    }
  }

  /**
   * The main helper method to handle the calculate, reset, and unit menu item uses.
   * @param target The currently focused menu item.
   * @throws NumberFormatException From method <code>convertValues()</code>.
   * @throws Exception A general runtimne error.
   */
  public void handleMenuItemUse(JMenuItem target) throws NumberFormatException, Exception {
    if (target == calcItem)
      convertValues();
    else if (target == resetItem)
      resetValues();
    else
      updateUnitData(target);
  }

  public void actionPerformed(ActionEvent e) {
    Object eventTarget = e.getSource();
    boolean shouldReset = false;

    try {
      if (eventTarget instanceof JMenuItem)
        handleMenuItemUse((JMenuItem)eventTarget);
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
