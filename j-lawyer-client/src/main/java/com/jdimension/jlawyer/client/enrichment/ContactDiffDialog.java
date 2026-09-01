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
import com.jdimension.jlawyer.domain.enrichment.model.CompanyRegistryResult;
import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import com.jdimension.jlawyer.persistence.AddressBean;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Diálogo para comparação lado a lado entre os dados cadastrais existentes e os
 * dados enriquecidos de fontes públicas oficiais (Receita Federal / BrasilAPI / SERPRO).
 *
 * Permite ao usuário inspecionar divergências e escolher seletivamente quais campos aplicar.
 *
 * @author BR-LAWYER Team
 */
public class ContactDiffDialog extends JDialog {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com/jdimension/jlawyer/client/enrichment/ContactDiffDialog");

    public static final String FIELD_COMPANY_NAME = "company";
    public static final String FIELD_TRADE_NAME = "custom1";
    public static final String FIELD_VAT_ID = "vatId";
    public static final String FIELD_STATUS = "custom2";
    public static final String FIELD_LEGAL_NATURE = "custom3";
    public static final String FIELD_STREET = "street";
    public static final String FIELD_NUMBER = "streetNr";
    public static final String FIELD_COMPLEMENT = "adjunct";
    public static final String FIELD_DISTRICT = "district";
    public static final String FIELD_ZIP = "zipCode";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_IBGE = "externalId1";
    public static final String FIELD_PHONE1 = "phone";
    public static final String FIELD_PHONE2 = "phone2";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_CNAE = "externalId2";

    private final AddressBean currentAddress;
    private final CompanyRegistryResult enrichedResult;
    private final Map<String, String> selectedValues = new LinkedHashMap<>();
    private boolean applied = false;

    private JTable diffTable;
    private DiffTableModel tableModel;
    private JLabel lblProvenance;
    private JButton btnApply;
    private JButton btnKeepCurrent;
    private JButton btnCancel;

    public static class DiffItem {
        public String fieldKey;
        public String fieldLabel;
        public String currentValue;
        public String enrichedValue;
        public boolean divergent;
        public boolean selected;

        public DiffItem(String fieldKey, String fieldLabel, String currentValue, String enrichedValue) {
            this.fieldKey = fieldKey;
            this.fieldLabel = fieldLabel;
            this.currentValue = currentValue != null ? currentValue.trim() : "";
            this.enrichedValue = enrichedValue != null ? enrichedValue.trim() : "";
            this.divergent = !this.currentValue.equalsIgnoreCase(this.enrichedValue) && !this.enrichedValue.isEmpty();
            // Default selected if divergent and enriched value is non-empty
            this.selected = this.divergent;
        }
    }

    private final List<DiffItem> items = new ArrayList<>();

