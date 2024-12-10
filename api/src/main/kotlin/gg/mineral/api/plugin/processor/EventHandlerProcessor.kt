package gg.mineral.api.plugin.processor

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import gg.mineral.api.plugin.event.Event
import gg.mineral.api.plugin.event.EventHandler
import gg.mineral.api.plugin.listener.Listener
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedAnnotationTypes("gg.mineral.api.plugin.event.EventHandler")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
class EventHandlerProcessor : AbstractProcessor() {
    private var messager: Messager? = null
    private var filer: Filer? = null
    private var typeUtils: Types? = null
    private var elementUtils: Elements? = null
    private var listenerType: TypeMirror? = null

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        this.messager = processingEnv.messager
        this.filer = processingEnv.filer
        this.typeUtils = processingEnv.typeUtils
        this.elementUtils = processingEnv.elementUtils
        this.listenerType = elementUtils?.getTypeElement(Listener::class.java.canonicalName)?.asType()
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Collect all methods annotated with @EventHandler
        val listenerMethodsMap = HashMap<TypeElement, MutableList<ExecutableElement>>()

        // Use the annotation class reference directly
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(EventHandler::class.java)) {
            if (annotatedElement !is ExecutableElement) continue

            val enclosingClass = annotatedElement.getEnclosingElement() as TypeElement

            // Check if the enclosing class implements Listener
            if (!implementsListener(enclosingClass)) {
                messager!!.printMessage(
                    Diagnostic.Kind.WARNING,
                    "@EventHandler found in a class that does not implement Listener: "
                            + enclosingClass.qualifiedName
                )
                continue
            }

            listenerMethodsMap.computeIfAbsent(enclosingClass) { k: TypeElement? -> ArrayList() }.add(annotatedElement)
        }

        // For each class that had annotated methods, generate a new subclass
        for ((originalClass, eventHandlerMethods) in listenerMethodsMap) {
            generateSubclass(originalClass, eventHandlerMethods)
        }

        return true // We handled these annotations
    }

    private fun implementsListener(type: TypeElement): Boolean {
        for (iface in type.interfaces) if (typeUtils!!.isSameType(iface, listenerType)) return true

        // Check superclasses if necessary
        val superclass = type.superclass
        if (superclass != null && superclass.kind != TypeKind.NONE) {
            val superElement = typeUtils!!.asElement(superclass)
            if (superElement is TypeElement) return implementsListener(superElement)
        }

        return false
    }

    private fun generateSubclass(originalClass: TypeElement, eventMethods: List<ExecutableElement>) {
        val originalClassName = originalClass.simpleName.toString()
        val packageName = elementUtils!!.getPackageOf(originalClass).qualifiedName.toString()
        val generatedClassName = originalClassName + "_Generated"

        // Build the onEvent method
        val onEventBuilder = MethodSpec.methodBuilder("onEvent")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.BOOLEAN)
            .addParameter(ClassName.get(Event::class.java), "event")

        // Check if event matches any known event method
        for (method in eventMethods) {
            if (method.parameters.size != 1) {
                messager!!.printMessage(
                    Diagnostic.Kind.WARNING,
                    "EventHandler methods should have exactly one parameter of type Event: "
                            + method.simpleName
                )
                continue
            }

            val param = method.parameters[0]
            val paramType = param.asType()

            onEventBuilder.beginControlFlow("if (event instanceof \$T typedEvent)", TypeName.get(paramType))
                .addStatement(
                    "return \$L((\$T)typedEvent)",
                    method.simpleName,
                    TypeName.get(paramType)
                )
                .endControlFlow()
        }

        onEventBuilder.addStatement("return false")

        // Copy all @EventHandler methods as-is (not strictly necessary if they exist in
        // the original class)
        val copiedMethods = ArrayList<MethodSpec>()
        for (method in eventMethods) {
            val methodBuilder = MethodSpec.methodBuilder(method.simpleName.toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(method.returnType))

            for (param in method.parameters) methodBuilder.addParameter(
                TypeName.get(param.asType()),
                param.simpleName.toString()
            )

            // Delegate to super implementation
            val params = if (method.parameters.isEmpty())
                ""
            else
                method.parameters[0].simpleName.toString()

            if (method.returnType.kind == TypeKind.BOOLEAN) methodBuilder.addStatement(
                "return super.\$L(\$L)",
                method.simpleName,
                params
            )
            else methodBuilder.addStatement("return super.\$L(\$L)", method.simpleName, params)

            copiedMethods.add(methodBuilder.build())
        }

        val generatedClass = TypeSpec.classBuilder(generatedClassName)
            .superclass(TypeName.get(originalClass.asType()))
            .addModifiers(Modifier.PUBLIC)
            .addMethod(onEventBuilder.build())
            .addMethods(copiedMethods)
            .build()

        val javaFile = JavaFile.builder(packageName, generatedClass).build()
        try {
            javaFile.writeTo(filer)
        } catch (e: Exception) {
            messager!!.printMessage(Diagnostic.Kind.ERROR, "Failed to write generated class: " + e.message)
        }
    }
}
