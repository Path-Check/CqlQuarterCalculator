package org.pathcheck.cqlquartercalculator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.cqframework.cql.cql2elm.CqlCompiler;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.LibraryContentType;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.evaluator.engine.elm.LibraryMapper;

public class CqlCompilerHelper {

    public Context evaluator(String file) throws UcumException, IOException {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new MyLibrarySourceProvider());

        UcumService ucumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));

        CqlCompiler compiler = new CqlCompiler(null, modelManager, libraryManager, ucumService);
        compiler.run(CqlCompilerHelper.class.getResourceAsStream(file));

        if (compiler.getErrors().size() > 0) {
            System.err.println("Translation failed due to errors:");
            ArrayList<String> errors = new ArrayList<>();
            for (CqlCompilerException error : compiler.getErrors()) {
                TrackBack tb = error.getLocator();
                String lines = tb == null ? "[n/a]" : String.format("[%d:%d, %d:%d]",
                    tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
                System.err.printf("%s %s%n", lines, error.getMessage());
                errors.add(lines + error.getMessage());
            }
            throw new IllegalArgumentException(errors.toString());
        }

        assertThat(compiler.getErrors().size(), is(0));

        Context c = new Context(LibraryMapper.INSTANCE.map(compiler.getLibrary()));
        HashMap<org.cqframework.cql.elm.execution.VersionedIdentifier, Library> cachedLibs = new HashMap<>();
        for (CompiledLibrary lib : compiler.getCompiledLibraries().values()) {
            Library convertedLib = LibraryMapper.INSTANCE.map(lib.getLibrary());
            cachedLibs.put(convertedLib.getIdentifier(), convertedLib);
        }
        c.registerLibraryLoader(libraryIdentifier -> cachedLibs.get(libraryIdentifier));

        return c;
    }

    public class MyLibrarySourceProvider implements LibrarySourceProvider {
        public MyLibrarySourceProvider() {}

        @Override
        public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
            System.out.println(CqlCompilerHelper.class.getResource(getFileName(libraryIdentifier, LibraryContentType.CQL)));
            return CqlCompilerHelper.class.getResourceAsStream(getFileName(libraryIdentifier, LibraryContentType.CQL));
        }

        private String getFileName(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
            return String.format("%s%s.%s",
                    libraryIdentifier.getId(),
                    libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "",
                    type.toString().toLowerCase()
            );
        }
    }
}
