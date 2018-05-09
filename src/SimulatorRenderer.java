import Simulators.Simulator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class SimulatorRenderer extends JLabel implements ListCellRenderer<Simulator> {
    private java.util.List<Color> _colors;

    public void setColors(java.util.List<Color> colors) {
        _colors = colors;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Simulator> list, Simulator value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        java.util.List<Simulator> selectedSimulators = (List<Simulator>) list.getSelectedValuesList();
        if (selectedSimulators.contains(value)) {
            this.setBackground(_colors.get(selectedSimulators.indexOf(value)));
            setForeground(_colors.get(selectedSimulators.indexOf(value)));
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }
        return this;
    }
}
