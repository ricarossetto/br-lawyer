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
import com.jdimension.jlawyer.services.BrazilianTaskServiceRemote;
import com.jdimension.jlawyer.services.JLawyerServiceLocator;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Diálogo Swing para criação e edição de tarefas jurídicas e prazos no BR-LAWYER.
 *
 * @author BR-LAWYER Team
 */
public class TaskEditDialog extends JDialog {

    private static final Logger log = Logger.getLogger(TaskEditDialog.class.getName());
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd/MM/yyyy");

    private String taskId;
    private PublicationDetailDTO linkedPublication;
    private boolean saved = false;

    private JTextField txtTitle;
    private JTextArea txtDescription;
    private JComboBox<String> cmbPriority;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbStatus;
    private JTextField txtDueDate;
    private JTextField txtDueTime;
    private JTextField txtAssignedUser;
    private JCheckBox chkSyncCalendar;

    public TaskEditDialog(Window owner, String taskId, PublicationDetailDTO linkedPublication) {
        super(owner, taskId != null ? "Editar Tarefa / Prazo" : "Nova Tarefa / Prazo", ModalityType.APPLICATION_MODAL);
        this.taskId = taskId;
        this.linkedPublication = linkedPublication;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setSize(650, 520);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Title
        c.gridx = 0; c.gridy = 0; c.weightx = 0.0;
        mainPanel.add(new JLabel("Título da Tarefa: *"), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1.0; c.gridwidth = 3;
        txtTitle = new JTextField();
        mainPanel.add(txtTitle, c);

        // Category & Priority
        c.gridx = 0; c.gridy = 1; c.weightx = 0.0; c.gridwidth = 1;
        mainPanel.add(new JLabel("Categoria:"), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 0.5;
        cmbCategory = new JComboBox<>(new String[]{"CUMPRIMENTO_PRAZO", "ANALISE", "PETICAO", "AUDIENCIA", "DILIGENCIA", "REUNIAO", "OUTRO"});
        mainPanel.add(cmbCategory, c);

        c.gridx = 2; c.gridy = 1; c.weightx = 0.0;
        mainPanel.add(new JLabel("Prioridade:"), c);
        c.gridx = 3; c.gridy = 1; c.weightx = 0.5;
        cmbPriority = new JComboBox<>(new String[]{"URGENT", "HIGH", "NORMAL", "LOW"});
        cmbPriority.setSelectedItem("HIGH");
        mainPanel.add(cmbPriority, c);

        // Status & Assigned User
        c.gridx = 0; c.gridy = 2; c.weightx = 0.0;
        mainPanel.add(new JLabel("Status:"), c);
        c.gridx = 1; c.gridy = 2; c.weightx = 0.5;
        cmbStatus = new JComboBox<>(new String[]{"TODO", "IN_PROGRESS", "WAITING", "DONE", "CANCELLED"});
        mainPanel.add(cmbStatus, c);

        c.gridx = 2; c.gridy = 2; c.weightx = 0.0;
        mainPanel.add(new JLabel("Responsável:"), c);
        c.gridx = 3; c.gridy = 2; c.weightx = 0.5;
        txtAssignedUser = new JTextField();
        mainPanel.add(txtAssignedUser, c);

        // Due Date & Time
        c.gridx = 0; c.gridy = 3; c.weightx = 0.0;
        mainPanel.add(new JLabel("Prazo Fatal (dd/MM/yyyy):"), c);
        c.gridx = 1; c.gridy = 3; c.weightx = 0.5;
        txtDueDate = new JTextField();
        mainPanel.add(txtDueDate, c);

        c.gridx = 2; c.gridy = 3; c.weightx = 0.0;
        mainPanel.add(new JLabel("Horário (HH:mm):"), c);
        c.gridx = 3; c.gridy = 3; c.weightx = 0.5;
        txtDueTime = new JTextField();
        mainPanel.add(txtDueTime, c);

        // Calendar Sync Checkbox
        c.gridx = 0; c.gridy = 4; c.gridwidth = 4;
        chkSyncCalendar = new JCheckBox("Sincronizar com Calendário / Prazos Oficiais do j-lawyer (Frist / Respite)", true);
        chkSyncCalendar.setFont(chkSyncCalendar.getFont().deriveFont(Font.BOLD));
        mainPanel.add(chkSyncCalendar, c);

        // Description
        c.gridx = 0; c.gridy = 5; c.gridwidth = 4; c.fill = GridBagConstraints.BOTH; c.weighty = 1.0;
        txtDescription = new JTextArea();
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setBorder(BorderFactory.createTitledBorder("Descrição / Instruções da Tarefa"));
        mainPanel.add(scrollDesc, c);

        // Button bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnSave = new JButton("Salvar Tarefa");
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD));
        JButton btnCancel = new JButton("Cancelar");

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        if (taskId != null) {
            try {
                BrazilianTaskServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianTaskServiceRemote();
                TaskDetailDTO task = service.getTask(taskId);
                if (task != null) {
                    txtTitle.setText(task.getTitle());
                    txtDescription.setText(task.getDescription());
                    cmbCategory.setSelectedItem(task.getCategory());
                    cmbPriority.setSelectedItem(task.getPriority());
                    cmbStatus.setSelectedItem(task.getStatus());
                    txtAssignedUser.setText(task.getAssignedUser());
                    if (task.getDueDate() != null) {
                        txtDueDate.setText(DATE_FMT.format(task.getDueDate()));
                    }
                    txtDueTime.setText(task.getDueTime());
                }
            } catch (Exception ex) {
                log.error("Erro ao carregar tarefa: " + ex.getMessage(), ex);
            }
        } else if (linkedPublication != null) {
            txtTitle.setText("Cumprimento de publicação: " + (linkedPublication.getCourtCode() != null ? linkedPublication.getCourtCode() : "Intimação"));
            txtDescription.setText(linkedPublication.getContent());
            txtAssignedUser.setText(linkedPublication.getAssignedUser());
            if (linkedPublication.getSuggestedDueDate() != null) {
                txtDueDate.setText(DATE_FMT.format(linkedPublication.getSuggestedDueDate()));
            } else {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, 15);
                txtDueDate.setText(DATE_FMT.format(cal.getTime()));
            }
        }
    }

    private void onSave() {
        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Título da tarefa é obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            TaskDetailDTO dto = new TaskDetailDTO();
            dto.setId(taskId);
            dto.setTitle(title);
            dto.setDescription(txtDescription.getText());
            dto.setCategory((String) cmbCategory.getSelectedItem());
            dto.setPriority((String) cmbPriority.getSelectedItem());
            dto.setStatus((String) cmbStatus.getSelectedItem());
            dto.setAssignedUser(txtAssignedUser.getText().trim());

            if (linkedPublication != null) {
                dto.setProcessId(linkedPublication.getProcessId());
                dto.setPublicationId(linkedPublication.getId());
            }

            String dateStr = txtDueDate.getText().trim();
            if (!dateStr.isEmpty()) {
                dto.setDueDate(DATE_FMT.parse(dateStr));
            }
            dto.setDueTime(txtDueTime.getText().trim());

            BrazilianTaskServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianTaskServiceRemote();
            service.saveTask(dto, "CURRENT_USER", chkSyncCalendar.isSelected());
            saved = true;
            dispose();
        } catch (Exception ex) {
            log.error("Erro ao salvar tarefa: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Erro ao salvar tarefa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}