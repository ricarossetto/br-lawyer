/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.utils;

import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import com.jdimension.jlawyer.domain.legal.cnj.CnjNumber;
import com.jdimension.jlawyer.domain.legal.cnj.CnjNumberValidator;
import com.jdimension.jlawyer.domain.legal.cnj.CpfCnpjValidator;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

/**
 * Utilitários e formatadores Swing com validação visual (FlatLaf outline e tooltips)
 * para campos de identificação jurídica do ecossistema brasileiro:
 * - NPU CNJ (Resolução 65/2008 - Módulo 97-10)
 * - CPF (Módulo 11)
 * - CNPJ (Módulo 11 / IN RFB nº 2.229/2024 alfanumérico)
 * - CEP (8 dígitos)
 *
 * @author BR-LAWYER Team
 */
public final class BrazilianUiUtils {

    public static final String OUTLINE_KEY = "JComponent.outline";
    public static final String OUTLINE_ERROR = "error";
    public static final String OUTLINE_WARNING = "warning";

    private BrazilianUiUtils() {
        // Utilitário estático
    }

    /**
     * Instala formatador e validador de CNJ/NPU em tempo real em um JTextField.
     *
     * @param field Campo de texto Swing
     * @param onSuccessCallback Callback executado quando um CNJ de 20 dígitos for matematicamente válido
     */
    public static void installCnjFormatter(JTextField field, Consumer<CnjNumber> onSuccessCallback) {
        if (field == null) {
            return;
        }

        if (field.getDocument() instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument) field.getDocument();
            doc.setDocumentFilter(new CnjDocumentFilter(field, onSuccessCallback));
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateCnjField(field, onSuccessCallback, true);
            }

