/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.enrichment;

import com.jdimension.jlawyer.client.utils.ComponentUtils;
import com.jdimension.jlawyer.client.utils.FrameUtils;
import com.jdimension.jlawyer.client.utils.StringUtils;
import com.jdimension.jlawyer.domain.enrichment.model.AddressResult;
import com.jdimension.jlawyer.domain.enrichment.model.CompanyMemberResult;
import com.jdimension.jlawyer.domain.enrichment.model.CompanyRegistryResult;
import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import com.jdimension.jlawyer.persistence.AddressBean;
import com.jdimension.jlawyer.services.BrazilianDataEnrichmentServiceRemote;
import com.jdimension.jlawyer.services.JLawyerServiceLocator;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Diálogo visual para busca, visualização e enriquecimento de dados cadastrais
 * de pessoas jurídicas através do CNPJ, integrando fontes públicas e oficiais brasileiras.
 *
 * Permite preenchimento automático de campos empresariais, endereço, contatos e
 * importação do Quadro de Sócios e Administradores (QSA).
 *
 * @author BR-LAWYER Team
 */
public class CompanyEnrichmentDialog extends JDialog {

    private static final Logger log = Logger.getLogger(CompanyEnrichmentDialog.class.getName());
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com/jdimension/jlawyer/client/enrichment/CompanyEnrichmentDialog");

    private final AddressBean targetAddress;
    private CompanyRegistryResult currentResult;
    private boolean applied = false;
    private final List<CompanyMemberResult> selectedQsaToImport = new ArrayList<>();

    // UI Components
    private JTextField txtCnpjSearch;
    private JButton btnSearch;
    private JProgressBar progressBar;
    private JLabel lblStatus;

    // Metadata & Provenance
    private JLabel lblProvenanceInfo;

    // Tabs
    private JTabbedPane tabbedPane;

    // Company Tab Fields
    private JTextField txtRazaoSocial;
    private JTextField txtNomeFantasia;
    private JLabel lblSituacaoBadge;
    private JTextField txtDataSituacao;
    private JTextField txtMotivoSituacao;
    private JTextField txtNaturezaJuridica;
    private JTextField txtCapitalSocial;
    private JTextField txtPorte;
    private JTextField txtSimples;
    private JTextField txtMei;

    // Address Tab Fields
    private JTextField txtStreet;
    private JTextField txtNumber;
    private JTextField txtComplement;
    private JTextField txtDistrict;
    private JTextField txtZipCode;
    private JTextField txtCity;
    private JTextField txtState;
    private JTextField txtIbgeCode;

    // Contact Tab Fields
    private JTextField txtPhone1;
    private JTextField txtPhone2;
    private JTextField txtEmail;

    // CNAE Tab Fields
    private JTextField txtCnaePrincipal;
    private JTextArea txtCnaesSecundarios;

    // QSA Tab Fields
    private JTable tblQsa;
    private QsaTableModel qsaModel;
    private JButton btnImportQsa;

    // Action Buttons
    private JButton btnApply;
    private JButton btnCompare;
    private JButton btnClose;

