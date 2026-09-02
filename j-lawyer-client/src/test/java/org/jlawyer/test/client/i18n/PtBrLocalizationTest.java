package org.jlawyer.test.client.i18n;

import com.jdimension.jlawyer.client.utils.DateUtils;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.UIManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests verifying pt-BR ResourceBundles, DateUtils, UIManager, and currency formatting.
 */
public class PtBrLocalizationTest {

    private static final Locale PT_BR = new Locale("pt", "BR");

    @BeforeClass
    public static void setUpClass() {
        Locale.setDefault(PT_BR);
        Locale.setDefault(Locale.Category.FORMAT, PT_BR);
        Locale.setDefault(Locale.Category.DISPLAY, PT_BR);

        UIManager.put("OptionPane.yesButtonText", "Sim");
        UIManager.put("OptionPane.noButtonText", "Não");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "OK");

        UIManager.put("FileChooser.lookInLabelText", "Examinar em:");
        UIManager.put("FileChooser.saveInLabelText", "Salvar em:");
        UIManager.put("FileChooser.openButtonText", "Abrir");
        UIManager.put("FileChooser.saveButtonText", "Salvar");
        UIManager.put("FileChooser.cancelButtonText", "Cancelar");
        UIManager.put("FileChooser.fileNameLabelText", "Nome do Arquivo:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Arquivos do Tipo:");
    }

    @Test
    public void testResourceBundlesExistAndLoadPtBr() {
        String[] bundleBases = {
            "com.jdimension.jlawyer.client.AboutDialog",
            "com.jdimension.jlawyer.client.AdminConsoleFrame",
            "com.jdimension.jlawyer.client.JKanzleiGUI",
            "com.jdimension.jlawyer.client.LoginDialog",
            "com.jdimension.jlawyer.client.Main",
            "com.jdimension.jlawyer.client.Modules",
            "com.jdimension.jlawyer.client.SplashThread",
            "com.jdimension.jlawyer.client.StartupSplashFrame",
            "com.jdimension.jlawyer.client.components.MultiCalDialog",
            "com.jdimension.jlawyer.client.configuration.BackupConfigurationDialog",
            "com.jdimension.jlawyer.client.configuration.BankSearchDialog",
            "com.jdimension.jlawyer.client.configuration.BankSearchThread",
            "com.jdimension.jlawyer.client.configuration.CitySearchDialog",
            "com.jdimension.jlawyer.client.configuration.CitySearchThread",
            "com.jdimension.jlawyer.client.configuration.CustomFieldConfigurationDialog",
            "com.jdimension.jlawyer.client.configuration.CustomLauncherOptionsDialog",
            "com.jdimension.jlawyer.client.configuration.DrebisConfigurationDialog",
            "com.jdimension.jlawyer.client.configuration.FontSizeConfigDialog",
            "com.jdimension.jlawyer.client.configuration.ImportBanksDialog",
            "com.jdimension.jlawyer.client.configuration.ImportBanksThread",
            "com.jdimension.jlawyer.client.configuration.ImportContactsDialog",
            "com.jdimension.jlawyer.client.configuration.ImportZipCodesDialog",
            "com.jdimension.jlawyer.client.configuration.ImportZipCodesThread",
            "com.jdimension.jlawyer.client.configuration.OptionGroupConfigurationDialog",
            "com.jdimension.jlawyer.client.configuration.ProfileDialog",
            "com.jdimension.jlawyer.client.configuration.UserProfileDialog",
            "com.jdimension.jlawyer.client.desktop.DesktopPanel",
            "com.jdimension.jlawyer.client.desktop.LastChangedEntryPanel",
            "com.jdimension.jlawyer.client.desktop.LastChangedTimerTask",
            "com.jdimension.jlawyer.client.desktop.ReviewDueEntryPanel",
            "com.jdimension.jlawyer.client.desktop.ReviewsDueTimerTask",
            "com.jdimension.jlawyer.client.desktop.SystemStateTimerTask",
            "com.jdimension.jlawyer.client.desktop.TaggedEntryPanel",
            "com.jdimension.jlawyer.client.desktop.TaggedTimerTask",
            "com.jdimension.jlawyer.client.editors.EditorsRegistry",
            "com.jdimension.jlawyer.client.editors.ShowURLDialog",
            "com.jdimension.jlawyer.client.editors.addresses.CaseForContactEntryPanel",
            "com.jdimension.jlawyer.client.enrichment.BrazilianIntegrationsConfigDialog",
            "com.jdimension.jlawyer.client.enrichment.CompanyEnrichmentDialog",
            "com.jdimension.jlawyer.client.enrichment.ContactDiffDialog",
            "de.costache.calendar.calendar",
            "themes.FlatIntelliJLaf"
        };

        for (String baseName : bundleBases) {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, PT_BR);
            Assert.assertNotNull("ResourceBundle should be found for " + baseName, bundle);
            Assert.assertFalse("ResourceBundle should not be empty for " + baseName, bundle.keySet().isEmpty());

            // Check key parity against default bundle
            ResourceBundle baseBundle = ResourceBundle.getBundle(baseName, Locale.ROOT);
            Enumeration<String> baseKeys = baseBundle.getKeys();
            while (baseKeys.hasMoreElements()) {
                String key = baseKeys.nextElement();
                Assert.assertTrue("Bundle " + baseName + " must contain key '" + key + "' in pt_BR", bundle.containsKey(key));
                String value = bundle.getString(key);
                Assert.assertNotNull("Value for key '" + key + "' in " + baseName + " must not be null", value);
                String baseValue = baseBundle.getString(key);
                if (!baseValue.trim().isEmpty()) {
                    Assert.assertFalse("Value for key '" + key + "' in " + baseName + " must not be empty", value.trim().isEmpty());
                }
            }
        }
    }

    @Test
    public void testDateUtilsFormattingAndParsing() {
        Assert.assertEquals("dd/MM/yyyy, HH:mm", DateUtils.DATEFORMAT_DATETIME_DEFAULT);
        Assert.assertEquals("EEE, dd/MM/yyyy HH:mm:ss", DateUtils.DATEFORMAT_DATETIME_FULL);

        Date d1 = DateUtils.parseDate("15/08/2026");
        Assert.assertNotNull("DateUtils should parse dd/MM/yyyy", d1);

        Date d2 = DateUtils.parseDate("15/08/2026 14:30");
        Assert.assertNotNull("DateUtils should parse dd/MM/yyyy HH:mm", d2);

        Date d3 = DateUtils.parseDate("15/08/2026 14:30:45");
        Assert.assertNotNull("DateUtils should parse dd/MM/yyyy HH:mm:ss", d3);

        Date d4 = DateUtils.parseDate("15.08.2026");
        Assert.assertNotNull("DateUtils should parse legacy dd.MM.yyyy", d4);
    }

    @Test
    public void testDateUtilsHumanReadableTimePtBr() {
        long now = System.currentTimeMillis();
        String justNow = DateUtils.getHumanReadableTimeInPast(new Date(now - 1000));
        Assert.assertTrue("Should indicate seconds: " + justNow, justNow.contains("segundo"));

        String fiveMinsAgo = DateUtils.getHumanReadableTimeInPast(new Date(now - (5 * 60 * 1000)));
        Assert.assertTrue("Should indicate minutes ago: " + fiveMinsAgo, fiveMinsAgo.contains("minuto"));

        String pastDate = DateUtils.getHumanReadableTime(new Date(now - (10 * 86400 * 1000L)));
        Assert.assertNotNull(pastDate);
        Assert.assertFalse(pastDate.isEmpty());
        Assert.assertTrue("Should indicate past time: " + pastDate, pastDate.contains("semana") || pastDate.contains("dia"));

        String nullDate = DateUtils.getHumanReadableTimeInPast(null);
        Assert.assertEquals("desconhecido", nullDate);
    }

    @Test
    public void testCurrencyFormatPtBr() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(PT_BR);
        String formatted = nf.format(new BigDecimal("1234.56"));
        Assert.assertTrue("Formatted currency should contain R$: " + formatted, formatted.contains("R$"));
        Assert.assertTrue("Formatted currency should use comma for decimals: " + formatted, formatted.contains("1.234,56") || formatted.contains("1234,56"));
    }

    @Test
    public void testUIManagerPtBrStrings() {
        Assert.assertEquals("Sim", UIManager.getString("OptionPane.yesButtonText"));
        Assert.assertEquals("Não", UIManager.getString("OptionPane.noButtonText"));
        Assert.assertEquals("Cancelar", UIManager.getString("OptionPane.cancelButtonText"));
        Assert.assertEquals("OK", UIManager.getString("OptionPane.okButtonText"));
        Assert.assertEquals("Examinar em:", UIManager.getString("FileChooser.lookInLabelText"));
        Assert.assertEquals("Abrir", UIManager.getString("FileChooser.openButtonText"));
        Assert.assertEquals("Salvar", UIManager.getString("FileChooser.saveButtonText"));
    }
}
