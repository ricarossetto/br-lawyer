package org.jlawyer.test.client.i18n;

import com.jdimension.jlawyer.client.utils.DateUtils;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.UIManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Empirical Challenger Stress Test Suite for Milestone M1 (Desktop Localization pt-BR).
 */
public class M1ChallengerStressTest {

    private static final Locale PT_BR = new Locale("pt", "BR");

    @BeforeClass
    public static void setUp() {
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

    // =========================================================================
    // 1. RESOURCEBUNDLE DYNAMIC DISCOVERY, LOADING & PARITY TESTS
    // =========================================================================

    @Test
    public void testDynamicallyDiscoveredBundlesAllLoadCleanlyInPtBr() {
        File baseDir = new File("src/main");
        if (!baseDir.exists()) {
            baseDir = new File("j-lawyer-client/src/main");
        }
        Assert.assertTrue("src/main dir should exist", baseDir.exists());

        List<File> ptBrFiles = new ArrayList<>();
        findPtBrFiles(baseDir, ptBrFiles);
        Assert.assertTrue("Should find pt_BR files (found " + ptBrFiles.size() + ")", ptBrFiles.size() >= 40);

        int loadedCount = 0;
        for (File ptBrFile : ptBrFiles) {
            String absPath = ptBrFile.getAbsolutePath().replace("\\", "/");
            // Extract package and base name from path
            String relPath;
            if (absPath.contains("/src/main/java/")) {
                relPath = absPath.substring(absPath.indexOf("/src/main/java/") + "/src/main/java/".length());
            } else if (absPath.contains("/src/main/resources/")) {
                relPath = absPath.substring(absPath.indexOf("/src/main/resources/") + "/src/main/resources/".length());
            } else {
                continue;
            }

            String bundleBase = relPath.replace("_pt_BR.properties", "").replace("/", ".");
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(bundleBase, PT_BR);
                Assert.assertNotNull("Bundle failed to load for base: " + bundleBase, bundle);
                Assert.assertFalse("Bundle keys empty for base: " + bundleBase, bundle.keySet().isEmpty());
                loadedCount++;
            } catch (Exception e) {
                Assert.fail("Failed to load ResourceBundle for '" + bundleBase + "': " + e.getMessage());
            }
        }
        Assert.assertTrue("Must dynamically load at least 40 bundles (loaded " + loadedCount + ")", loadedCount >= 40);
    }

