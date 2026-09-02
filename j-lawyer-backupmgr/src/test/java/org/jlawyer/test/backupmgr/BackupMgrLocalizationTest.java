package org.jlawyer.test.backupmgr;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests verifying pt-BR localization for j-lawyer-backupmgr.
 */
public class BackupMgrLocalizationTest {

    private static final Locale PT_BR = new Locale("pt", "BR");

    @Test
    public void testBackupMgrBundlePtBr() {
        String baseName = "org.jlawyer.backupmgr.controller.backupmgr";
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, PT_BR);
        Assert.assertNotNull("ResourceBundle should be found for " + baseName, bundle);
        Assert.assertFalse("ResourceBundle should not be empty for " + baseName, bundle.keySet().isEmpty());

        ResourceBundle baseBundle = ResourceBundle.getBundle(baseName, Locale.ROOT);
        Enumeration<String> baseKeys = baseBundle.getKeys();
        while (baseKeys.hasMoreElements()) {
            String key = baseKeys.nextElement();
            Assert.assertTrue("Bundle " + baseName + " must contain key '" + key + "' in pt_BR", bundle.containsKey(key));
            String value = bundle.getString(key);
            Assert.assertNotNull("Value for key '" + key + "' must not be null", value);
            Assert.assertFalse("Value for key '" + key + "' must not be empty", value.trim().isEmpty());
        }
    }
}
