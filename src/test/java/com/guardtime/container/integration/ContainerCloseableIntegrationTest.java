package com.guardtime.container.integration;

import com.guardtime.container.packaging.Container;
import com.guardtime.container.packaging.ContainerPackagingFactory;
import com.guardtime.container.packaging.parsing.TemporaryFileBasedParsingStoreFactory;
import com.guardtime.container.packaging.zip.ZipContainerPackagingFactoryBuilder;
import com.guardtime.container.signature.SignatureFactory;
import com.guardtime.container.signature.ksi.KsiSignatureFactory;
import com.guardtime.container.util.Util;
import com.guardtime.ksi.KSI;
import com.guardtime.ksi.hashing.DataHash;
import com.guardtime.ksi.unisignature.KSISignature;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ContainerCloseableIntegrationTest {
    private Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
    private ContainerPackagingFactory packagingFactory;

    @Before
    public void cleanUpTempDir() throws Exception {
        cleanTempDir();
        assertFalse("Unclean test system! There are some 'ksie' files in " + tmpDir, anyKsieTempFiles());
    }

    @After
    public void assertCleanTempDir() {
        assertFalse("Close did not delete all temporary files!", anyKsieTempFiles());
    }

    @Before
    public void setUp() throws Exception {
        KSI mockKsi = Mockito.mock(KSI.class);
        when(mockKsi.sign(Mockito.any(DataHash.class))).thenReturn(Mockito.mock(KSISignature.class));
        when(mockKsi.extend(Mockito.any(KSISignature.class))).thenReturn(Mockito.mock(KSISignature.class));
        SignatureFactory signatureFactory = new KsiSignatureFactory(mockKsi);
        packagingFactory = new ZipContainerPackagingFactoryBuilder().
                withSignatureFactory(signatureFactory).
                withParsingStoreFactory(new TemporaryFileBasedParsingStoreFactory()).
                build();
    }

    @Test
    public void testClosingClosedContainerDoesNotThrowException() throws Exception {
        Container container = getContainer();
        container.close();
        container.close();
    }

    @Test
    public void testCloseDeletesTemporaryFiles() throws Exception {
        Container container = getContainer();
        assertTrue("Temporary files not found!", anyKsieTempFiles());
        container.close();
        assertFalse("Close did not delete all temporary files!", anyKsieTempFiles());
    }

    @Test
    public void testContainerWithTryWithResources() throws Exception {
        try (Container container = getContainer()) {
            assertFalse(container.getSignatureContents().isEmpty());
            assertTrue("Temporary files not found!", anyKsieTempFiles());
        }
    }

    private Container getContainer() throws Exception {
        URL url = Thread.currentThread().getContextClassLoader().getResource("containers/container-one-document.ksie");
        File file = new File(url.toURI());
        FileInputStream input = new FileInputStream(file);
        return packagingFactory.read(input);
    }

    private boolean anyKsieTempFiles() {
        for (String s : tempFiles()) {
            if (isTempFile(s)) {
                return true;
            }
        }
        return false;
    }

    private void cleanTempDir() throws IOException {
        for (String s : tempFiles()) {
            if (isTempFile(s)) {
                Util.deleteFileOrDirectory(Paths.get(s));
            }
        }
    }

    private List<String> tempFiles() {
        String[] list = tmpDir.toFile().list();
        if (list == null) {
            return new LinkedList<>();
        } else {
            return Arrays.asList(list);
        }
    }

    private boolean isTempFile(String s) {
        return s.startsWith(Util.TEMP_DIR_PREFIX) || s.startsWith(Util.TEMP_FILE_PREFIX);
    }
}
