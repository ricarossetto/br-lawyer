/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.enrichment;

import com.jdimension.jlawyer.client.settings.ClientSettings;
import com.jdimension.jlawyer.client.utils.ComponentUtils;
import com.jdimension.jlawyer.client.utils.FrameUtils;
import com.jdimension.jlawyer.client.utils.StringUtils;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;
import com.jdimension.jlawyer.services.BrazilianDataEnrichmentServiceRemote;
import com.jdimension.jlawyer.services.JLawyerServiceLocator;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

/**
 * Diálogo de administração e configuração dos provedores de enriquecimento de dados
 * cadastrais brasileiros (BrasilAPI, ViaCEP, SERPRO, BACEN, CNA/OAB).
 *
 * Permite aos administradores testar a conectividade em tempo real, verificar latência,
 * gerenciar credenciais de acesso de forma segura e definir políticas de fallback.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianIntegrationsConfigDialog extends JDialog {

    private static final Logger log = Logger.getLogger(BrazilianIntegrationsConfigDialog.class.getName());
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com/jdimension/jlawyer/client/enrichment/BrazilianIntegrationsConfigDialog");

    private List<ProviderConfig> providerConfigs = new ArrayList<>();
    private ProviderConfig selectedConfig = null;

    // UI components
    private JTable tblProviders;
    private ProviderTableModel tableModel;

    private JTextField txtProviderId;
    private JTextField txtDisplayName;
    private JCheckBox chkEnabled;
    private JSpinner spnPriority;
    private JTextField txtBaseUrl;
    private JTextField txtApiKey;
    private JPasswordField txtApiSecret;
    private JCheckBox chkShowSecret;
    private JSpinner spnTimeout;
    private JSpinner spnCacheTtl;
    private JSpinner spnMaxRetries;

    private JLabel lblTestResult;
    private JButton btnTestConnection;
    private JButton btnSave;
    private JButton btnRefresh;
    private JButton btnClose;

    public BrazilianIntegrationsConfigDialog(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initComponents();
        loadProviders();
        ComponentUtils.restoreDialogSize(this);
    }

    private void initComponents() {
        setTitle(bundle.getString("dialog.title"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Header Panel
        JPanel pnlHeader = new JPanel(new BorderLayout(5, 5));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 12, 5, 12));

        JLabel lblTitle = new JLabel("<html><b style='font-size:13px;'>" + bundle.getString("header.title") + "</b></html>");
        lblTitle.setIcon(new ImageIcon(getClass().getResource("/icons/database.png")));
        pnlHeader.add(lblTitle, BorderLayout.NORTH);

        JLabel lblDesc = new JLabel("<html><span style='color:#555;font-size:11px;'>" + bundle.getString("header.description") + "</span></html>");
        pnlHeader.add(lblDesc, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // Center Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(340);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Left Panel: Providers Table
        JPanel pnlLeft = new JPanel(new BorderLayout(5, 5));
        pnlLeft.setBorder(BorderFactory.createTitledBorder(bundle.getString("table.col.provider")));

        tableModel = new ProviderTableModel();
        tblProviders = new JTable(tableModel);
        tblProviders.setRowHeight(24);
        tblProviders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProviders.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblProviders.getSelectedRow();
                if (row >= 0 && row < providerConfigs.size()) {
                    selectProvider(providerConfigs.get(row));
                }
            }
        });

        tblProviders.getColumnModel().getColumn(0).setPreferredWidth(170);
        tblProviders.getColumnModel().getColumn(1).setPreferredWidth(55);
        tblProviders.getColumnModel().getColumn(2).setPreferredWidth(65);

        tblProviders.setDefaultRenderer(Object.class, new ProviderCellRenderer());

        pnlLeft.add(new JScrollPane(tblProviders), BorderLayout.CENTER);
        splitPane.setLeftComponent(pnlLeft);

        // Right Panel: Provider Details
        JPanel pnlRight = buildDetailsPanel();
        splitPane.setRightComponent(pnlRight);

        add(splitPane, BorderLayout.CENTER);

        // Bottom Button Bar
        JPanel pnlBottom = new JPanel(new BorderLayout(5, 5));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JPanel pnlBottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRefresh = new JButton(bundle.getString("button.refresh"));
        btnRefresh.setIcon(new ImageIcon(getClass().getResource("/icons/find.png")));
        btnRefresh.addActionListener(e -> loadProviders());
        pnlBottomLeft.add(btnRefresh);
        pnlBottom.add(pnlBottomLeft, BorderLayout.WEST);

        JPanel pnlBottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));

        btnTestConnection = new JButton(bundle.getString("button.testConnection"));
        btnTestConnection.setIcon(new ImageIcon(getClass().getResource("/icons/find.png")));
        btnTestConnection.setEnabled(false);
        btnTestConnection.addActionListener(e -> onTestConnectionClicked());
        pnlBottomRight.add(btnTestConnection);

        btnSave = new JButton(bundle.getString("button.save"));
        btnSave.setIcon(new ImageIcon(getClass().getResource("/icons/filesave.png")));
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD));
        btnSave.setEnabled(false);
        btnSave.addActionListener(e -> onSaveClicked());
        pnlBottomRight.add(btnSave);

        btnClose = new JButton(bundle.getString("button.close"));
        btnClose.addActionListener(e -> dispose());
        pnlBottomRight.add(btnClose);

        pnlBottom.add(pnlBottomRight, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);

        // Keybindings
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        setSize(new Dimension(860, 560));
        setMinimumSize(new Dimension(720, 460));
        FrameUtils.centerDialog(this, getOwner());
    }

    private JPanel buildDetailsPanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createTitledBorder(bundle.getString("panel.details")));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Provider ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.providerId")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        txtProviderId = new JTextField();
        txtProviderId.setEditable(false);
        txtProviderId.setBackground(new Color(245, 245, 245));
        form.add(txtProviderId, gbc);

        // Display Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.displayName")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        txtDisplayName = new JTextField();
        form.add(txtDisplayName, gbc);

        // Enabled
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.enabled")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        chkEnabled = new JCheckBox();
        form.add(chkEnabled, gbc);

        // Priority
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.priority")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        spnPriority = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spnPriority.setPreferredSize(new Dimension(80, 24));
        JPanel pnlPrio = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlPrio.add(spnPriority);
        form.add(pnlPrio, gbc);

        // Base URL
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.baseUrl")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        txtBaseUrl = new JTextField();
        form.add(txtBaseUrl, gbc);

        // API Key
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.apiKey")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        txtApiKey = new JTextField();
        form.add(txtApiKey, gbc);

        // API Secret
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.apiSecret")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        txtApiSecret = new JPasswordField();
        chkShowSecret = new JCheckBox("👁");
        chkShowSecret.setToolTipText("Mostrar/Ocultar segredo");
        chkShowSecret.addActionListener(e -> {
            if (chkShowSecret.isSelected()) {
                txtApiSecret.setEchoChar((char) 0);
            } else {
                txtApiSecret.setEchoChar('•');
            }
        });
        JPanel pnlSecret = new JPanel(new BorderLayout(4, 0));
        pnlSecret.add(txtApiSecret, BorderLayout.CENTER);
        pnlSecret.add(chkShowSecret, BorderLayout.EAST);
        form.add(pnlSecret, gbc);

        // Timeout (ms)
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.timeoutMs")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        spnTimeout = new JSpinner(new SpinnerNumberModel(5000, 500, 60000, 500));
        spnTimeout.setPreferredSize(new Dimension(100, 24));
        JPanel pnlTimeout = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTimeout.add(spnTimeout);
        form.add(pnlTimeout, gbc);

        // Cache TTL (minutos)
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.cacheTtl")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        spnCacheTtl = new JSpinner(new SpinnerNumberModel(1440, 0, 43200, 60));
        spnCacheTtl.setPreferredSize(new Dimension(100, 24));
        JPanel pnlTtl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTtl.add(spnCacheTtl);
        form.add(pnlTtl, gbc);

        // Max Retries
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        form.add(new JLabel(bundle.getString("label.maxRetries")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        spnMaxRetries = new JSpinner(new SpinnerNumberModel(2, 0, 5, 1));
        spnMaxRetries.setPreferredSize(new Dimension(80, 24));
        JPanel pnlRetries = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlRetries.add(spnMaxRetries);
        form.add(pnlRetries, gbc);

        // Spacer
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0; gbc.gridwidth = 2;
        form.add(new JPanel(), gbc);

        p.add(new JScrollPane(form), BorderLayout.CENTER);

        // Test Status Result Banner at Bottom
        lblTestResult = new JLabel(" ");
        lblTestResult.setFont(lblTestResult.getFont().deriveFont(Font.BOLD, 11f));
        lblTestResult.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        p.add(lblTestResult, BorderLayout.SOUTH);

        return p;
    }

    private void loadProviders() {
        btnRefresh.setEnabled(false);
        SwingWorker<List<ProviderConfig>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProviderConfig> doInBackground() throws Exception {
                BrazilianDataEnrichmentServiceRemote svc = JLawyerServiceLocator.getInstance(
                        ClientSettings.getInstance().getLookupProperties()
                ).lookupBrazilianDataEnrichmentServiceRemote();
                return svc.getProviderConfigs();
            }

            @Override
            protected void done() {
                btnRefresh.setEnabled(true);
                try {
                    providerConfigs = get();
                    if (providerConfigs == null) providerConfigs = new ArrayList<>();
                    tableModel.fireTableDataChanged();
                    if (!providerConfigs.isEmpty()) {
                        tblProviders.setRowSelectionInterval(0, 0);
                        selectProvider(providerConfigs.get(0));
                    }
                } catch (Exception ex) {
                    log.error("Erro ao carregar provedores", ex);
                    JOptionPane.showMessageDialog(BrazilianIntegrationsConfigDialog.this,
                            bundle.getString("msg.loadError") + "\n" + ex.getMessage(),
                            getTitle(), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void selectProvider(ProviderConfig config) {
        this.selectedConfig = config;
        lblTestResult.setText(" ");

        if (config == null) {
            txtProviderId.setText("");
            txtDisplayName.setText("");
            chkEnabled.setSelected(false);
            spnPriority.setValue(1);
            txtBaseUrl.setText("");
            txtApiKey.setText("");
            txtApiSecret.setText("");
            spnTimeout.setValue(5000);
            spnCacheTtl.setValue(1440);
            spnMaxRetries.setValue(2);
            btnTestConnection.setEnabled(false);
            btnSave.setEnabled(false);
            return;
        }

        txtProviderId.setText(config.getProviderId());
        txtDisplayName.setText(config.getDisplayName());
        chkEnabled.setSelected(config.isEnabled());
        spnPriority.setValue(config.getPriority());
        txtBaseUrl.setText(StringUtils.nonEmpty(config.getBaseUrl()));
        txtApiKey.setText(StringUtils.nonEmpty(config.getApiKey()));
        txtApiSecret.setText(StringUtils.nonEmpty(config.getApiSecret()));
        spnTimeout.setValue(config.getTimeoutMs() > 0 ? config.getTimeoutMs() : 5000);
        spnCacheTtl.setValue(config.getCacheTtlMinutes() >= 0 ? config.getCacheTtlMinutes() : 1440);
        spnMaxRetries.setValue(config.getMaxRetries() >= 0 ? config.getMaxRetries() : 2);

        btnTestConnection.setEnabled(true);
        btnSave.setEnabled(true);
    }

    private void onTestConnectionClicked() {
        if (selectedConfig == null) return;

        btnTestConnection.setEnabled(false);
        btnTestConnection.setText(bundle.getString("button.testing"));
        lblTestResult.setForeground(Color.DARK_GRAY);
        lblTestResult.setText("Testando conectividade com " + selectedConfig.getDisplayName() + "...");

        final String providerId = selectedConfig.getProviderId();
        final long start = System.currentTimeMillis();

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                BrazilianDataEnrichmentServiceRemote svc = JLawyerServiceLocator.getInstance(
                        ClientSettings.getInstance().getLookupProperties()
                ).lookupBrazilianDataEnrichmentServiceRemote();
                return svc.testProvider(providerId);
            }

            @Override
            protected void done() {
                btnTestConnection.setEnabled(true);
                btnTestConnection.setText(bundle.getString("button.testConnection"));
                long latency = System.currentTimeMillis() - start;

                try {
                    boolean success = get();
                    if (success) {
                        lblTestResult.setForeground(new Color(0, 130, 0));
                        String msg = MessageFormat.format(bundle.getString("msg.testSuccess"), latency);
                        lblTestResult.setText("✓ " + msg);
                    } else {
                        lblTestResult.setForeground(new Color(180, 0, 0));
                        String msg = MessageFormat.format(bundle.getString("msg.testFailed"), "Provedor retornou status inoperante.");
                        lblTestResult.setText("✗ " + msg);
                    }
                } catch (Exception ex) {
                    log.error("Erro no teste de conexão do provedor", ex);
                    lblTestResult.setForeground(new Color(180, 0, 0));
                    String msg = MessageFormat.format(bundle.getString("msg.testFailed"), ex.getMessage());
                    lblTestResult.setText("✗ " + msg);
                }
            }
        };

        worker.execute();
    }

    private void onSaveClicked() {
        if (selectedConfig == null) return;

        selectedConfig.setDisplayName(txtDisplayName.getText());
        selectedConfig.setEnabled(chkEnabled.isSelected());
        selectedConfig.setPriority((Integer) spnPriority.getValue());
        selectedConfig.setBaseUrl(txtBaseUrl.getText());
        selectedConfig.setApiKey(txtApiKey.getText());
        selectedConfig.setApiSecret(new String(txtApiSecret.getPassword()));
        selectedConfig.setTimeoutMs((Integer) spnTimeout.getValue());
        selectedConfig.setCacheTtlMinutes((Integer) spnCacheTtl.getValue());
        selectedConfig.setMaxRetries((Integer) spnMaxRetries.getValue());

        btnSave.setEnabled(false);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                BrazilianDataEnrichmentServiceRemote svc = JLawyerServiceLocator.getInstance(
                        ClientSettings.getInstance().getLookupProperties()
                ).lookupBrazilianDataEnrichmentServiceRemote();
                svc.saveProviderConfig(selectedConfig);
                return null;
            }

            @Override
            protected void done() {
                btnSave.setEnabled(true);
                try {
                    get();
                    tableModel.fireTableDataChanged();
                    JOptionPane.showMessageDialog(BrazilianIntegrationsConfigDialog.this,
                            bundle.getString("msg.saveSuccess"), getTitle(), JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    log.error("Erro ao salvar configuração de provedor", ex);
                    JOptionPane.showMessageDialog(BrazilianIntegrationsConfigDialog.this,
                            bundle.getString("msg.saveError") + "\n" + ex.getMessage(),
                            getTitle(), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private class ProviderTableModel extends DefaultTableModel {
        private final String[] cols = new String[]{
                bundle.getString("table.col.provider"),
                bundle.getString("table.col.enabled"),
                bundle.getString("table.col.priority")
        };

        @Override
        public int getRowCount() {
            return providerConfigs != null ? providerConfigs.size() : 0;
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int column) {
            return cols[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1) return Boolean.class;
            if (columnIndex == 2) return Integer.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= providerConfigs.size()) return null;
            ProviderConfig cfg = providerConfigs.get(rowIndex);
            switch (columnIndex) {
                case 0: return cfg.getDisplayName() != null ? cfg.getDisplayName() : cfg.getProviderId();
                case 1: return cfg.isEnabled();
                case 2: return cfg.getPriority();
                default: return null;
            }
        }
    }

    private static class ProviderCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(new Color(250, 250, 250));
                } else {
                    c.setBackground(Color.WHITE);
                }
            }
            return c;
        }
    }
}
