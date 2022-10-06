package com.derktee;

import java.awt.FlowLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A simple informational dialog for my unit converter. Only use on wrong user input.
 */
public class AppAlert extends JDialog {
  private static int DEFAULT_WIDTH = 250;
  private static int DEFAULT_HEIGHT = 250;

  private static String DEFAULT_ALERT_TITLE = "Alert";
  private static String DEFAULT_ALERT_MSG = "No exceptions!"; // fallback alert message for testing?

  private String alertMessage;

  private JLabel alertText;

  public AppAlert(JFrame owner) {
    super(owner, DEFAULT_ALERT_TITLE, true);
    alertMessage = DEFAULT_ALERT_MSG;
    alertText = new JLabel(alertMessage);

    // setup GUI content for this dialog
    setLayout(new FlowLayout());

    getContentPane().add(alertText);

    // setup alert window's appearance
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    setResizable(false);
  }

  public void setAlertMsg(String msg) {
    if (!msg.isEmpty()) {
      alertMessage = msg;
      alertText.setText(alertMessage);
    }
  }

  public String getAlertMsg() {
    return alertMessage;
  }
}
