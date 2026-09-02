package org.jlawyer.test.backupmgr;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * Challenger Stress Test for Backup Manager Localization.
 */
public class BackupMgrChallengerTest {

    private static final Locale PT_BR = new Locale("pt", "BR");

    @Test
    public void testBackupMgrBundleCompletenessAndFormatting() {
        String baseName = "org.jlawyer.backupmgr.controller.backupmgr";
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, PT_BR);
        Assert.assertNotNull("ResourceBundle should load for " + baseName, bundle);
        Assert.assertFalse("ResourceBundle keys should not be empty", bundle.keySet().isEmpty());

        ResourceBundle baseBundle = ResourceBundle.getBundle(baseName, Locale.ROOT);
        Enumeration<String> baseKeys = baseBundle.getKeys();
        while (baseKeys.hasMoreElements()) {
            String key = baseKeys.nextElement();
            Assert.assertTrue("Bundle must contain key '" + key + "' in pt_BR", bundle.containsKey(key));
            String value = bundle.getString(key);
            Assert.assertNotNull("Value for key '" + key + "' must not be null", value);
            Assert.assertFalse("Value for key '" + key + "' must not be empty", value.trim().isEmpty());
        }
    }
}
