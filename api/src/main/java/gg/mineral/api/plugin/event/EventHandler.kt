package gg.mineral.api.plugin.event

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.SOURCE
)
annotation class EventHandler 
