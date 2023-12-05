package Server;

import java.awt.*;

public class GBCBuild {
    private GridBagConstraints gbc;

    public GBCBuild(int gridx, int gridy) {
        gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
    }

    public GBCBuild setInsets(int insets) {
        gbc.insets = new Insets(insets, insets, insets, insets);
        return this;
    }

    public GBCBuild setFill(int fill) {
        gbc.fill = fill;
        return this;
    }

    public GBCBuild setWeight(int weightx, int weighty) {
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        return this;
    }

    public GBCBuild setSpan(int gridwidth, int gridheight) {
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        return this;
    }

    public GridBagConstraints build() {
        return gbc;
    }
}
