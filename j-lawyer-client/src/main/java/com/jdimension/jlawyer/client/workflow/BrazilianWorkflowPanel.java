/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.workflow;

import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.services.*;
import com.jdimension.jlawyer.client.settings.ClientSettings;
import com.jdimension.jlawyer.client.settings.UserSettings;
import com.jdimension.jlawyer.client.editors.ResetOnDisplayEditor;
import com.jdimension.jlawyer.client.editors.StatusBarProvider;
import com.jdimension.jlawyer.client.editors.ThemeableEditor;
import javax.naming.Context;
import java.util.Properties;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Painel Swing do Workflow Operacional Brasileiro:
 * - Aba 1: Publicações & Intimações (Inbox & Triagem)
 * - Aba 2: Tarefas & Prazos Judiciais (Lista & Gestão)
 * - Aba 3: Dashboard Operacional Consolidado
 *
 * @author BR-LAWYER Team
 */
public class BrazilianWorkflowPanel extends JPanel implements ThemeableEditor, ResetOnDisplayEditor, StatusBarProvider {

    private static final Logger log = Logger.getLogger(BrazilianWorkflowPanel.class.getName());
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd/MM/yyyy");

    private Image backgroundImage = null;
    private boolean needsReset = false;

    private JTabbedPane tabbedPane;

    // --- ABA 1: PUBLICAÇÕES ---
    private JTable tblPublications;
    private DefaultTableModel modelPublications;
    private JComboBox<String> cmbPubStatus;
    private JComboBox<String> cmbPubRead;
    private JTextField txtPubSearch;
    private List<PublicationOverviewDTO> currentPublications;

    // --- ABA 2: TAREFAS ---
    private JTable tblTasks;
    private DefaultTableModel modelTasks;
    private JComboBox<String> cmbTaskStatus;
    private JComboBox<String> cmbTaskPriority;
    private JCheckBox chkTaskOverdue;
    private JCheckBox chkTaskDueToday;
    private JTextField txtTaskSearch;
    private List<TaskOverviewDTO> currentTasks;

    // --- ABA 3: DASHBOARD ---
    private JLabel lblDashNewPubs;
    private JLabel lblDashUntreatedPubs;
    private JLabel lblDashOpenTasks;
    private JLabel lblDashOverdueTasks;
    private JLabel lblDashTodayTasks;

