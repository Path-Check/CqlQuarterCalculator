package org.pathcheck.cqlquartercalculator

import org.cqframework.cql.cql2elm.*
import org.fhir.ucum.UcumEssenceService
import org.fhir.ucum.UcumService
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.opencds.cqf.cql.engine.execution.Context
import org.opencds.cqf.cql.evaluator.engine.elm.LibraryMapper
import java.io.InputStream
import java.util.*

class CqlCompiler {

    fun evaluator(file: String?): Context {
        val modelManager = ModelManager()

        // Makes .cql files available to compile
        val libraryManager = LibraryManager(modelManager).apply {
            librarySourceLoader.registerProvider(MyLibrarySourceProvider())
        }

        // Makes sure UCUM exists.
        val ucumService: UcumService = UcumEssenceService(UcumEssenceService::class.java.getResourceAsStream("/ucum-essence.xml"))

        // Compiles
        val compiler = CqlCompiler(null, modelManager, libraryManager, ucumService)
        compiler.run(CqlCompiler::class.java.getResourceAsStream(file))

        if (compiler.errors.size > 0) {
            println("Translation failed due to errors:")
            val errors = ArrayList<String>()
            for (error in compiler.errors) {
                val tb = error.locator
                val lines = if (tb == null) "[n/a]" else "[${tb.startLine}:${tb.startChar}, ${tb.endLine}:${tb.endChar}]"
                println("$lines ${error.message}")
                errors.add(lines + error.message)
            }
            throw IllegalArgumentException(errors.toString())
        }

        // Makes sure there are no errors before proceeding.
        MatcherAssert.assertThat(compiler.errors.size, Matchers.`is`(0))

        // Converts Main Lib to Executable.
        val c = Context(LibraryMapper.INSTANCE.map(compiler.library))

        // Converts dependencies to Executable.
        val cachedLibs = compiler.compiledLibraries.values.associate {
            val convertedLib = LibraryMapper.INSTANCE.map(it.library)
            convertedLib.identifier to convertedLib
        }

        // Adds a loader for the cached libs
        c.registerLibraryLoader { libIdentifier -> cachedLibs[libIdentifier] }

        return c
    }

    inner class MyLibrarySourceProvider : LibrarySourceProvider {
        override fun getLibrarySource(libraryIdentifier: org.hl7.elm.r1.VersionedIdentifier): InputStream? {
            return CqlCompiler::class.java.getResourceAsStream(fileName(libraryIdentifier,LibraryContentType.CQL))
        }

        private fun fileName(libraryIdentifier: org.hl7.elm.r1.VersionedIdentifier, type: LibraryContentType): String {
            val fileName = listOfNotNull(libraryIdentifier.id, libraryIdentifier.version).joinToString("-")
            val extension = type.toString().lowercase(Locale.getDefault())
            return "$fileName.$extension"
        }
    }
}