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

  private static final int UNIT_MENU_1 = 0;
  private static final int UNIT_MENU_2 = 1;

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
    menuUsageFlags[UNIT_MENU_1] = false;
    menuUsageFlags[UNIT_MENU_2] = false;

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

  private void resetUnitMenuFlags() {
    menuUsageFlags[0] = false;
    menuUsageFlags[1] = false;
  }

  private String findMenuItemText(int menuNumber, JMenuItem unitItem) {
    String sourceText = unitItem.getText();
    String targetText = "";

    if (menuNumber == UNIT_MENU_1) {
      for (int i = 0; i < UNIT_COUNT; i++) {
        targetText = sourceUnitItems[i].getText();

        if (targetText == sourceText)
          break;
      }
    } else if (menuNumber == UNIT_MENU_2) {
      for (int i = 0; i < UNIT_COUNT; i++) {
        targetText = targetUnitItems[i].getText();
        
        if (targetText == sourceText)
          break;
      }
    }

    return targetText;
  }

  private void updateUnitLabel(JMenuItem unitItem) {
    if (isUsingUnitMenu(UNIT_MENU_1)) {
      sourceUnitLabel.setText(findMenuItemText(UNIT_MENU_1, unitItem));
    } else if (isUsingUnitMenu(UNIT_MENU_2)) {
      targetUnitLabel.setText(findMenuItemText(UNIT_MENU_2, unitItem));
    }

    resetUnitMenuFlags();
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
      else if (eventTarget == sourceUnitMenu)
        toggleUnitMenuFlag(UNIT_MENU_1);
      else if (eventTarget == targetUnitMenu)
        toggleUnitMenuFlag(UNIT_MENU_2);
      else if (eventTarget.getClass().getName() == "javax.swing.JMenuItem")
        updateUnitLabel(((JMenuItem)eventTarget));
      else
        resetUnitMenuFlags();
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
