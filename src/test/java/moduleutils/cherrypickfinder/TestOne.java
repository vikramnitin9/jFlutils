package moduleutils.cherrypickfinder;

import moduleutils.CherryPickFinder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestOne {
    private List<Object> metaPath;
    private List<String> attrMap;
    private Map<String, Object> additionalAttrs;

    @BeforeEach
    void setUp() {
        metaPath = new ArrayList<>();
//        System.setMetaPath(metaPath);
    }

    @AfterEach
    void tearDown() {
        metaPath.clear();
    }

    @Test
    void testCherryPickFinderLoad() {
        CherryPickFinder.load();
        assertEquals(1, metaPath.size());
        assertTrue(metaPath.get(0) instanceof CherryPickFinder);
    }

    @Test
    void testCherryPickFinderAdd() {
        String fullname = "testobj";
        CherryPickFinder.add(
                fullname,
                "__init__",
                "apath",
                attrMap,
                additionalAttrs
        );
        CherryPickFinder finder = (CherryPickFinder) metaPath.get(0);
        assertEquals(fullname, finder.getCache().get(fullname).getFullname());
        assertEquals("__init__", finder.getCache().get(fullname).getOrigin());
        assertEquals("apath", finder.getCache().get(fullname).getPath());
        assertArrayEquals(attrMap, finder.getCache().get(fullname).getAttrMap());
        assertEquals(additionalAttrs, finder.getCache().get(fullname).getAddtlAttrs());
    }

    @Test
    void testCherryPickFinderFindSpec() {
        String fullname = "testobj";
        CherryPickFinder.add(
                fullname,
                "__init__",
                "apath",
                attrMap,
                additionalAttrs
        );
        CherryPickFinder finder = (CherryPickFinder) metaPath.get(0);
        ModuleSpec spec = finder.findSpec(fullname, "unusedpath");
        assertEquals(fullname, spec.getName());
        _CherryPickLoaderState loaderState = (_CherryPickLoaderState) spec.getLoaderState();
        assertEquals(fullname, loaderState.getFullname());
        assertEquals("__init__", loaderState.getOrigin());
        assertEquals("apath", loaderState.getPath());
    }
}