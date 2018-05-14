package TSPSimulator.Components;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends Panel {
    public HeaderPanel(String headerText, Component ... components) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel header = new JLabel(headerText);
        header.setFont(new Font("Serif", Font.BOLD, 20));
        add(header);
        for (Component c : components) {
            add(c);
        }
    }
}