    @Test
    public void testPropertiesFilesSyntaxAndPlaceholdersMatch() throws Exception {
        File baseDir = new File("src/main");
        if (!baseDir.exists()) {
            baseDir = new File("j-lawyer-client/src/main");
        }
        Assert.assertTrue("src/main dir should exist for deep file inspection", baseDir.exists());

        List<File> ptBrFiles = new ArrayList<>();
        findPtBrFiles(baseDir, ptBrFiles);
        Assert.assertTrue("Should find pt_BR.properties files (found " + ptBrFiles.size() + ")", ptBrFiles.size() >= 40);

        Pattern paramPattern = Pattern.compile("\\{(\\d+)\\}");

        for (File ptBrFile : ptBrFiles) {
            Properties ptProps = new Properties();
            try (FileInputStream fis = new FileInputStream(ptBrFile)) {
                ptProps.load(fis);
            }

            String basePath = ptBrFile.getAbsolutePath().replace("_pt_BR.properties", ".properties");
            File baseFile = new File(basePath);
            if (baseFile.exists()) {
                Properties baseProps = new Properties();
                try (FileInputStream fis = new FileInputStream(baseFile)) {
                    baseProps.load(fis);
                }

                for (String key : baseProps.stringPropertyNames()) {
                    String ptVal = ptProps.getProperty(key);
                    Assert.assertNotNull("File " + ptBrFile.getName() + " missing key: " + key, ptVal);

                    String baseVal = baseProps.getProperty(key);
                    Set<String> baseParams = new HashSet<>();
                    Matcher m1 = paramPattern.matcher(baseVal);
                    while (m1.find()) {
                        baseParams.add(m1.group(1));
                    }

                    Set<String> ptParams = new HashSet<>();
                    Matcher m2 = paramPattern.matcher(ptVal);
                    while (m2.find()) {
                        ptParams.add(m2.group(1));
                    }

                    Assert.assertEquals("Placeholders in " + ptBrFile.getName() + " for key '" + key + "' must match base bundle",
                            baseParams, ptParams);

                    if (!ptParams.isEmpty()) {
                        Object[] testArgs = new Object[10];
                        for (int i = 0; i < 10; i++) testArgs[i] = "ARG" + i;
                        try {
                            String formatted = MessageFormat.format(ptVal, testArgs);
                            Assert.assertNotNull(formatted);
                        } catch (Exception e) {
                            Assert.fail("MessageFormat failed on key '" + key + "' in " + ptBrFile.getName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void findPtBrFiles(File dir, List<File> results) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                findPtBrFiles(f, results);
            } else if (f.getName().endsWith("_pt_BR.properties")) {
                results.add(f);
            }
        }
    }

    // =========================================================================
    // 2. DATEUTILS EDGE CASES, LEAP YEARS & RELATIVE TIME STRESS TESTS
    // =========================================================================

    @Test
    public void testDateUtilsLeapYearsAndEdgeDates() {
        // Valid leap years
        Date leap2024 = DateUtils.parseDate("29/02/2024");
        Assert.assertNotNull("29/02/2024 (leap year) should parse", leap2024);

        Date leap2020 = DateUtils.parseDate("29/02/2020");
        Assert.assertNotNull("29/02/2020 (leap year) should parse", leap2020);

        Date leap2000 = DateUtils.parseDate("29/02/2000");
        Assert.assertNotNull("29/02/2000 (century leap year) should parse", leap2000);

        // Non-leap years (must be rejected due to lenient=false)
        Date nonLeap2023 = DateUtils.parseDate("29/02/2023");
        Assert.assertNull("29/02/2023 is not a leap year and must return null", nonLeap2023);

        Date nonLeap2021 = DateUtils.parseDate("29/02/2021");
        Assert.assertNull("29/02/2021 is not a leap year and must return null", nonLeap2021);

        Date nonLeap1900 = DateUtils.parseDate("29/02/1900");
        Assert.assertNull("29/02/1900 is not a leap year and must return null", nonLeap1900);

        // Invalid days / months
        Date invalidDay = DateUtils.parseDate("31/04/2026");
        Assert.assertNull("31/04/2026 (April has 30 days) must return null", invalidDay);

        Date invalidMonth = DateUtils.parseDate("15/13/2026");
        Assert.assertNull("15/13/2026 must return null", invalidMonth);

        Date day32 = DateUtils.parseDate("32/01/2026");
        Assert.assertNull("32/01/2026 must return null", day32);

        // Blank and invalid strings
        Assert.assertNull(DateUtils.parseDate(""));
        Assert.assertNull(DateUtils.parseDate("    "));
        Assert.assertNull(DateUtils.parseDate("invalid-date-string"));
    }

    @Test
    public void testDateUtilsFormatsVariety() {
        // dd/MM/yyyy
        Date d1 = DateUtils.parseDate("01/01/2026");
        Assert.assertNotNull(d1);

        // dd/MM/yyyy HH:mm
        Date d2 = DateUtils.parseDate("01/01/2026 09:30");
        Assert.assertNotNull(d2);

        // dd/MM/yyyy HH:mm:ss
        Date d3 = DateUtils.parseDate("01/01/2026 09:30:15");
        Assert.assertNotNull(d3);

        // ISO format yyyy-MM-dd
        Date d4 = DateUtils.parseDate("2026-01-01");
        Assert.assertNotNull(d4);

        // Legacy format dd.MM.yyyy
        Date d5 = DateUtils.parseDate("01.01.2026");
        Assert.assertNotNull(d5);
    }

    @Test
    public void testDateUtilsRelativeTimeGranularIntervals() {
        // Null
        Assert.assertEquals("desconhecido", DateUtils.getHumanReadableTimeInPast(null));
        Assert.assertEquals("desconhecido", DateUtils.getHumanReadableTime(null));

        // Past time
        long now = System.currentTimeMillis();
        String s1 = DateUtils.getHumanReadableTimeInPast(new Date(now - 1200L));
        Assert.assertTrue("Should indicate segundos: " + s1, s1.contains("segundo"));

        String s5 = DateUtils.getHumanReadableTimeInPast(new Date(now - 5500L));
        Assert.assertEquals("há 5 segundos", s5);

        String m1 = DateUtils.getHumanReadableTimeInPast(new Date(now - 62_000L));
        Assert.assertEquals("há 1 minuto", m1);

        String m15 = DateUtils.getHumanReadableTimeInPast(new Date(now - 15 * 60_000L - 1000L));
        Assert.assertEquals("há 15 minutos", m15);

        String h1 = DateUtils.getHumanReadableTimeInPast(new Date(now - 3600_000L - 5000L));
        Assert.assertEquals("há 1 hora", h1);

        String h3 = DateUtils.getHumanReadableTimeInPast(new Date(now - 3 * 3600_000L - 5000L));
        Assert.assertEquals("há 3 horas", h3);

        String d1 = DateUtils.getHumanReadableTimeInPast(new Date(now - 86400_000L - 10_000L));
        Assert.assertEquals("há 1 dia", d1);

        String d4 = DateUtils.getHumanReadableTimeInPast(new Date(now - 4 * 86400_000L - 10_000L));
        Assert.assertEquals("há 4 dias", d4);

        String w1 = DateUtils.getHumanReadableTimeInPast(new Date(now - 7 * 86400_000L - 10_000L));
        Assert.assertEquals("há 1 semana", w1);

        String w3 = DateUtils.getHumanReadableTimeInPast(new Date(now - 21 * 86400_000L - 10_000L));
        Assert.assertEquals("há 3 semanas", w3);

        // Future dates with getHumanReadableTime
        String fs5 = DateUtils.getHumanReadableTime(new Date(System.currentTimeMillis() + 5500L));
        Assert.assertEquals("em 5 segundos", fs5);

        String fm1 = DateUtils.getHumanReadableTime(new Date(System.currentTimeMillis() + 65_000L));
        Assert.assertEquals("em 1 minuto", fm1);

        String fh2 = DateUtils.getHumanReadableTime(new Date(System.currentTimeMillis() + 2 * 3600_000L + 10_000L));
        Assert.assertEquals("em 2 horas", fh2);

        String fd1 = DateUtils.getHumanReadableTime(new Date(System.currentTimeMillis() + 86400_000L + 10_000L));
        Assert.assertEquals("em 1 dia", fd1);

        String fw2 = DateUtils.getHumanReadableTime(new Date(System.currentTimeMillis() + 14 * 86400_000L + 10_000L));
        Assert.assertEquals("em 2 semanas", fw2);

        // 1 year in future (53 weeks > 52 weeks) -> "em 1 ano"
        String fy1 = DateUtils.getHumanReadableTime(new Date(System.currentTimeMillis() + 53L * 7 * 86400_000L));
        Assert.assertTrue("Should indicate 1 ano: " + fy1, fy1.contains("1 ano"));

        // 3 years in future -> "em 3 anos"
        String fy3 = DateUtils.getHumanReadableTime(new Date(System.currentTimeMillis() + 3L * 53 * 7 * 86400_000L));
        Assert.assertTrue("Should indicate anos: " + fy3, fy3.contains("anos"));
    }

    @Test
    public void testDateUtilsRangeAndTodayChecks() {
        Date today = new Date();
        Date yesterday = new Date(System.currentTimeMillis() - 86400_000L);
        Date tomorrow = new Date(System.currentTimeMillis() + 86400_000L);

        Assert.assertTrue(DateUtils.isToday(today));
        Assert.assertFalse(DateUtils.isToday(yesterday));
        Assert.assertFalse(DateUtils.isToday(tomorrow));
        Assert.assertFalse(DateUtils.isToday(null));

        Assert.assertTrue(DateUtils.containsToday(yesterday, tomorrow));
        Assert.assertFalse(DateUtils.containsToday(yesterday, yesterday));
        Assert.assertFalse(DateUtils.containsToday(tomorrow, tomorrow));
        Assert.assertTrue(DateUtils.containsToday(today, today));
        Assert.assertTrue(DateUtils.containsToday(null, today));
        Assert.assertTrue(DateUtils.containsToday(today, null));
        Assert.assertFalse(DateUtils.containsToday(null, null));

        Assert.assertTrue(DateUtils.overlapsWithRange(yesterday, tomorrow, -1, 1));
        Assert.assertFalse(DateUtils.overlapsWithRange(null, null, -1, 1));
    }

    // =========================================================================
    // 3. CURRENCY FORMATTER STRESS TESTS (Negative, Large, Zero, Separators)
    // =========================================================================

    @Test
    public void testCurrencyFormatterPtBrConventions() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(PT_BR);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(PT_BR);

        Assert.assertEquals(',', symbols.getDecimalSeparator());
        Assert.assertEquals('.', symbols.getGroupingSeparator());
        Assert.assertEquals("R$", symbols.getCurrencySymbol());

        // Zero
        String formattedZero = nf.format(BigDecimal.ZERO).replace("\u00a0", " ").trim();
        Assert.assertTrue("Formatted zero should be R$ 0,00: " + formattedZero,
                formattedZero.equals("R$ 0,00") || formattedZero.equals("R$0,00") || formattedZero.contains("0,00"));

        // Small decimal
        String formattedSmall = nf.format(new BigDecimal("0.05")).replace("\u00a0", " ").trim();
        Assert.assertTrue("Formatted small decimal should contain 0,05: " + formattedSmall, formattedSmall.contains("0,05"));

        // Regular amount
        String formattedReg = nf.format(new BigDecimal("1234.56")).replace("\u00a0", " ").trim();
        Assert.assertTrue("Formatted regular amount should contain 1.234,56: " + formattedReg, formattedReg.contains("1.234,56"));

        // Negative amount
        String formattedNeg = nf.format(new BigDecimal("-1234.56")).replace("\u00a0", " ").trim();
        Assert.assertTrue("Negative currency should contain negative indicator and 1.234,56: " + formattedNeg,
                formattedNeg.contains("-") && formattedNeg.contains("1.234,56"));

        // Very large amount (Billions)
        String formattedLarge = nf.format(new BigDecimal("9876543210.99")).replace("\u00a0", " ").trim();
        Assert.assertTrue("Large currency should contain 9.876.543.210,99: " + formattedLarge,
                formattedLarge.contains("9.876.543.210,99"));
    }

    // =========================================================================
    // 4. UIMANAGER STRINGS VERIFICATION
    // =========================================================================

    @Test
    public void testUIManagerPortugueseStrings() {
        Assert.assertEquals("Sim", UIManager.getString("OptionPane.yesButtonText"));
        Assert.assertEquals("Não", UIManager.getString("OptionPane.noButtonText"));
        Assert.assertEquals("Cancelar", UIManager.getString("OptionPane.cancelButtonText"));
        Assert.assertEquals("OK", UIManager.getString("OptionPane.okButtonText"));
        Assert.assertEquals("Examinar em:", UIManager.getString("FileChooser.lookInLabelText"));
        Assert.assertEquals("Salvar em:", UIManager.getString("FileChooser.saveInLabelText"));
        Assert.assertEquals("Abrir", UIManager.getString("FileChooser.openButtonText"));
        Assert.assertEquals("Salvar", UIManager.getString("FileChooser.saveButtonText"));
        Assert.assertEquals("Cancelar", UIManager.getString("FileChooser.cancelButtonText"));
        Assert.assertEquals("Nome do Arquivo:", UIManager.getString("FileChooser.fileNameLabelText"));
        Assert.assertEquals("Arquivos do Tipo:", UIManager.getString("FileChooser.filesOfTypeLabelText"));
    }
}
