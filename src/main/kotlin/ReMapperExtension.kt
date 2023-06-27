import kotlin.reflect.KClass

fun <I : Any, O : Any> I.mapTo(clazz: KClass<O>): O {
    return ReMapper().map(this, clazz)
}
