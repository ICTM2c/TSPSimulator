package TSPSimulator.Components;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends Panel {
    public static Panel fillPanelWithComponents(Component... components) {
        Panel subPanel = new Panel();
        subPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        subPanel.setMinimumSize(new Dimension(0, 0));
        for (Component c : components) {
            subPanel.add(c);
        }
        return subPanel;
    }

    public HeaderPanel(String headerText, JButton btn, Component ... components) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel header = new JLabel(headerText);
        header.setFont(new Font("Serif", Font.BOLD, 20));
        add(header);

        if (components.length != 0) {
            add(fillPanelWithComponents(components));
        }

        if (btn != null) {
            add(btn);
        }
    }
}