    public CompanyEnrichmentDialog(Window parent, AddressBean targetAddress, String initialCnpj) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.targetAddress = targetAddress;
        initComponents();
        if (StringUtils.nonEmpty(initialCnpj) != null) {
            String clean = BrazilianDocumentValidator.unmask(initialCnpj);
            this.txtCnpjSearch.setText(clean);
            if (BrazilianDocumentValidator.isValidCnpj(clean)) {
                performSearch(clean);
            }
        }
        ComponentUtils.restoreDialogSize(this);
    }

    private void initComponents() {
        setTitle(bundle.getString("dialog.title"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Top Search Bar
        JPanel pnlTop = new JPanel(new BorderLayout(6, 6));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel pnlSearchInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlSearchInput.add(new JLabel(bundle.getString("label.cnpj")));

        txtCnpjSearch = new JTextField(18);
        txtCnpjSearch.setFont(txtCnpjSearch.getFont().deriveFont(Font.BOLD, 13f));
        txtCnpjSearch.addActionListener(e -> onSearchClicked());
        pnlSearchInput.add(txtCnpjSearch);

        btnSearch = new JButton(bundle.getString("button.search"));
        btnSearch.setIcon(new ImageIcon(getClass().getResource("/icons/find.png")));
        btnSearch.addActionListener(e -> onSearchClicked());
        pnlSearchInput.add(btnSearch);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(100, 20));
        pnlSearchInput.add(progressBar);

        lblStatus = new JLabel("");
        pnlSearchInput.add(lblStatus);

        pnlTop.add(pnlSearchInput, BorderLayout.NORTH);

        lblProvenanceInfo = new JLabel(" ");
        lblProvenanceInfo.setFont(lblProvenanceInfo.getFont().deriveFont(Font.ITALIC, 11f));
        lblProvenanceInfo.setForeground(new Color(100, 100, 100));
        lblProvenanceInfo.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));
        pnlTop.add(lblProvenanceInfo, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // Center Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        tabbedPane.addTab(bundle.getString("tab.company"), buildCompanyPanel());
        tabbedPane.addTab(bundle.getString("tab.address"), buildAddressPanel());
        tabbedPane.addTab(bundle.getString("tab.contacts"), buildContactsPanel());
        tabbedPane.addTab(bundle.getString("tab.qsa"), buildQsaPanel());
        tabbedPane.addTab(bundle.getString("tab.cnae"), buildCnaePanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JPanel pnlBottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnImportQsa = new JButton(bundle.getString("button.importQsaMembers"));
        btnImportQsa.setIcon(new ImageIcon(getClass().getResource("/icons/baseline_import_contacts_black_36dp.png")));
        btnImportQsa.setEnabled(false);
        btnImportQsa.addActionListener(e -> onImportQsaClicked());
        pnlBottomLeft.add(btnImportQsa);

        pnlBottom.add(pnlBottomLeft, BorderLayout.WEST);

        JPanel pnlBottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));

        btnCompare = new JButton(bundle.getString("button.compareWithCurrent"));
        btnCompare.setIcon(new ImageIcon(getClass().getResource("/icons/find.png")));
        btnCompare.setEnabled(false);
        btnCompare.addActionListener(e -> onCompareClicked());
        pnlBottomRight.add(btnCompare);

        btnApply = new JButton(bundle.getString("button.applySelected"));
        btnApply.setIcon(new ImageIcon(getClass().getResource("/icons/agt_action_success.png")));
        btnApply.setFont(btnApply.getFont().deriveFont(Font.BOLD));
        btnApply.setEnabled(false);
        btnApply.addActionListener(e -> onApplyClicked());
        pnlBottomRight.add(btnApply);

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
        setSize(new Dimension(880, 580));
        setMinimumSize(new Dimension(720, 480));
        FrameUtils.centerDialog(this, getOwner());
    }

    private JPanel buildCompanyPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Razão Social
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.razaoSocial")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.gridwidth = 3;
        txtRazaoSocial = createReadOnlyField();
        p.add(txtRazaoSocial, gbc);

        // Nome Fantasia
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0; gbc.gridwidth = 1;
        p.add(new JLabel(bundle.getString("label.nomeFantasia")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.gridwidth = 3;
        txtNomeFantasia = createReadOnlyField();
        p.add(txtNomeFantasia, gbc);

        // Situação Cadastral & Data
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0; gbc.gridwidth = 1;
        p.add(new JLabel(bundle.getString("label.situacaoCadastral")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
        lblSituacaoBadge = new JLabel("-");
        lblSituacaoBadge.setFont(lblSituacaoBadge.getFont().deriveFont(Font.BOLD, 12f));
        p.add(lblSituacaoBadge, gbc);

        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.dataSituacao")), gbc);
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.5;
        txtDataSituacao = createReadOnlyField();
        p.add(txtDataSituacao, gbc);

        // Motivo da Situação
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.motivoSituacao")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.gridwidth = 3;
        txtMotivoSituacao = createReadOnlyField();
        p.add(txtMotivoSituacao, gbc);

        // Natureza Jurídica
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0; gbc.gridwidth = 1;
        p.add(new JLabel(bundle.getString("label.naturezaJuridica")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.gridwidth = 3;
        txtNaturezaJuridica = createReadOnlyField();
        p.add(txtNaturezaJuridica, gbc);

        // Capital Social & Porte
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0; gbc.gridwidth = 1;
        p.add(new JLabel(bundle.getString("label.capitalSocial")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
        txtCapitalSocial = createReadOnlyField();
        p.add(txtCapitalSocial, gbc);

        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.porte")), gbc);
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.5;
        txtPorte = createReadOnlyField();
        p.add(txtPorte, gbc);

        // Simples Nacional & MEI
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0; gbc.gridwidth = 1;
        p.add(new JLabel(bundle.getString("label.simplesNacional")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
        txtSimples = createReadOnlyField();
        p.add(txtSimples, gbc);

        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.mei")), gbc);
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.5;
        txtMei = createReadOnlyField();
        p.add(txtMei, gbc);

        // Spacer
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0; gbc.gridwidth = 4;
        p.add(new JPanel(), gbc);

        return p;
    }

    private JPanel buildAddressPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // CEP & Município
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.zipCode")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.4;
        txtZipCode = createReadOnlyField();
        p.add(txtZipCode, gbc);

        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.city")), gbc);
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.6;
        txtCity = createReadOnlyField();
        p.add(txtCity, gbc);

        // UF & IBGE
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.state")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.4;
        txtState = createReadOnlyField();
        p.add(txtState, gbc);

        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.ibgeCode")), gbc);
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.6;
        txtIbgeCode = createReadOnlyField();
        p.add(txtIbgeCode, gbc);

        // Logradouro & Número
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.street")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7; gbc.gridwidth = 2;
        txtStreet = createReadOnlyField();
        p.add(txtStreet, gbc);

        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.3; gbc.gridwidth = 1;
        p.add(new JLabel(bundle.getString("label.number")), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.number")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.4;
        txtNumber = createReadOnlyField();
        p.add(txtNumber, gbc);

        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.complement")), gbc);
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.6;
        txtComplement = createReadOnlyField();
        p.add(txtComplement, gbc);

        // Bairro
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.district")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.gridwidth = 3;
        txtDistrict = createReadOnlyField();
        p.add(txtDistrict, gbc);

        // Spacer
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0; gbc.gridwidth = 4;
        p.add(new JPanel(), gbc);

        return p;
    }

    private JPanel buildContactsPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.phone1")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        txtPhone1 = createReadOnlyField();
        p.add(txtPhone1, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.phone2")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        txtPhone2 = createReadOnlyField();
        p.add(txtPhone2, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        p.add(new JLabel(bundle.getString("label.email")), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        txtEmail = createReadOnlyField();
        p.add(txtEmail, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0; gbc.gridwidth = 2;
        p.add(new JPanel(), gbc);

        return p;
    }

    private JPanel buildQsaPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        qsaModel = new QsaTableModel();
        tblQsa = new JTable(qsaModel);
        tblQsa.setRowHeight(22);
        tblQsa.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblQsa.getColumnModel().getColumn(1).setPreferredWidth(260);
        tblQsa.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblQsa.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblQsa.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane sp = new JScrollPane(tblQsa);
        p.add(sp, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildCnaePanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlMain = new JPanel(new BorderLayout(4, 4));
        pnlMain.add(new JLabel(bundle.getString("label.cnaePrincipal")), BorderLayout.NORTH);
        txtCnaePrincipal = createReadOnlyField();
        pnlMain.add(txtCnaePrincipal, BorderLayout.CENTER);
        p.add(pnlMain, BorderLayout.NORTH);

        JPanel pnlSec = new JPanel(new BorderLayout(4, 4));
        pnlSec.add(new JLabel(bundle.getString("label.cnaesSecundarios")), BorderLayout.NORTH);
        txtCnaesSecundarios = new JTextArea(8, 40);
        txtCnaesSecundarios.setEditable(false);
        txtCnaesSecundarios.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        pnlSec.add(new JScrollPane(txtCnaesSecundarios), BorderLayout.CENTER);
        p.add(pnlSec, BorderLayout.CENTER);

        return p;
    }

    private JTextField createReadOnlyField() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setBackground(new Color(245, 245, 245));
        return tf;
    }

    private void onSearchClicked() {
        String input = txtCnpjSearch.getText();
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, bundle.getString("msg.invalidCnpj"), getTitle(), JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cleanCnpj = BrazilianDocumentValidator.unmask(input.trim());
        if (!BrazilianDocumentValidator.isValidCnpj(cleanCnpj)) {
            JOptionPane.showMessageDialog(this, bundle.getString("msg.invalidCnpj"), getTitle(), JOptionPane.WARNING_MESSAGE);
            return;
        }

        performSearch(cleanCnpj);
    }

    private void performSearch(String cnpj) {
        btnSearch.setEnabled(false);
        btnSearch.setText(bundle.getString("button.searching"));
        progressBar.setVisible(true);
        lblStatus.setText("");

        SwingWorker<CompanyRegistryResult, Void> worker = new SwingWorker<>() {
            @Override
            protected CompanyRegistryResult doInBackground() throws Exception {
                BrazilianDataEnrichmentServiceRemote svc = JLawyerServiceLocator.getInstance(
                        com.jdimension.jlawyer.client.settings.ClientSettings.getInstance().getLookupProperties()
                ).lookupBrazilianDataEnrichmentServiceRemote();
                return svc.lookupCompany(cnpj, false);
            }

            @Override
            protected void done() {
                btnSearch.setEnabled(true);
                btnSearch.setText(bundle.getString("button.search"));
                progressBar.setVisible(false);

                try {
                    CompanyRegistryResult result = get();
                    if (result == null) {
                        JOptionPane.showMessageDialog(CompanyEnrichmentDialog.this,
                                bundle.getString("msg.companyNotFound"), getTitle(), JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                    } else {
                        populateResult(result);
                    }
                } catch (Exception ex) {
                    log.error("Erro na busca de CNPJ", ex);
                    JOptionPane.showMessageDialog(CompanyEnrichmentDialog.this,
                            bundle.getString("msg.searchError") + "\n" + ex.getMessage(),
                            getTitle(), JOptionPane.ERROR_MESSAGE);
                    clearFields();
                }
            }
        };

        worker.execute();
    }

    private void populateResult(CompanyRegistryResult res) {
        this.currentResult = res;

        // Header formatted
        if (BrazilianDocumentValidator.isValidCnpj(res.getCnpj())) {
            this.txtCnpjSearch.setText(BrazilianDocumentValidator.formatCnpj(res.getCnpj()));
        }

        // Provenance info
        if (res.getProvenance() != null) {
            String prov = res.getProvenance().getProviderName();
            String cache = res.getProvenance().getCacheStatus() != null ? res.getProvenance().getCacheStatus().name() : "LIVE";
            String dateStr = "";
            if (res.getProvenance().getConsultedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                dateStr = sdf.format(res.getProvenance().getConsultedAt());
            }
            lblProvenanceInfo.setText(bundle.getString("label.provenance") + " " + prov + "  |  " +
                    bundle.getString("label.cached") + " " + cache + "  |  " +
                    bundle.getString("label.updated") + " " + dateStr);
        }

        // Company Tab
        txtRazaoSocial.setText(StringUtils.nonEmpty(res.getLegalName()));
        txtNomeFantasia.setText(StringUtils.nonEmpty(res.getTradeName()));

        String status = res.getStatus() != null ? res.getStatus().name() : StringUtils.nonEmpty(res.getStatusDescription());
        lblSituacaoBadge.setText(status);
        if ("ACTIVE".equalsIgnoreCase(status) || "ATIVA".equalsIgnoreCase(status)) {
            lblSituacaoBadge.setForeground(new Color(0, 130, 0));
        } else {
            lblSituacaoBadge.setForeground(new Color(180, 0, 0));
        }

        if (res.getStatusDate() != null) {
            txtDataSituacao.setText(new SimpleDateFormat("dd/MM/yyyy").format(res.getStatusDate()));
        } else {
            txtDataSituacao.setText("");
        }

        txtMotivoSituacao.setText(StringUtils.nonEmpty(res.getStatusReason()));
        String legalNat = res.getLegalNatureDescription() != null ? res.getLegalNatureDescription() : StringUtils.nonEmpty(res.getLegalNatureCode());
        txtNaturezaJuridica.setText(legalNat);

        if (res.getCapitalSocial() != null) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            txtCapitalSocial.setText(nf.format(res.getCapitalSocial()));
        } else {
            txtCapitalSocial.setText("");
        }

        txtPorte.setText(StringUtils.nonEmpty(res.getCompanySize()));
        txtSimples.setText(res.isSimplesOptant() ? "SIM" : "NÃO");
        txtMei.setText(res.isMeiopting() ? "SIM" : "NÃO");

        // Address Tab
        AddressResult addr = res.getAddress();
        if (addr != null) {
            txtStreet.setText(StringUtils.nonEmpty(addr.getStreet()));
            txtNumber.setText(StringUtils.nonEmpty(addr.getNumber()));
            txtComplement.setText(StringUtils.nonEmpty(addr.getComplement()));
            txtDistrict.setText(StringUtils.nonEmpty(addr.getNeighborhood()));
            txtZipCode.setText(StringUtils.nonEmpty(addr.getCep()));
            txtCity.setText(StringUtils.nonEmpty(addr.getCity()));
            txtState.setText(StringUtils.nonEmpty(addr.getState()));
            txtIbgeCode.setText(StringUtils.nonEmpty(addr.getIbgeCityCode()));
        } else {
            txtStreet.setText("");
            txtNumber.setText("");
            txtComplement.setText("");
            txtDistrict.setText("");
            txtZipCode.setText("");
            txtCity.setText("");
            txtState.setText("");
            txtIbgeCode.setText("");
        }

        // Contact Tab
        if (res.getPhones() != null && !res.getPhones().isEmpty()) {
            txtPhone1.setText(res.getPhones().get(0));
            txtPhone2.setText(res.getPhones().size() > 1 ? res.getPhones().get(1) : "");
        } else {
            txtPhone1.setText("");
            txtPhone2.setText("");
        }

        if (res.getEmails() != null && !res.getEmails().isEmpty()) {
            txtEmail.setText(res.getEmails().get(0));
        } else {
            txtEmail.setText("");
        }

        // QSA Tab
        qsaModel.setMembers(res.getMembers());
        btnImportQsa.setEnabled(res.getMembers() != null && !res.getMembers().isEmpty());

        // CNAE Tab
        if (res.getMainCnaeCode() != null) {
            txtCnaePrincipal.setText(res.getMainCnaeCode() + " - " + StringUtils.nonEmpty(res.getMainCnaeDescription()));
        } else {
            txtCnaePrincipal.setText("");
        }

        if (res.getSecondaryCnaes() != null && !res.getSecondaryCnaes().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (CompanyRegistryResult.CnaeEntry cnae : res.getSecondaryCnaes()) {
                sb.append(cnae.getCode()).append(" - ").append(cnae.getDescription()).append("\n");
            }
            txtCnaesSecundarios.setText(sb.toString());
        } else {
            txtCnaesSecundarios.setText("");
        }

        btnApply.setEnabled(true);
        btnCompare.setEnabled(targetAddress != null);
    }

    private void clearFields() {
        currentResult = null;
        lblProvenanceInfo.setText(" ");
        txtRazaoSocial.setText("");
        txtNomeFantasia.setText("");
        lblSituacaoBadge.setText("-");
        txtDataSituacao.setText("");
        txtMotivoSituacao.setText("");
        txtNaturezaJuridica.setText("");
        txtCapitalSocial.setText("");
        txtPorte.setText("");
        txtSimples.setText("");
        txtMei.setText("");

        txtStreet.setText("");
        txtNumber.setText("");
        txtComplement.setText("");
        txtDistrict.setText("");
        txtZipCode.setText("");
        txtCity.setText("");
        txtState.setText("");
        txtIbgeCode.setText("");

        txtPhone1.setText("");
        txtPhone2.setText("");
        txtEmail.setText("");

        qsaModel.setMembers(null);
        txtCnaePrincipal.setText("");
        txtCnaesSecundarios.setText("");

        btnApply.setEnabled(false);
        btnCompare.setEnabled(false);
        btnImportQsa.setEnabled(false);
    }

    private void onCompareClicked() {
        if (currentResult == null || targetAddress == null) return;

        ContactDiffDialog diffDialog = new ContactDiffDialog(this, targetAddress, currentResult);
        diffDialog.setVisible(true);

        if (diffDialog.isApplied()) {
            diffDialog.applyTo(targetAddress);
            this.applied = true;
            JOptionPane.showMessageDialog(this, bundle.getString("msg.successApplied"),
                    getTitle(), JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    private void onApplyClicked() {
        if (currentResult == null) return;

        if (targetAddress != null) {
            // If targetAddress exists, open Diff dialog for explicit comparison
            onCompareClicked();
        } else {
            // Direct apply confirmation
            int opt = JOptionPane.showConfirmDialog(this, bundle.getString("msg.confirmApply"),
                    getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                this.applied = true;
                dispose();
            }
        }
    }

    private void onImportQsaClicked() {
        List<CompanyMemberResult> selected = qsaModel.getSelectedMembers();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, bundle.getString("msg.noSelection"), getTitle(), JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedQsaToImport.clear();
        selectedQsaToImport.addAll(selected);

        String msg = MessageFormat.format(bundle.getString("msg.importQsaSuccess"), selected.size());
        JOptionPane.showMessageDialog(this, msg, getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean isApplied() {
        return applied;
    }

    public CompanyRegistryResult getResult() {
        return currentResult;
    }

    public List<CompanyMemberResult> getSelectedQsaMembers() {
        return Collections.unmodifiableList(selectedQsaToImport);
    }

    /**
     * Aplica os dados enriquecidos diretamente a um AddressBean.
     */
    public void applyTo(AddressBean target) {
        if (currentResult == null || target == null) return;

        target.setCompany(currentResult.getLegalName());
        target.setCustom1(currentResult.getTradeName());
        target.setVatId(BrazilianDocumentValidator.formatCnpj(currentResult.getCnpj()));
        target.setCustom2(currentResult.getStatus() != null ? currentResult.getStatus().name() : currentResult.getStatusDescription());
        target.setCustom3(currentResult.getLegalNatureDescription() != null ? currentResult.getLegalNatureDescription() : currentResult.getLegalNatureCode());

        AddressResult addr = currentResult.getAddress();
        if (addr != null) {
            target.setStreet(addr.getStreet());
            target.setStreetNumber(addr.getNumber());
            target.setAdjunct(addr.getComplement());
            target.setDistrict(addr.getNeighborhood());
            target.setZipCode(addr.getCep());
            target.setCity(addr.getCity());
            target.setState(addr.getState());
            target.setExternalId1(addr.getIbgeCityCode());
        }

        if (currentResult.getPhones() != null && !currentResult.getPhones().isEmpty()) {
            target.setPhone(currentResult.getPhones().get(0));
            if (currentResult.getPhones().size() > 1) {
                target.setMobile(currentResult.getPhones().get(1));
            }
        }

        if (currentResult.getEmails() != null && !currentResult.getEmails().isEmpty()) {
            target.setEmail(currentResult.getEmails().get(0));
        }

        if (currentResult.getMainCnaeCode() != null) {
            target.setExternalId2(currentResult.getMainCnaeCode() + " - " + StringUtils.nonEmpty(currentResult.getMainCnaeDescription()));
        }
    }

    /**
     * TableModel para o Quadro de Sócios e Administradores (QSA).
     */
    private static class QsaTableModel extends DefaultTableModel {
        private final String[] columns = new String[]{
                bundle.getString("table.qsa.select"),
                bundle.getString("table.qsa.name"),
                bundle.getString("table.qsa.qualification"),
                bundle.getString("table.qsa.ageGroup"),
                bundle.getString("table.qsa.legalRep")
        };

        private static class QsaRow {
            CompanyMemberResult member;
            boolean selected;

            QsaRow(CompanyMemberResult member) {
                this.member = member;
                this.selected = false;
            }
        }

        private final List<QsaRow> rows = new ArrayList<>();

        public void setMembers(List<CompanyMemberResult> members) {
            rows.clear();
            if (members != null) {
                for (CompanyMemberResult m : members) {
                    rows.add(new QsaRow(m));
                }
            }
            fireTableDataChanged();
        }

        public List<CompanyMemberResult> getSelectedMembers() {
            List<CompanyMemberResult> list = new ArrayList<>();
            for (QsaRow r : rows) {
                if (r.selected) list.add(r.member);
            }
            return list;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return Boolean.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= rows.size()) return null;
            QsaRow row = rows.get(rowIndex);
            switch (columnIndex) {
                case 0: return row.selected;
                case 1: return row.member.getName();
                case 2: return row.member.getQualificationDescription() != null ? row.member.getQualificationDescription() : row.member.getQualificationCode();
                case 3: return row.member.getAgeGroup();
                case 4: return row.member.getLegalRepresentativeName();
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex < rows.size() && columnIndex == 0 && aValue instanceof Boolean) {
                rows.get(rowIndex).selected = (Boolean) aValue;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }
}
