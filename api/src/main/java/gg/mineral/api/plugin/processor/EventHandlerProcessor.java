package gg.mineral.api.plugin.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import gg.mineral.api.plugin.event.Event;
import gg.mineral.api.plugin.event.EventHandler;
import gg.mineral.api.plugin.listener.Listener;
import lombok.val;

@AutoService(Processor.class)
@SupportedAnnotationTypes("gg.mineral.api.plugin.event.EventHandler")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class EventHandlerProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private Types typeUtils;
    private Elements elementUtils;
    private TypeMirror listenerType;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.listenerType = elementUtils.getTypeElement(Listener.class.getCanonicalName()).asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Collect all methods annotated with @EventHandler
        val listenerMethodsMap = new HashMap<TypeElement, List<ExecutableElement>>();

        // Use the annotation class reference directly
        for (val annotatedElement : roundEnv.getElementsAnnotatedWith(EventHandler.class)) {
            if (!(annotatedElement instanceof ExecutableElement method))
                continue;

            val enclosingClass = (TypeElement) method.getEnclosingElement();

            // Check if the enclosing class implements Listener
            if (!implementsListener(enclosingClass)) {
                messager.printMessage(Diagnostic.Kind.WARNING,
                        "@EventHandler found in a class that does not implement Listener: "
                                + enclosingClass.getQualifiedName());
                continue;
            }

            listenerMethodsMap.computeIfAbsent(enclosingClass, k -> new ArrayList<>()).add(method);
        }

        // For each class that had annotated methods, generate a new subclass
        for (val entry : listenerMethodsMap.entrySet()) {
            val originalClass = entry.getKey();
            val eventHandlerMethods = entry.getValue();

            generateSubclass(originalClass, eventHandlerMethods);
        }

        return true; // We handled these annotations
    }

    private boolean implementsListener(TypeElement type) {
        for (val iface : type.getInterfaces())
            if (typeUtils.isSameType(iface, listenerType))
                return true;

        // Check superclasses if necessary
        val superclass = type.getSuperclass();
        if (superclass != null && superclass.getKind() != TypeKind.NONE) {
            val superElement = typeUtils.asElement(superclass);
            if (superElement instanceof TypeElement ste)
                return implementsListener(ste);
        }

        return false;
    }

    private void generateSubclass(TypeElement originalClass, List<ExecutableElement> eventMethods) {
        val originalClassName = originalClass.getSimpleName().toString();
        val packageName = elementUtils.getPackageOf(originalClass).getQualifiedName().toString();
        val generatedClassName = originalClassName + "_Generated";

        // Build the onEvent method
        val onEventBuilder = MethodSpec.methodBuilder("onEvent")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(ClassName.get(Event.class), "event");

        // Check if event matches any known event method
        for (val method : eventMethods) {
            if (method.getParameters().size() != 1) {
                messager.printMessage(Diagnostic.Kind.WARNING,
                        "EventHandler methods should have exactly one parameter of type Event: "
                                + method.getSimpleName());
                continue;
            }

            val param = method.getParameters().get(0);
            val paramType = param.asType();

            onEventBuilder.beginControlFlow("if (event instanceof $T typedEvent)", TypeName.get(paramType))
                    .addStatement("return $L(($T)typedEvent)",
                            method.getSimpleName(),
                            TypeName.get(paramType))
                    .endControlFlow();
        }

        onEventBuilder.addStatement("return false");

        // Copy all @EventHandler methods as-is (not strictly necessary if they exist in
        // the original class)
        val copiedMethods = new ArrayList<MethodSpec>();
        for (val method : eventMethods) {
            val methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(method.getReturnType()));

            for (val param : method.getParameters())
                methodBuilder.addParameter(TypeName.get(param.asType()), param.getSimpleName().toString());

            // Delegate to super implementation
            val params = method.getParameters().isEmpty() ? ""
                    : method.getParameters().get(0).getSimpleName().toString();

            if (method.getReturnType().getKind() == TypeKind.BOOLEAN)
                methodBuilder.addStatement("return super.$L($L)", method.getSimpleName(), params);
            else
                methodBuilder.addStatement("return super.$L($L)", method.getSimpleName(), params);

            copiedMethods.add(methodBuilder.build());
        }

        val generatedClass = TypeSpec.classBuilder(generatedClassName)
                .superclass(TypeName.get(originalClass.asType()))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(onEventBuilder.build())
                .addMethods(copiedMethods)
                .build();

        val javaFile = JavaFile.builder(packageName, generatedClass).build();
        try {
            javaFile.writeTo(filer);
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Failed to write generated class: " + e.getMessage());
        }
    }
}