            @Override
            public void focusGained(FocusEvent e) {
                validateCnjField(field, onSuccessCallback, false);
            }
        });
    }

    /**
     * Instala formatador e validador de CNJ/NPU sem callback.
     */
    public static void installCnjFormatter(JTextField field) {
        installCnjFormatter(field, null);
    }

    /**
     * Instala formatador e validador de CPF em tempo real em um JTextField.
     */
    public static void installCpfFormatter(JTextField field) {
        if (field == null) {
            return;
        }

        if (field.getDocument() instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument) field.getDocument();
            doc.setDocumentFilter(new CpfDocumentFilter(field));
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateCpfField(field, true);
            }

            @Override
            public void focusGained(FocusEvent e) {
                validateCpfField(field, false);
            }
        });
    }

    /**
     * Instala formatador e validador de CNPJ (suporta tradicional e alfanumérico) em tempo real.
     */
    public static void installCnpjFormatter(JTextField field) {
        if (field == null) {
            return;
        }

        if (field.getDocument() instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument) field.getDocument();
            doc.setDocumentFilter(new CnpjDocumentFilter(field));
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateCnpjField(field, true);
            }

            @Override
            public void focusGained(FocusEvent e) {
                validateCnpjField(field, false);
            }
        });
    }

    /**
     * Instala formatador dinâmico CPF ou CNPJ (detecta pelo comprimento de dígitos).
     */
    public static void installCpfOrCnpjFormatter(JTextField field) {
        if (field == null) {
            return;
        }

        if (field.getDocument() instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument) field.getDocument();
            doc.setDocumentFilter(new CpfCnpjDynamicDocumentFilter(field));
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateCpfOrCnpjField(field, true);
            }

            @Override
            public void focusGained(FocusEvent e) {
                validateCpfOrCnpjField(field, false);
            }
        });
    }

    /**
     * Instala formatador de CEP em tempo real em um JTextField.
     *
     * @param field Campo de texto
     * @param onComplete Callback executado ao preencher 8 dígitos válidos
     */
    public static void installCepFormatter(JTextField field, Runnable onComplete) {
        if (field == null) {
            return;
        }

        if (field.getDocument() instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument) field.getDocument();
            doc.setDocumentFilter(new CepDocumentFilter(field, onComplete));
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateCepField(field, onComplete, true);
            }

            @Override
            public void focusGained(FocusEvent e) {
                validateCepField(field, onComplete, false);
            }
        });
    }

    /**
     * Instala formatador de CEP sem callback.
     */
    public static void installCepFormatter(JTextField field) {
        installCepFormatter(field, null);
    }

    // =========================================================================
    // VALIDAÇÕES E ATUALIZAÇÃO VISUAL (FlatLaf Outline + Tooltips)
    // =========================================================================

    public static boolean validateCnjField(JTextField field, Consumer<CnjNumber> onSuccessCallback, boolean onFocusLost) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            field.putClientProperty(OUTLINE_KEY, null);
            field.setToolTipText(null);
            field.repaint();
            return true;
        }

        String rawDigits = text.replaceAll("[^0-9]", "");
        if (rawDigits.length() == 20) {
            if (CnjNumberValidator.isValid(rawDigits)) {
                field.putClientProperty(OUTLINE_KEY, null);
                field.setToolTipText("Processo CNJ Válido");
                field.repaint();
                if (onSuccessCallback != null) {
                    try {
                        onSuccessCallback.accept(CnjNumberValidator.parse(rawDigits));
                    } catch (Exception ignored) {
                    }
                }
                return true;
            } else {
                field.putClientProperty(OUTLINE_KEY, OUTLINE_ERROR);
                field.setToolTipText("Número CNJ inválido: dígito verificador incorreto");
                field.repaint();
                return false;
            }
        } else {
            if (onFocusLost && !rawDigits.isEmpty()) {
                field.putClientProperty(OUTLINE_KEY, OUTLINE_ERROR);
                field.setToolTipText("Número CNJ incompleto (20 dígitos obrigatórios)");
            } else {
                field.putClientProperty(OUTLINE_KEY, null);
                field.setToolTipText(null);
            }
            field.repaint();
            return false;
        }
    }

    public static boolean validateCpfField(JTextField field, boolean onFocusLost) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            field.putClientProperty(OUTLINE_KEY, null);
            field.setToolTipText(null);
            field.repaint();
            return true;
        }

        String digits = text.replaceAll("[^0-9]", "");
        if (digits.length() == 11) {
            if (BrazilianDocumentValidator.isValidCpf(digits)) {
                field.putClientProperty(OUTLINE_KEY, null);
                field.setToolTipText("CPF Válido");
                field.repaint();
                return true;
            } else {
                field.putClientProperty(OUTLINE_KEY, OUTLINE_ERROR);
                field.setToolTipText("CPF Inválido: dígito verificador incorreto");
                field.repaint();
                return false;
            }
        } else {
            if (onFocusLost && !digits.isEmpty()) {
                field.putClientProperty(OUTLINE_KEY, OUTLINE_ERROR);
                field.setToolTipText("CPF incompleto (11 dígitos)");
            } else {
                field.putClientProperty(OUTLINE_KEY, null);
                field.setToolTipText(null);
            }
            field.repaint();
            return false;
        }
    }

    public static boolean validateCnpjField(JTextField field, boolean onFocusLost) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            field.putClientProperty(OUTLINE_KEY, null);
            field.setToolTipText(null);
            field.repaint();
            return true;
        }

        String raw = text.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (raw.length() == 14) {
            if (BrazilianDocumentValidator.isValidCnpj(raw)) {
                field.putClientProperty(OUTLINE_KEY, null);
                field.setToolTipText("CNPJ Válido");
                field.repaint();
                return true;
            } else {
                field.putClientProperty(OUTLINE_KEY, OUTLINE_ERROR);
                field.setToolTipText("CNPJ Inválido: dígito verificador incorreto");
                field.repaint();
                return false;
            }
        } else {
            if (onFocusLost && !raw.isEmpty()) {
                field.putClientProperty(OUTLINE_KEY, OUTLINE_ERROR);
                field.setToolTipText("CNPJ incompleto (14 caracteres)");
            } else {
                field.putClientProperty(OUTLINE_KEY, null);
                field.setToolTipText(null);
            }
            field.repaint();
            return false;
        }
    }

    public static boolean validateCpfOrCnpjField(JTextField field, boolean onFocusLost) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            field.putClientProperty(OUTLINE_KEY, null);
            field.setToolTipText(null);
            field.repaint();
            return true;
        }

        String raw = text.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (raw.length() <= 11) {
            return validateCpfField(field, onFocusLost);
        } else {
            return validateCnpjField(field, onFocusLost);
        }
    }

    public static boolean validateCepField(JTextField field, Runnable onComplete, boolean onFocusLost) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            field.putClientProperty(OUTLINE_KEY, null);
            field.setToolTipText(null);
            field.repaint();
            return true;
        }

        String digits = text.replaceAll("[^0-9]", "");
        if (digits.length() == 8) {
            if (BrazilianDocumentValidator.isValidCep(digits)) {
                field.putClientProperty(OUTLINE_KEY, null);
                field.setToolTipText("CEP Válido");
                field.repaint();
                if (onComplete != null) {
                    try {
                        onComplete.run();
                    } catch (Exception ignored) {
                    }
                }
                return true;
            } else {
                field.putClientProperty(OUTLINE_KEY, OUTLINE_ERROR);
                field.setToolTipText("CEP Inválido");
                field.repaint();
                return false;
            }
        } else {
            if (onFocusLost && !digits.isEmpty()) {
                field.putClientProperty(OUTLINE_KEY, OUTLINE_ERROR);
                field.setToolTipText("CEP incompleto (8 dígitos)");
            } else {
                field.putClientProperty(OUTLINE_KEY, null);
                field.setToolTipText(null);
            }
            field.repaint();
            return false;
        }
    }

    // =========================================================================
    // MÁSCARAS E FORMATAÇÕES EM TEMPO REAL (DOCUMENT FILTERS)
    // =========================================================================

    public static String applyCnjMask(String digitsOnly) {
        if (digitsOnly == null || digitsOnly.isEmpty()) {
            return "";
        }
        String clean = digitsOnly.replaceAll("[^0-9]", "");
        if (clean.length() > 20) {
            clean = clean.substring(0, 20);
        }
        int len = clean.length();
        if (len <= 7) {
            return clean;
        } else if (len <= 9) {
            return clean.substring(0, 7) + "-" + clean.substring(7);
        } else if (len <= 13) {
            return clean.substring(0, 7) + "-" + clean.substring(7, 9) + "." + clean.substring(9);
        } else if (len <= 14) {
            return clean.substring(0, 7) + "-" + clean.substring(7, 9) + "." + clean.substring(9, 13) + "." + clean.substring(13);
        } else if (len <= 16) {
            return clean.substring(0, 7) + "-" + clean.substring(7, 9) + "." + clean.substring(9, 13) + "." + clean.substring(13, 14) + "." + clean.substring(14);
        } else {
            return clean.substring(0, 7) + "-" + clean.substring(7, 9) + "." + clean.substring(9, 13) + "." + clean.substring(13, 14) + "." + clean.substring(14, 16) + "." + clean.substring(16);
        }
    }

    public static String applyCpfMask(String digitsOnly) {
        if (digitsOnly == null || digitsOnly.isEmpty()) {
            return "";
        }
        String clean = digitsOnly.replaceAll("[^0-9]", "");
        if (clean.length() > 11) {
            clean = clean.substring(0, 11);
        }
        int len = clean.length();
        if (len <= 3) {
            return clean;
        } else if (len <= 6) {
            return clean.substring(0, 3) + "." + clean.substring(3);
        } else if (len <= 9) {
            return clean.substring(0, 3) + "." + clean.substring(3, 6) + "." + clean.substring(6);
        } else {
            return clean.substring(0, 3) + "." + clean.substring(3, 6) + "." + clean.substring(6, 9) + "-" + clean.substring(9);
        }
    }

    public static String applyCnpjMask(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        String clean = raw.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (clean.length() > 14) {
            clean = clean.substring(0, 14);
        }
        int len = clean.length();
        if (len <= 2) {
            return clean;
        } else if (len <= 5) {
            return clean.substring(0, 2) + "." + clean.substring(2);
        } else if (len <= 8) {
            return clean.substring(0, 2) + "." + clean.substring(2, 5) + "." + clean.substring(5);
        } else if (len <= 12) {
            return clean.substring(0, 2) + "." + clean.substring(2, 5) + "." + clean.substring(5, 8) + "/" + clean.substring(8);
        } else {
            return clean.substring(0, 2) + "." + clean.substring(2, 5) + "." + clean.substring(5, 8) + "/" + clean.substring(8, 12) + "-" + clean.substring(12);
        }
    }

    public static String applyCepMask(String digitsOnly) {
        if (digitsOnly == null || digitsOnly.isEmpty()) {
            return "";
        }
        String clean = digitsOnly.replaceAll("[^0-9]", "");
        if (clean.length() > 8) {
            clean = clean.substring(0, 8);
        }
        int len = clean.length();
        if (len <= 5) {
            return clean;
        } else {
            return clean.substring(0, 5) + "-" + clean.substring(5);
        }
    }

    // =========================================================================
    // INNER CLASSES: DOCUMENT FILTERS
    // =========================================================================

    private static class CnjDocumentFilter extends DocumentFilter {
        private final JTextField field;
        private final Consumer<CnjNumber> callback;
        private boolean formatting = false;

        public CnjDocumentFilter(JTextField field, Consumer<CnjNumber> callback) {
            this.field = field;
            this.callback = callback;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || formatting) {
                super.insertString(fb, offset, string, attr);
                return;
            }
            reformat(fb, offset, 0, string);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (formatting) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            reformat(fb, offset, length, text);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (formatting) {
                super.remove(fb, offset, length);
                return;
            }
            reformat(fb, offset, length, "");
        }

        private void reformat(FilterBypass fb, int offset, int length, String inserted) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            StringBuilder sb = new StringBuilder(current);
            if (length > 0) {
                sb.delete(offset, offset + length);
            }
            if (inserted != null && !inserted.isEmpty()) {
                sb.insert(offset, inserted);
            }

            String digits = sb.toString().replaceAll("[^0-9]", "");
            if (digits.length() > 20) {
                digits = digits.substring(0, 20);
            }

            String formatted = applyCnjMask(digits);
            formatting = true;
            try {
                fb.replace(0, fb.getDocument().getLength(), formatted, null);
            } finally {
                formatting = false;
            }

            validateCnjField(field, callback, false);
        }
    }

    private static class CpfDocumentFilter extends DocumentFilter {
        private final JTextField field;
        private boolean formatting = false;

        public CpfDocumentFilter(JTextField field) {
            this.field = field;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || formatting) {
                super.insertString(fb, offset, string, attr);
                return;
            }
            reformat(fb, offset, 0, string);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (formatting) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            reformat(fb, offset, length, text);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (formatting) {
                super.remove(fb, offset, length);
                return;
            }
            reformat(fb, offset, length, "");
        }

        private void reformat(FilterBypass fb, int offset, int length, String inserted) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            StringBuilder sb = new StringBuilder(current);
            if (length > 0) {
                sb.delete(offset, offset + length);
            }
            if (inserted != null && !inserted.isEmpty()) {
                sb.insert(offset, inserted);
            }

            String digits = sb.toString().replaceAll("[^0-9]", "");
            if (digits.length() > 11) {
                digits = digits.substring(0, 11);
            }

            String formatted = applyCpfMask(digits);
            formatting = true;
            try {
                fb.replace(0, fb.getDocument().getLength(), formatted, null);
            } finally {
                formatting = false;
            }

            validateCpfField(field, false);
        }
    }

    private static class CnpjDocumentFilter extends DocumentFilter {
        private final JTextField field;
        private boolean formatting = false;

        public CnpjDocumentFilter(JTextField field) {
            this.field = field;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || formatting) {
                super.insertString(fb, offset, string, attr);
                return;
            }
            reformat(fb, offset, 0, string);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (formatting) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            reformat(fb, offset, length, text);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (formatting) {
                super.remove(fb, offset, length);
                return;
            }
            reformat(fb, offset, length, "");
        }

        private void reformat(FilterBypass fb, int offset, int length, String inserted) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            StringBuilder sb = new StringBuilder(current);
            if (length > 0) {
                sb.delete(offset, offset + length);
            }
            if (inserted != null && !inserted.isEmpty()) {
                sb.insert(offset, inserted);
            }

            String raw = sb.toString().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            if (raw.length() > 14) {
                raw = raw.substring(0, 14);
            }

            String formatted = applyCnpjMask(raw);
            formatting = true;
            try {
                fb.replace(0, fb.getDocument().getLength(), formatted, null);
            } finally {
                formatting = false;
            }

            validateCnpjField(field, false);
        }
    }

    private static class CpfCnpjDynamicDocumentFilter extends DocumentFilter {
        private final JTextField field;
        private boolean formatting = false;

        public CpfCnpjDynamicDocumentFilter(JTextField field) {
            this.field = field;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || formatting) {
                super.insertString(fb, offset, string, attr);
                return;
            }
            reformat(fb, offset, 0, string);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (formatting) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            reformat(fb, offset, length, text);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (formatting) {
                super.remove(fb, offset, length);
                return;
            }
            reformat(fb, offset, length, "");
        }

        private void reformat(FilterBypass fb, int offset, int length, String inserted) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            StringBuilder sb = new StringBuilder(current);
            if (length > 0) {
                sb.delete(offset, offset + length);
            }
            if (inserted != null && !inserted.isEmpty()) {
                sb.insert(offset, inserted);
            }

            String raw = sb.toString().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            if (raw.length() > 14) {
                raw = raw.substring(0, 14);
            }

            String formatted = (raw.length() <= 11) ? applyCpfMask(raw) : applyCnpjMask(raw);
            formatting = true;
            try {
                fb.replace(0, fb.getDocument().getLength(), formatted, null);
            } finally {
                formatting = false;
            }

            validateCpfOrCnpjField(field, false);
        }
    }

    private static class CepDocumentFilter extends DocumentFilter {
        private final JTextField field;
        private final Runnable onComplete;
        private boolean formatting = false;

        public CepDocumentFilter(JTextField field, Runnable onComplete) {
            this.field = field;
            this.onComplete = onComplete;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || formatting) {
                super.insertString(fb, offset, string, attr);
                return;
            }
            reformat(fb, offset, 0, string);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (formatting) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            reformat(fb, offset, length, text);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (formatting) {
                super.remove(fb, offset, length);
                return;
            }
            reformat(fb, offset, length, "");
        }

        private void reformat(FilterBypass fb, int offset, int length, String inserted) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            StringBuilder sb = new StringBuilder(current);
            if (length > 0) {
                sb.delete(offset, offset + length);
            }
            if (inserted != null && !inserted.isEmpty()) {
                sb.insert(offset, inserted);
            }

            String digits = sb.toString().replaceAll("[^0-9]", "");
            if (digits.length() > 8) {
                digits = digits.substring(0, 8);
            }

            String formatted = applyCepMask(digits);
            formatting = true;
            try {
                fb.replace(0, fb.getDocument().getLength(), formatted, null);
            } finally {
                formatting = false;
            }

            validateCepField(field, onComplete, false);
        }
    }
}
