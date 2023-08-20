package by.overpass.defaultimpl

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import java.io.BufferedWriter

typealias FunctionParams = List<Pair<String, KSDeclaration>>

class SymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val defaultImplAnnotationQualifiedName = DefaultImpl::class.qualifiedName!!
        resolver.getSymbolsWithAnnotation(defaultImplAnnotationQualifiedName)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { ksClassDeclaration ->
                processClassDeclaration(
                    resolver = resolver,
                    defaultImplAnnotationQualifiedName = defaultImplAnnotationQualifiedName,
                    ksClassDeclaration = ksClassDeclaration,
                )
            }
        return emptyList()
    }

    private fun processClassDeclaration(
        resolver: Resolver,
        defaultImplAnnotationQualifiedName: String,
        ksClassDeclaration: KSClassDeclaration,
    ) {
        ksClassDeclaration.containingFile?.let { ksFile ->
            val packageName = ksFile.packageName.asString()
            val className = ksClassDeclaration.simpleName.asString()
            ksClassDeclaration.findAnnotation(defaultImplAnnotationQualifiedName)
                ?.let { ksAnnotation ->
                    val implClassDeclaration = ksAnnotation.getArgumentTypeByName("impl")
                        ?.getClassDeclaration(resolver)
                    if (!hasErrors(ksClassDeclaration, implClassDeclaration)) {
                        generateDefaultImplFile(ksFile, packageName, className, implClassDeclaration!!)
                    }
                }
        }
    }

    private fun hasErrors(
        ksClassDeclaration: KSClassDeclaration,
        implClassDeclaration: KSClassDeclaration?,
    ): Boolean {
        if (implClassDeclaration == null) {
            logger.error(
                "Couldn't find the default implementation class for " +
                        ksClassDeclaration.qualifiedNameString,
                ksClassDeclaration,
            )
            return true
        }
        if (!implClassDeclaration.isSubtypeOf(ksClassDeclaration)) {
            logger.error(
                "${implClassDeclaration.qualifiedNameString} is not a subtype" +
                        " of ${ksClassDeclaration.qualifiedNameString}",
                implClassDeclaration,
            )
            return true
        }
        if (implClassDeclaration.classKind !in listOf(ClassKind.CLASS, ClassKind.OBJECT)) {
            logger.error(
                "Only plain classes and object are allowed as default implementations:" +
                        " ${implClassDeclaration.qualifiedNameString}",
                implClassDeclaration,
            )
            return true
        }
        if (implClassDeclaration.isPrivate()) {
            logger.error(
                "Default implementation class can't " +
                        "be private: ${implClassDeclaration.qualifiedNameString}",
                implClassDeclaration,
            )
            return true
        }
        return false
    }

    private fun generateDefaultImplFile(
        ksFile: KSFile,
        packageName: String,
        className: String,
        implClassDeclaration: KSClassDeclaration,
    ) {
        codeGenerator.createNewFile(
            Dependencies(false, ksFile),
            packageName,
            "${className}DefaultImpl",
        )
            .bufferedWriter()
            .use { writer ->
                with(writer) {
                    appendLine("package $packageName")
                    appendLine()
                    implClassDeclaration.getConstructors()
                        .mapToFunctionParams()
                        .forEach { functionParams ->
                            createConstructorFunction(
                                functionParams = functionParams,
                                className = className,
                                implClassDeclaration = implClassDeclaration,
                            )
                        }
                }
            }
    }

    private fun BufferedWriter.createConstructorFunction(
        functionParams: FunctionParams,
        className: String,
        implClassDeclaration: KSClassDeclaration,
    ) {
        appendLine("fun $className(")
        functionParams.forEach { (name, type) ->
            appendLine("    $name: ${type.qualifiedNameString},")
        }
        appendLine("): $className {")
        if (implClassDeclaration.classKind == ClassKind.OBJECT) {
            appendLine("    return ${implClassDeclaration.qualifiedNameString}")
        } else if (implClassDeclaration.classKind == ClassKind.CLASS) {
            appendLine("    return ${implClassDeclaration.qualifiedNameString}(")
            functionParams.forEach { (name) ->
                appendLine("        $name = $name,")
            }
            appendLine("    )")
        }
        appendLine("}")
    }

    class Provider : SymbolProcessorProvider {

        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
            SymbolProcessor(
                environment.codeGenerator,
                environment.logger,
            )
    }
}

private fun KSClassDeclaration.findAnnotation(annotationClassQualifiedName: String): KSAnnotation? =
    annotations
        .find { ksAnnotation ->
            ksAnnotation.annotationType
                .resolve()
                .declaration
                .qualifiedName
                ?.asString() == annotationClassQualifiedName
        }

private fun KSAnnotation.getArgumentTypeByName(argName: String): KSType? =
    arguments
        .find { ksValueArgument ->
            ksValueArgument.name?.asString() == argName
        }
        ?.value
        ?.let { it as? KSType }

private fun KSType.getClassDeclaration(resolver: Resolver): KSClassDeclaration? =
    declaration
        .qualifiedName
        ?.let(resolver::getClassDeclarationByName)

private fun KSClassDeclaration.isSubtypeOf(ksClassDeclaration: KSClassDeclaration): Boolean =
    getAllSuperTypes().contains(ksClassDeclaration.asStarProjectedType())

private val KSDeclaration.qualifiedNameString: String
    get() = qualifiedName?.asString() ?: "[unknown]"

private fun Sequence<KSFunctionDeclaration>.mapToFunctionParams(): Sequence<FunctionParams> =
    map { ksFunctionDeclaration ->
        ksFunctionDeclaration.parameters
            .map { ksValueParameter ->
                val name = ksValueParameter.name?.asString() ?: "arg"
                val type = ksValueParameter.type.resolve().declaration
                name to type
            }
    }
