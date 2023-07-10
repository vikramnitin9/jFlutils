import moduleutils.CherryPickFinder;
import moduleutils.ImportLib;
import moduleutils.importLib.Util;
import moduleutils.importLib.machinery.ModuleSpec;
import moduleutils.sys.Modules;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.*;
import java.util.Map;

import static org.mockito.Mockito.*;

class TestOneTest {

    private Map<String, Object> namespace;
    private ModuleSpec spec;
    private MockedStatic<ModuleUtils> cherryPickMock;

    @BeforeEach
    void setUp() {
        namespace = new HashMap<>();
        namespace.put("__name__", "testmod");
        namespace.put("__file__", "/home/test_user/tmp/flutils/__init__.py");
        namespace.put("__path__", Collections.singletonList("/home/test_user/tmp/flutils"));
        namespace.put("__attr_map__", attrMap);
        namespace.put("__additional_attrs__", additionalAttrs);

        spec = new ModuleSpec(
                "testmod",
                sentinel.loader,
                Map.of("attr_map", attrMap, "addtl_attrs", additionalAttrs)
        );

        MockedStatic<Util> utilsMock = mockStatic(Util.class);
        utilsMock.when(() -> Util.findSpec(anyString())).thenReturn(spec);

        MockedStatic<CherryPickFinder> cherryPickMock = mockStatic(CherryPickFinder.class);
        cherryPickMock.when(() -> CherryPickFinder.add(anyString(), anyString(), anyList(), anyMap())).thenReturn(null);

        MockedStatic<ImportLib> importLibMock = mockStatic(ImportLib.class);
        importLibMock.when(ImportLib::reload).thenReturn(null);
        importLibMock.when(() -> ImportLib.importModule(anyString())).thenReturn(sentinel.newTestmod);

        this.cherryPickMock = cherryPickMock;
    }

    @AfterEach
    void tearDown() {
        cherryPickMock.close();
    }

    @Test
    void testCherryPick() {
        ModuleUtils.cherryPick(namespace);
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("__loader__", sentinel.loader);
        kwargs.put("__path__", namespace.get("__path__"));
        kwargs.put("__file__", namespace.get("__file__"));
        kwargs.putAll(additionalAttrs);

        verify(CherryPickFinder.class, times(1)).add(
                "testmod",
                namespace.get("__file__"),
                (List<String>) namespace.get("__path__"),
                attrMap,
                kwargs
        );

        verify(ImportLib.class, times(1)).reload(sentinel.testmod);
        verify(ImportLib.class, never()).importModule(anyString());
    }
}
