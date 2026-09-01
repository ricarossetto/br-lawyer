/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.workflow;

import com.jdimension.jlawyer.client.utils.FrameUtils;
import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.services.BrazilianPublicationServiceRemote;
import com.jdimension.jlawyer.client.settings.ClientSettings;
import com.jdimension.jlawyer.client.settings.UserSettings;
import javax.naming.Context;
import java.util.Properties;
import com.jdimension.jlawyer.services.JLawyerServiceLocator;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Diálogo Swing para visualização completa, triagem e tratamento de publicação judicial.
 *
 * @author BR-LAWYER Team
 */
public class PublicationDetailDialog extends JDialog {

    private static final Logger log = Logger.getLogger(PublicationDetailDialog.class.getName());
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final String publicationId;
    private PublicationDetailDTO publication;
    private boolean changed = false;

    private JLabel lblHeader;
    private JLabel lblCourt;
    private JLabel lblCnj;
    private JLabel lblCase;
    private JLabel lblDates;
    private JLabel lblStatus;
    private JLabel lblLawyer;
    private JTextArea txtContent;
    private JTextArea txtNotes;

    public PublicationDetailDialog(Window owner, String publicationId) {
        super(owner, "Publicação / Intimação Judicial", ModalityType.APPLICATION_MODAL);
        this.publicationId = publicationId;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setSize(850, 650);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 4, 4));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informações da Publicação"));

        lblHeader = new JLabel("Carregando...");
        lblHeader.setFont(lblHeader.getFont().deriveFont(Font.BOLD, 14f));
        lblCourt = new JLabel("Tribunal / Fonte: -");
        lblCnj = new JLabel("Número CNJ: -");
        lblCase = new JLabel("Processo Vinculado: -");
        lblDates = new JLabel("Datas: -");
        lblStatus = new JLabel("Status / Tratamento: -");
        lblLawyer = new JLabel("Destinatário / OAB: -");

        infoPanel.add(lblHeader);
        infoPanel.add(lblCourt);
        infoPanel.add(lblCnj);
        infoPanel.add(lblCase);
        infoPanel.add(lblDates);
        infoPanel.add(lblStatus);

        // Center Content Panel
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        txtContent = new JTextArea();
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollContent = new JScrollPane(txtContent);
        scrollContent.setBorder(BorderFactory.createTitledBorder("Teor Completo da Publicação / Intimação"));

        // Bottom Notes Panel
        txtNotes = new JTextArea(3, 40);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane scrollNotes = new JScrollPane(txtNotes);
        scrollNotes.setBorder(BorderFactory.createTitledBorder("Notas de Tratamento Interno"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollContent, scrollNotes);
        split.setResizeWeight(0.75);

        centerPanel.add(split, BorderLayout.CENTER);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Buttons Bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnTreatTask = new JButton("Tratar com Tarefa / Prazo");
        btnTreatTask.setFont(btnTreatTask.getFont().deriveFont(Font.BOLD));
        JButton btnTreatOnly = new JButton("Marcar como Tratada");
        JButton btnArchive = new JButton("Dispensar / Arquivar");
        JButton btnClose = new JButton("Fechar");

        btnTreatTask.addActionListener(e -> onTreatWithTask());
        btnTreatOnly.addActionListener(e -> onTreatOnly());
        btnArchive.addActionListener(e -> onArchive());
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnTreatTask);
        buttonPanel.add(btnTreatOnly);
        buttonPanel.add(btnArchive);
        buttonPanel.add(btnClose);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            BrazilianPublicationServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianPublicationServiceRemote();
            publication = service.getPublication(publicationId);
            if (publication != null) {
                lblHeader.setText("Publicação #" + publication.getId());
                lblCourt.setText("Tribunal / Fonte: " + (publication.getCourtCode() != null ? publication.getCourtCode() : "N/D") 
                        + " (" + publication.getSource() + " - " + publication.getSourceType() + ")");
                lblCnj.setText("Número CNJ: " + (publication.getCnjNumber() != null ? publication.getCnjNumber() : "Não identificado"));
                lblCase.setText("Processo Vinculado: " + (publication.getCaseFileNumber() != null ? publication.getCaseFileNumber() + " - " + publication.getCaseName() : "Nenhum vínculo"));
                lblDates.setText("Disponibilização: " + (publication.getAvailabilityDate() != null ? DATE_FMT.format(publication.getAvailabilityDate()) : "-") 
                        + " | Publicação: " + (publication.getPublicationDate() != null ? DATE_FMT.format(publication.getPublicationDate()) : "-"));
                lblStatus.setText("Status: " + publication.getStatus() + " | Leitura: " + publication.getReadStatus() + " | Tratamento: " + publication.getTreatmentStatus());
                txtContent.setText(publication.getContent());
                txtNotes.setText(publication.getNotes());

                // Se não lida, marca como lida automaticamente ao abrir
                if ("UNREAD".equalsIgnoreCase(publication.getReadStatus())) {
                    service.markRead(publicationId, true, getAuthenticatedUser());
                    changed = true;
                }
            }
        } catch (Exception ex) {
            log.error("Erro ao carregar publicação: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Erro ao carregar publicação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onTreatWithTask() {
        if (publication == null) return;
        TaskEditDialog dlg = new TaskEditDialog(this, null, publication);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                BrazilianPublicationServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianPublicationServiceRemote();
                PublicationTreatRequestDTO req = new PublicationTreatRequestDTO();
                req.setUser(getAuthenticatedUser());
                req.setNotes(txtNotes.getText());
                req.setCreateFollowUpTask(false); // Já criada pelo TaskEditDialog
                service.treatPublication(publicationId, req);
                changed = true;
                JOptionPane.showMessageDialog(this, "Publicação tratada e tarefa vinculada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                log.error("Erro ao tratar publicação: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(this, "Erro ao tratar publicação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onTreatOnly() {
        try {
            BrazilianPublicationServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianPublicationServiceRemote();
            PublicationTreatRequestDTO req = new PublicationTreatRequestDTO();
            req.setUser(getAuthenticatedUser());
            req.setNotes(txtNotes.getText());
            req.setCreateFollowUpTask(false);
            service.treatPublication(publicationId, req);
            changed = true;
            JOptionPane.showMessageDialog(this, "Publicação marcada como tratada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            log.error("Erro ao tratar publicação: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Erro ao tratar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onArchive() {
        String reason = JOptionPane.showInputDialog(this, "Informe o motivo do arquivamento / dispensa:", "Dispensar Publicação", JOptionPane.QUESTION_MESSAGE);
        if (reason != null) {
            try {
                BrazilianPublicationServiceRemote service = JLawyerServiceLocator.getInstance(null).lookupBrazilianPublicationServiceRemote();
                service.archivePublication(publicationId, getAuthenticatedUser(), reason);
                changed = true;
                JOptionPane.showMessageDialog(this, "Publicação arquivada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                log.error("Erro ao arquivar publicação: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(this, "Erro ao arquivar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
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

    public boolean isChanged() {
        return changed;
    }
}