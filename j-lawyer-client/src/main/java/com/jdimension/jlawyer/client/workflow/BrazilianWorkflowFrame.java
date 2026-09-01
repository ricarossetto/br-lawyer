/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.workflow;

import javax.swing.*;
import java.awt.*;

/**
 * Janela / Frame para o Workflow Jurídico Brasileiro no j-lawyer-client.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianWorkflowFrame extends JFrame {

    public BrazilianWorkflowFrame() {
        super("BR-LAWYER — Workflow Operacional Brasileiro (Publicações + Tarefas + Prazos)");
        initComponents();
    }

    private void initComponents() {
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BrazilianWorkflowPanel workflowPanel = new BrazilianWorkflowPanel();
        getContentPane().add(workflowPanel, BorderLayout.CENTER);
    }
}