    public ContactDiffDialog(Window parent, AddressBean currentAddress, CompanyRegistryResult enrichedResult) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.currentAddress = currentAddress;
        this.enrichedResult = enrichedResult;
        buildDiffItems();
        initComponents();
        ComponentUtils.restoreDialogSize(this);
    }

    private void buildDiffItems() {
        items.clear();
        if (enrichedResult == null) return;

        String curCompany = currentAddress != null ? currentAddress.getCompany() : "";
        String curVat = currentAddress != null ? currentAddress.getVatId() : "";
        String curTradeName = currentAddress != null ? currentAddress.getCustom1() : "";
        String curStreet = currentAddress != null ? currentAddress.getStreet() : "";
        String curStreetNr = currentAddress != null ? currentAddress.getStreetNumber() : "";
        String curAdjunct = currentAddress != null ? currentAddress.getAdjunct() : "";
        String curDistrict = currentAddress != null ? currentAddress.getDistrict() : "";
        String curZip = currentAddress != null ? currentAddress.getZipCode() : "";
        String curCity = currentAddress != null ? currentAddress.getCity() : "";
        String curState = currentAddress != null ? currentAddress.getState() : "";
        String curPhone1 = currentAddress != null ? currentAddress.getPhone() : "";
        String curPhone2 = currentAddress != null ? currentAddress.getMobile() : "";
        String curEmail = currentAddress != null ? currentAddress.getEmail() : "";
        String curStatus = currentAddress != null ? currentAddress.getCustom2() : "";
        String curLegalNat = currentAddress != null ? currentAddress.getCustom3() : "";
        String curIbge = currentAddress != null ? currentAddress.getExternalId1() : "";
        String curCnae = currentAddress != null ? currentAddress.getExternalId2() : "";

        // Format CNPJ if possible
        String enrichedVat = enrichedResult.getCnpj();
        if (enrichedVat != null && BrazilianDocumentValidator.isValidCnpj(enrichedVat)) {
            enrichedVat = BrazilianDocumentValidator.formatCnpj(enrichedVat);
        }
        if (curVat != null && BrazilianDocumentValidator.isValidCnpj(curVat)) {
            curVat = BrazilianDocumentValidator.formatCnpj(curVat);
        }

        AddressResult addr = enrichedResult.getAddress();
        String enrStreet = addr != null ? addr.getStreet() : "";
        String enrNumber = addr != null ? addr.getNumber() : "";
        String enrComplement = addr != null ? addr.getComplement() : "";
        String enrDistrict = addr != null ? addr.getNeighborhood() : "";
        String enrZip = addr != null ? addr.getCep() : "";
        String enrCity = addr != null ? addr.getCity() : "";
        String enrState = addr != null ? addr.getState() : "";
        String enrIbge = addr != null ? addr.getIbgeCityCode() : "";

        // Phone numbers
        String enrPhone1 = "";
        String enrPhone2 = "";
        if (enrichedResult.getPhones() != null) {
            if (enrichedResult.getPhones().size() > 0) enrPhone1 = enrichedResult.getPhones().get(0);
            if (enrichedResult.getPhones().size() > 1) enrPhone2 = enrichedResult.getPhones().get(1);
        }

        // Emails
        String enrEmail = "";
        if (enrichedResult.getEmails() != null && !enrichedResult.getEmails().isEmpty()) {
            enrEmail = enrichedResult.getEmails().get(0);
        }

        // CNAE
        String enrCnae = "";
        if (enrichedResult.getMainCnaeCode() != null) {
            enrCnae = enrichedResult.getMainCnaeCode() + " - " + StringUtils.nonEmpty(enrichedResult.getMainCnaeDescription());
        }

        String enrStatus = enrichedResult.getStatus() != null ? enrichedResult.getStatus().name() : StringUtils.nonEmpty(enrichedResult.getStatusDescription());
        String enrLegalNat = enrichedResult.getLegalNatureDescription() != null ? enrichedResult.getLegalNatureDescription() : StringUtils.nonEmpty(enrichedResult.getLegalNatureCode());

        items.add(new DiffItem(FIELD_COMPANY_NAME, bundle.getString("field.companyName"), curCompany, enrichedResult.getLegalName()));
        items.add(new DiffItem(FIELD_TRADE_NAME, bundle.getString("field.tradeName"), curTradeName, enrichedResult.getTradeName()));
        items.add(new DiffItem(FIELD_VAT_ID, bundle.getString("field.vatId"), curVat, enrichedVat));
        items.add(new DiffItem(FIELD_STATUS, bundle.getString("field.status"), curStatus, enrStatus));
        items.add(new DiffItem(FIELD_LEGAL_NATURE, bundle.getString("field.legalNature"), curLegalNat, enrLegalNat));
        items.add(new DiffItem(FIELD_STREET, bundle.getString("field.street"), curStreet, enrStreet));
        items.add(new DiffItem(FIELD_NUMBER, bundle.getString("field.number"), curStreetNr, enrNumber));
        items.add(new DiffItem(FIELD_COMPLEMENT, bundle.getString("field.complement"), curAdjunct, enrComplement));
        items.add(new DiffItem(FIELD_DISTRICT, bundle.getString("field.district"), curDistrict, enrDistrict));
        items.add(new DiffItem(FIELD_ZIP, bundle.getString("field.zipCode"), curZip, enrZip));
        items.add(new DiffItem(FIELD_CITY, bundle.getString("field.city"), curCity, enrCity));
        items.add(new DiffItem(FIELD_STATE, bundle.getString("field.state"), curState, enrState));
        items.add(new DiffItem(FIELD_IBGE, bundle.getString("field.ibgeCode"), curIbge, enrIbge));
        items.add(new DiffItem(FIELD_PHONE1, bundle.getString("field.phone1"), curPhone1, enrPhone1));
        items.add(new DiffItem(FIELD_PHONE2, bundle.getString("field.phone2"), curPhone2, enrPhone2));
        items.add(new DiffItem(FIELD_EMAIL, bundle.getString("field.email"), curEmail, enrEmail));
        items.add(new DiffItem(FIELD_CNAE, bundle.getString("field.cnae"), curCnae, enrCnae));
    }

    private void initComponents() {
        setTitle(bundle.getString("dialog.title"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel pnlHeader = new JPanel(new BorderLayout(5, 5));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel lblExplanation = new JLabel("<html><b>" + bundle.getString("header.explanation") + "</b></html>");
        lblExplanation.setIcon(new ImageIcon(getClass().getResource("/icons/find.png")));
        pnlHeader.add(lblExplanation, BorderLayout.NORTH);

        // Provenance & Metadata
        String provText = "";
        if (enrichedResult != null && enrichedResult.getProvenance() != null) {
            String provName = enrichedResult.getProvenance().getProviderName();
            String cached = enrichedResult.getProvenance().getCacheStatus() != null ? enrichedResult.getProvenance().getCacheStatus().name() : "LIVE";
            String dateStr = "";
            if (enrichedResult.getProvenance().getConsultedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                dateStr = sdf.format(enrichedResult.getProvenance().getConsultedAt());
            }
            provText = bundle.getString("label.provenance") + " " + provName + "  |  " +
                    bundle.getString("label.timestamp") + " " + dateStr + "  |  " +
                    bundle.getString("label.cacheStatus") + " " + cached;
        }
        lblProvenance = new JLabel(provText);
        lblProvenance.setFont(lblProvenance.getFont().deriveFont(Font.ITALIC, 11f));
        lblProvenance.setForeground(new Color(90, 90, 90));
        pnlHeader.add(lblProvenance, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // Table Panel
        tableModel = new DiffTableModel(items);
        diffTable = new JTable(tableModel);
        diffTable.setRowHeight(24);
        diffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        diffTable.getTableHeader().setReorderingAllowed(false);

        // Custom Renderer for Divergence Highlighting
        diffTable.setDefaultRenderer(Object.class, new DiffCellRenderer());

        // Column widths
        diffTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        diffTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        diffTable.getColumnModel().getColumn(2).setPreferredWidth(230);
        diffTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        diffTable.getColumnModel().getColumn(4).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(diffTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        btnKeepCurrent = new JButton(bundle.getString("button.keepCurrent"));
        btnKeepCurrent.setIcon(new ImageIcon(getClass().getResource("/icons/cancel.png")));
        btnKeepCurrent.addActionListener(e -> onKeepCurrent());

        btnCancel = new JButton(bundle.getString("button.cancel"));
        btnCancel.addActionListener(e -> onCancel());

        btnApply = new JButton(bundle.getString("button.applySelected"));
        btnApply.setIcon(new ImageIcon(getClass().getResource("/icons/agt_action_success.png")));
        btnApply.setFont(btnApply.getFont().deriveFont(Font.BOLD));
        btnApply.addActionListener(e -> onApply());

        pnlButtons.add(btnKeepCurrent);
        pnlButtons.add(btnCancel);
        pnlButtons.add(btnApply);

        add(pnlButtons, BorderLayout.SOUTH);

        // Keybindings (ESC = cancel, ENTER = apply)
        getRootPane().registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        setSize(new Dimension(850, 520));
        setMinimumSize(new Dimension(700, 400));
        FrameUtils.centerDialog(this, getOwner());
    }

    private void onApply() {
        int count = 0;
        selectedValues.clear();
        for (DiffItem item : items) {
            if (item.selected && !item.enrichedValue.isEmpty()) {
                selectedValues.put(item.fieldKey, item.enrichedValue);
                count++;
            }
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("msg.noChangesSelected"),
                    getTitle(), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String msg = MessageFormat.format(bundle.getString("msg.confirmApply"), count);
        int opt = JOptionPane.showConfirmDialog(this, msg, getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            this.applied = true;
            dispose();
        }
    }

    private void onKeepCurrent() {
        this.applied = false;
        this.selectedValues.clear();
        dispose();
    }

    private void onCancel() {
        this.applied = false;
        this.selectedValues.clear();
        dispose();
    }

    public boolean isApplied() {
        return applied;
    }

    public Map<String, String> getSelectedValues() {
        return Collections.unmodifiableMap(selectedValues);
    }

    /**
     * Aplica os campos selecionados diretamente a um AddressBean.
     */
    public void applyTo(AddressBean target) {
        if (!applied || target == null) return;

        if (selectedValues.containsKey(FIELD_COMPANY_NAME)) target.setCompany(selectedValues.get(FIELD_COMPANY_NAME));
        if (selectedValues.containsKey(FIELD_TRADE_NAME)) target.setCustom1(selectedValues.get(FIELD_TRADE_NAME));
        if (selectedValues.containsKey(FIELD_VAT_ID)) target.setVatId(selectedValues.get(FIELD_VAT_ID));
        if (selectedValues.containsKey(FIELD_STATUS)) target.setCustom2(selectedValues.get(FIELD_STATUS));
        if (selectedValues.containsKey(FIELD_LEGAL_NATURE)) target.setCustom3(selectedValues.get(FIELD_LEGAL_NATURE));
        if (selectedValues.containsKey(FIELD_STREET)) target.setStreet(selectedValues.get(FIELD_STREET));
        if (selectedValues.containsKey(FIELD_NUMBER)) target.setStreetNumber(selectedValues.get(FIELD_NUMBER));
        if (selectedValues.containsKey(FIELD_COMPLEMENT)) target.setAdjunct(selectedValues.get(FIELD_COMPLEMENT));
        if (selectedValues.containsKey(FIELD_DISTRICT)) target.setDistrict(selectedValues.get(FIELD_DISTRICT));
        if (selectedValues.containsKey(FIELD_ZIP)) target.setZipCode(selectedValues.get(FIELD_ZIP));
        if (selectedValues.containsKey(FIELD_CITY)) target.setCity(selectedValues.get(FIELD_CITY));
        if (selectedValues.containsKey(FIELD_STATE)) target.setState(selectedValues.get(FIELD_STATE));
        if (selectedValues.containsKey(FIELD_IBGE)) target.setExternalId1(selectedValues.get(FIELD_IBGE));
        if (selectedValues.containsKey(FIELD_PHONE1)) target.setPhone(selectedValues.get(FIELD_PHONE1));
        if (selectedValues.containsKey(FIELD_PHONE2)) target.setMobile(selectedValues.get(FIELD_PHONE2));
        if (selectedValues.containsKey(FIELD_EMAIL)) target.setEmail(selectedValues.get(FIELD_EMAIL));
        if (selectedValues.containsKey(FIELD_CNAE)) target.setExternalId2(selectedValues.get(FIELD_CNAE));
    }

    /**
     * TableModel para exibição e seleção dos campos comparados.
     */
    private static class DiffTableModel extends DefaultTableModel {
        private final List<DiffItem> items;
        private final String[] columns = new String[]{
                bundle.getString("column.field"),
                bundle.getString("column.current"),
                bundle.getString("column.found"),
                bundle.getString("column.divergent"),
                bundle.getString("column.apply")
        };

        public DiffTableModel(List<DiffItem> items) {
            this.items = items;
        }

        @Override
        public int getRowCount() {
            return items != null ? items.size() : 0;
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
            if (columnIndex == 4) return Boolean.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= items.size()) return null;
            DiffItem item = items.get(rowIndex);
            switch (columnIndex) {
                case 0: return item.fieldLabel;
                case 1: return item.currentValue;
                case 2: return item.enrichedValue;
                case 3: return item.divergent ? bundle.getString("msg.divergentYes") : bundle.getString("msg.divergentNo");
                case 4: return item.selected;
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex < items.size() && columnIndex == 4 && aValue instanceof Boolean) {
                items.get(rowIndex).selected = (Boolean) aValue;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }

    /**
     * Custom Cell Renderer para destacar divergências com fundo suave.
     */
    private class DiffCellRenderer extends DefaultTableCellRenderer {
        private final Color DIVERGENT_BG = new Color(255, 250, 220);
        private final Color DIVERGENT_TEXT = new Color(180, 50, 0);
        private final Color MATCH_TEXT = new Color(40, 120, 40);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (row < items.size()) {
                DiffItem item = items.get(row);
                if (!isSelected) {
                    if (item.divergent) {
                        c.setBackground(DIVERGENT_BG);
                        if (column == 3) {
                            c.setForeground(DIVERGENT_TEXT);
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if (column == 2) {
                            c.setForeground(Color.BLUE.darker());
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(Color.BLACK);
                        }
                    } else {
                        c.setBackground(Color.WHITE);
                        if (column == 3) {
                            c.setForeground(MATCH_TEXT);
                        } else {
                            c.setForeground(Color.DARK_GRAY);
                        }
                    }
                }
            }
            return c;
        }
    }
}
