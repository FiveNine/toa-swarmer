package com.swarmer;

import java.awt.datatransfer.StringSelection;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;


public class SwarmerSidePanel extends PluginPanel
{
    private final Client client;
    private final SwarmerPlugin plugin;
    private final SwarmerConfig config;

    private final Color textColor = Color.WHITE;
    private final Color sidePanelColor = new Color(0x282828);
    private final Color backgroundColor = new Color(0x161616);
    private final Color tableColor1 = new Color(0x1F1F1F);
    private final Color tableColor2 = new Color(0x2D2D2D);

    Font tableTitleFont;
    Font tableFont;

    JTable statsTable;

    JTextArea textArea;

    private DefaultTableModel leaksTableModel;
    private JList<String> raidsList;

    @Inject
    SwarmerSidePanel(Client client, SwarmerPlugin plugin, SwarmerConfig config)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.tableTitleFont = new Font(SwarmerFonts.REGULAR.toString(), Font.PLAIN, 18);
        this.tableFont = new Font(SwarmerFonts.VERDANA.toString(), Font.PLAIN, 12);
    }

    public void createSidePanel()
    {
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);
        setLayout(new BorderLayout());

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));  // Vertical alignment
        mainPanel.setBackground(sidePanelColor);
        mainPanel.setForeground(textColor);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Recent Raids Panel
        JPanel recentRaidsPanel = new JPanel();
        recentRaidsPanel.setLayout(new BoxLayout(recentRaidsPanel, BoxLayout.Y_AXIS));
        recentRaidsPanel.setBackground(backgroundColor);

        JLabel recentRaidsLabel = new JLabel("Recent Raids");
        recentRaidsLabel.setBackground(backgroundColor);
        recentRaidsLabel.setForeground(textColor);
        recentRaidsLabel.setFont(tableTitleFont);
        recentRaidsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        recentRaidsLabel.setPreferredSize(new Dimension(150, 20));

        recentRaidsPanel.add(recentRaidsLabel);



        String[] recentRaids = getRecentRaids();
        raidsList = new JList<>(recentRaids);
        raidsList.setBackground(tableColor1);
        raidsList.setForeground(textColor);
        raidsList.setFont(tableFont);
        raidsList.setSelectionBackground(tableColor2);
        raidsList.setSelectionForeground(textColor);
        raidsList.setSelectedIndex(0);
        raidsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedRaid = raidsList.getSelectedValue();
                if (selectedRaid != null) {
                    loadRaidData(selectedRaid);
                }
            }
        });


        raidsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });

        recentRaidsPanel.add(new JScrollPane(raidsList));
        mainPanel.add(recentRaidsPanel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Leaks Table Panel
        JPanel leaksPanel = new JPanel();
        leaksPanel.setLayout(new BoxLayout(leaksPanel, BoxLayout.Y_AXIS));
        leaksPanel.setBackground(backgroundColor);

        JLabel leaksLabel = new JLabel("Leaks");
        leaksLabel.setBackground(backgroundColor);
        leaksLabel.setForeground(textColor);
        leaksLabel.setFont(tableTitleFont);
        leaksLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leaksLabel.setPreferredSize(new Dimension(150, 20));
        leaksPanel.add(leaksLabel);

        String[] columnNames = { "Down", "Wave", "Leaks" };
        Object[][] data = {
                { "1", "01", "0" },
                { "1", "02", "0" },
                { "1", "03", "0" },
                { "1", "04", "0" },
        };

        // Create a non-editable table model
        this.leaksTableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loadRaidData(raidsList.getSelectedValue());

        JTable leaksTable = new JTable(leaksTableModel);
        leaksTable.setRowSelectionAllowed(false);
        leaksTable.setColumnSelectionAllowed(false);
        leaksTable.setCellSelectionEnabled(false);
        leaksTable.setBackground(backgroundColor);
        leaksTable.setForeground(textColor);
        leaksTable.setGridColor(backgroundColor);
        leaksTable.setFont(tableFont);

        // Alternate row colors
        leaksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? tableColor1 : tableColor2);
                c.setForeground(textColor);
                return c;
            }
        });

        // Create a custom cell renderer to center the text
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Apply the renderer to each column
        for (int i = 0; i < leaksTable.getColumnCount(); i++) {
            leaksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane leaksScrollPane = new JScrollPane(leaksTable);
        leaksScrollPane.setPreferredSize(new Dimension(100, 400));

        leaksPanel.add(leaksScrollPane);
        mainPanel.add(leaksPanel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }


    public void setText(String text)
    {
//        textArea.setText(text);
    }
    public String getText()
    {
        return textArea.getText();
    }

    public void loadRaidData(String raid) {
        List<RaidData> raidDataList = DataHandler.getRaidData(raid);
        if (raidDataList == null) {
            return;
        }
        leaksTableModel.setRowCount(0); // Clear existing data
        for (RaidData raidData : raidDataList) {
            leaksTableModel.addRow(new Object[]{raidData.getDown(), raidData.getWave(), raidData.getLeaks()});
        }
    }

    public void updateRecentRaids() {
        raidsList.setListData(getRecentRaids());
    }

    private String[] getRecentRaids() {
        List<String> raids = DataHandler.getRaidList();
        if (raids == null) {
            return new String[0];
        }
        return raids.toArray(new String[0]);
    }

    private void _copyToClipboard(JTable table)
    {
        StringBuffer sbf = new StringBuffer();
        table.selectAll();
        int numCols = table.getSelectedColumnCount();
        int numRows = table.getSelectedRowCount();
        int[] selectedRows = table.getSelectedRows();
        int[] selectsColumns = table.getSelectedColumns();
        table.clearSelection();
        for (int i = 0; i < numCols; i++)
        {
            sbf.append(statsTable.getModel().getColumnName(i));
            sbf.append("\t");
        }
        sbf.append("\n");
        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < numCols; j++)
            {
                sbf.append(table.getValueAt(selectedRows[i], selectsColumns[j]));
                if (j < numCols - 1)
                {
                    sbf.append("\t");
                }
            }
            sbf.append("\n");
        }
        StringSelection data = new StringSelection(sbf.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(data, data);
    }



}