    public BrazilianWorkflowPanel() {
        initComponents();
        refreshAll();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Publicações & Intimações", createPublicationsPanel());
        tabbedPane.addTab("Tarefas & Prazos", createTasksPanel());
        tabbedPane.addTab("Dashboard Operacional", createDashboardPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ==========================================
    // ABA 1: PUBLICAÇÕES
    // ==========================================
    private JPanel createPublicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Filter Toolbar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        filterPanel.add(new JLabel("Status:"));
        cmbPubStatus = new JComboBox<>(new String[]{"ALL", "NOVA", "EM_ANALISE", "TRATADA", "ARQUIVADA"});
        filterPanel.add(cmbPubStatus);

        filterPanel.add(new JLabel("Leitura:"));
        cmbPubRead = new JComboBox<>(new String[]{"ALL", "UNREAD", "READ"});
        filterPanel.add(cmbPubRead);

        filterPanel.add(new JLabel("Busca:"));
        txtPubSearch = new JTextField(15);
        filterPanel.add(txtPubSearch);

        JButton btnFilterPub = new JButton("Filtrar");
        btnFilterPub.addActionListener(e -> refreshPublications());
        filterPanel.add(btnFilterPub);

        // Actions Toolbar
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnViewTreat = new JButton("Visualizar / Tratar");
        btnViewTreat.setFont(btnViewTreat.getFont().deriveFont(Font.BOLD));
        JButton btnMarkRead = new JButton("Marcar como Lida");
        JButton btnArchivePub = new JButton("Dispensar");
        JButton btnRefreshPub = new JButton("Atualizar");

        btnViewTreat.addActionListener(e -> onOpenSelectedPublication());
        btnMarkRead.addActionListener(e -> onMarkSelectedPublicationRead());
        btnArchivePub.addActionListener(e -> onArchiveSelectedPublication());
        btnRefreshPub.addActionListener(e -> refreshPublications());

        actionPanel.add(btnViewTreat);
        actionPanel.add(btnMarkRead);
        actionPanel.add(btnArchivePub);
        actionPanel.add(btnRefreshPub);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(filterPanel, BorderLayout.NORTH);
        topContainer.add(actionPanel, BorderLayout.SOUTH);

        // Table
        String[] cols = {"Data", "Tribunal", "Processo / CNJ", "Destinatário / OAB", "Status", "Leitura", "Tratamento", "Trecho"};
        modelPublications = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPublications = new JTable(modelPublications);
        tblPublications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPublications.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onOpenSelectedPublication();
                }
            }
        });

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblPublications), BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // ABA 2: TAREFAS
    // ==========================================
    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Filter Toolbar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        filterPanel.add(new JLabel("Status:"));
        cmbTaskStatus = new JComboBox<>(new String[]{"ALL", "TODO", "IN_PROGRESS", "WAITING", "DONE", "CANCELLED"});
        filterPanel.add(cmbTaskStatus);

        filterPanel.add(new JLabel("Prioridade:"));
        cmbTaskPriority = new JComboBox<>(new String[]{"ALL", "URGENT", "HIGH", "NORMAL", "LOW"});
        filterPanel.add(cmbTaskPriority);

        chkTaskOverdue = new JCheckBox("Atrasadas");
        chkTaskDueToday = new JCheckBox("Prazos de Hoje");
        filterPanel.add(chkTaskOverdue);
        filterPanel.add(chkTaskDueToday);

        filterPanel.add(new JLabel("Busca:"));
        txtTaskSearch = new JTextField(12);
        filterPanel.add(txtTaskSearch);

        JButton btnFilterTask = new JButton("Filtrar");
        btnFilterTask.addActionListener(e -> refreshTasks());
        filterPanel.add(btnFilterTask);

        // Actions Toolbar
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnNewTask = new JButton("Nova Tarefa");
        btnNewTask.setFont(btnNewTask.getFont().deriveFont(Font.BOLD));
        JButton btnEditTask = new JButton("Editar");
        JButton btnCompleteTask = new JButton("Marcar como Concluída");
        JButton btnRefreshTask = new JButton("Atualizar");

        btnNewTask.addActionListener(e -> onNewTask());
        btnEditTask.addActionListener(e -> onEditSelectedTask());
        btnCompleteTask.addActionListener(e -> onCompleteSelectedTask());
        btnRefreshTask.addActionListener(e -> refreshTasks());

        actionPanel.add(btnNewTask);
        actionPanel.add(btnEditTask);
        actionPanel.add(btnCompleteTask);
        actionPanel.add(btnRefreshTask);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(filterPanel, BorderLayout.NORTH);
        topContainer.add(actionPanel, BorderLayout.SOUTH);

        // Table
        String[] cols = {"Prioridade", "Prazo Fatal", "Título da Tarefa", "Processo", "Responsável", "Status", "Checklist"};
        modelTasks = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblTasks = new JTable(modelTasks);
        tblTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTasks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onEditSelectedTask();
                }
            }
        });

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblTasks), BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // ABA 3: DASHBOARD
    // ==========================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 12, 12));

        lblDashNewPubs = createCardLabel("0", "Publicações Novas", new Color(0, 120, 215));
        lblDashUntreatedPubs = createCardLabel("0", "Publicações Não Tratadas", new Color(230, 120, 0));
        lblDashOpenTasks = createCardLabel("0", "Tarefas em Aberto", new Color(0, 150, 100));
        lblDashOverdueTasks = createCardLabel("0", "Prazos Atrasados", new Color(200, 30, 30));
        lblDashTodayTasks = createCardLabel("0", "Prazos de Hoje", new Color(130, 50, 180));

        cardsPanel.add(lblDashNewPubs);
        cardsPanel.add(lblDashUntreatedPubs);
        cardsPanel.add(lblDashOpenTasks);
        cardsPanel.add(lblDashOverdueTasks);
        cardsPanel.add(lblDashTodayTasks);

        JButton btnRefreshDash = new JButton("Atualizar Métricas do Dashboard");
        btnRefreshDash.setFont(btnRefreshDash.getFont().deriveFont(Font.BOLD, 13f));
        btnRefreshDash.addActionListener(e -> refreshDashboard());

        panel.add(cardsPanel, BorderLayout.CENTER);
        panel.add(btnRefreshDash, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createCardLabel(String value, String title, Color color) {
        JLabel lbl = new JLabel("<html><center><font size='6' color='" + toHex(color) + "'><b>" + value + "</b></font><br><font size='4'>" + title + "</font></center></html>", SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(250, 250, 250));
        return lbl;
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // ==========================================
    // REFRESH & EVENT HANDLERS
    // ==========================================
    public void refreshAll() {
        refreshPublications();
        refreshTasks();
        refreshDashboard();
    }

    public void refreshPublications() {
        try {
            PublicationFilterDTO filter = new PublicationFilterDTO();
            filter.setStatus((String) cmbPubStatus.getSelectedItem());
            filter.setReadStatus((String) cmbPubRead.getSelectedItem());
            filter.setSearchText(txtPubSearch.getText().trim());
            filter.setPageSize(100);

            BrazilianPublicationServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianPublicationServiceRemote();
            currentPublications = service.listPublications(filter);

            modelPublications.setRowCount(0);
            for (PublicationOverviewDTO p : currentPublications) {
                modelPublications.addRow(new Object[]{
                        p.getPublicationDate() != null ? DATE_FMT.format(p.getPublicationDate()) : "-",
                        p.getCourtCode() != null ? p.getCourtCode() : "-",
                        p.getCaseFileNumber() != null ? p.getCaseFileNumber() : (p.getCnjNumber() != null ? p.getCnjNumber() : "-"),
                        p.getLawyerName() != null ? p.getLawyerName() : "-",
                        p.getStatus(),
                        p.getReadStatus(),
                        p.getTreatmentStatus(),
                        p.getSnippet()
                });
            }
        } catch (Exception ex) {
            log.error("Erro ao listar publicações: " + ex.getMessage(), ex);
        }
    }

    public void refreshTasks() {
        try {
            TaskFilterDTO filter = new TaskFilterDTO();
            filter.setStatus((String) cmbTaskStatus.getSelectedItem());
            filter.setPriority((String) cmbTaskPriority.getSelectedItem());
            if (chkTaskOverdue.isSelected()) filter.setOverdue(true);
            if (chkTaskDueToday.isSelected()) filter.setDueToday(true);
            filter.setSearchText(txtTaskSearch.getText().trim());
            filter.setPageSize(100);

            BrazilianTaskServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianTaskServiceRemote();
            currentTasks = service.listTasks(filter);

            modelTasks.setRowCount(0);
            for (TaskOverviewDTO t : currentTasks) {
                String checklistProgress = t.getChecklistTotalCount() > 0 
                        ? (t.getChecklistDoneCount() + "/" + t.getChecklistTotalCount()) : "-";
                modelTasks.addRow(new Object[]{
                        t.getPriority(),
                        t.getDueDate() != null ? DATE_FMT.format(t.getDueDate()) : "-",
                        t.getTitle(),
                        t.getCaseFileNumber() != null ? t.getCaseFileNumber() : "-",
                        t.getAssignedUser() != null ? t.getAssignedUser() : "-",
                        t.getStatus(),
                        checklistProgress
                });
            }
        } catch (Exception ex) {
            log.error("Erro ao listar tarefas: " + ex.getMessage(), ex);
        }
    }

    public void refreshDashboard() {
        try {
            BrazilianWorkflowDashboardServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianWorkflowDashboardServiceRemote();
            WorkflowDashboardDTO dto = service.getDashboard(getAuthenticatedUser());
            if (dto != null) {
                updateCard(lblDashNewPubs, String.valueOf(dto.getTotalNewPublications()), "Publicações Novas", new Color(0, 120, 215));
                updateCard(lblDashUntreatedPubs, String.valueOf(dto.getTotalUntreatedPublications()), "Publicações Não Tratadas", new Color(230, 120, 0));
                updateCard(lblDashOpenTasks, String.valueOf(dto.getTotalOpenTasks()), "Tarefas em Aberto", new Color(0, 150, 100));
                updateCard(lblDashOverdueTasks, String.valueOf(dto.getTotalOverdueTasks()), "Prazos Atrasados", new Color(200, 30, 30));
                updateCard(lblDashTodayTasks, String.valueOf(dto.getTotalDueTodayTasks()), "Prazos de Hoje", new Color(130, 50, 180));
            }
        } catch (Exception ex) {
            log.error("Erro ao atualizar dashboard: " + ex.getMessage(), ex);
        }
    }

    private void updateCard(JLabel lbl, String value, String title, Color color) {
        lbl.setText("<html><center><font size='6' color='" + toHex(color) + "'><b>" + value + "</b></font><br><font size='4'>" + title + "</font></center></html>");
    }

        private String getAuthenticatedUser() {
        try {
            if (UserSettings.getInstance().getCurrentUser() != null 
                    && UserSettings.getInstance().getCurrentUser().getPrincipalId() != null
                    && !UserSettings.getInstance().getCurrentUser().getPrincipalId().trim().isEmpty()) {
                return UserSettings.getInstance().getCurrentUser().getPrincipalId().trim();
            }
        } catch (Throwable ignored) {}
        try {
            Properties lookupProps = ClientSettings.getInstance().getLookupProperties();
            if (lookupProps != null && lookupProps.getProperty(Context.SECURITY_PRINCIPAL) != null) {
                String p = lookupProps.getProperty(Context.SECURITY_PRINCIPAL);
                if (p != null && !p.trim().isEmpty()) {
                    return p.trim();
                }
            }
        } catch (Throwable ignored) {}
        return System.getProperty("user.name", "system");
    }

    private void onOpenSelectedPublication() {
        int row = tblPublications.getSelectedRow();
        if (row < 0 || currentPublications == null || row >= currentPublications.size()) {
            JOptionPane.showMessageDialog(this, "Selecione uma publicação na lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PublicationOverviewDTO selected = currentPublications.get(row);
        PublicationDetailDialog dlg = new PublicationDetailDialog(SwingUtilities.getWindowAncestor(this), selected.getId());
        dlg.setVisible(true);
        if (dlg.isChanged()) {
            refreshPublications();
            refreshDashboard();
        }
    }

    private void onMarkSelectedPublicationRead() {
        int row = tblPublications.getSelectedRow();
        if (row < 0 || currentPublications == null || row >= currentPublications.size()) return;
        PublicationOverviewDTO selected = currentPublications.get(row);
        try {
            BrazilianPublicationServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianPublicationServiceRemote();
            service.markRead(selected.getId(), true, getAuthenticatedUser());
            refreshPublications();
            refreshDashboard();
        } catch (Exception ex) {
            log.error("Erro ao marcar publicação como lida: " + ex.getMessage(), ex);
        }
    }

    private void onArchiveSelectedPublication() {
        int row = tblPublications.getSelectedRow();
        if (row < 0 || currentPublications == null || row >= currentPublications.size()) return;
        PublicationOverviewDTO selected = currentPublications.get(row);
        String reason = JOptionPane.showInputDialog(this, "Motivo do arquivamento:", "Dispensar Publicação", JOptionPane.QUESTION_MESSAGE);
        if (reason != null) {
            try {
                BrazilianPublicationServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianPublicationServiceRemote();
                service.archivePublication(selected.getId(), getAuthenticatedUser(), reason);
                refreshPublications();
                refreshDashboard();
            } catch (Exception ex) {
                log.error("Erro ao arquivar publicação: " + ex.getMessage(), ex);
            }
        }
    }

    private void onNewTask() {
        TaskEditDialog dlg = new TaskEditDialog(SwingUtilities.getWindowAncestor(this), null, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            refreshTasks();
            refreshDashboard();
        }
    }

    private void onEditSelectedTask() {
        int row = tblTasks.getSelectedRow();
        if (row < 0 || currentTasks == null || row >= currentTasks.size()) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TaskOverviewDTO selected = currentTasks.get(row);
        TaskEditDialog dlg = new TaskEditDialog(SwingUtilities.getWindowAncestor(this), selected.getId(), null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            refreshTasks();
            refreshDashboard();
        }
    }

    private void onCompleteSelectedTask() {
        int row = tblTasks.getSelectedRow();
        if (row < 0 || currentTasks == null || row >= currentTasks.size()) return;
        TaskOverviewDTO selected = currentTasks.get(row);
        try {
            BrazilianTaskServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianTaskServiceRemote();
            TaskStatusChangeDTO change = new TaskStatusChangeDTO("DONE", getAuthenticatedUser());
            service.changeStatus(selected.getId(), change);
            refreshTasks();
            refreshDashboard();
        } catch (Exception ex) {
            log.error("Erro ao concluir tarefa: " + ex.getMessage(), ex);
        }
    }

    // ==========================================
    // INTERFACE CONTRACTS
    // ==========================================

    @Override
    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
        this.repaint();
    }

    @Override
    public Image getBackgroundImage() {
        return this.backgroundImage;
    }

    @Override
    public void reset() {
        refreshAll();
        this.needsReset = false;
    }

    @Override
    public boolean needsReset() {
        return this.needsReset;
    }

    public void setNeedsReset(boolean needsReset) {
        this.needsReset = needsReset;
    }

    @Override
    public void notifyStatusBarReady() {
        // Status bar notification if needed
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.backgroundImage != null) {
            g.drawImage(this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}