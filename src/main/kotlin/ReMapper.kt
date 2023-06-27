import kotlin.reflect.*
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

class ReMapper {

    fun <I : Any, O : Any> map(from: I, clazz: KClass<O>): O {
        val newObject = callConstructor(from, clazz) ?: throw NullPointerException()
        copyMutables(from, newObject)
        return newObject
    }

    private fun <I : Any, O : Any> callConstructor(from: I, clazz: KClass<O>): O? {
        val constructor = clazz.primaryConstructor
        if (constructor != null) {
            val constructorArgs = from::class.members.filterIsInstance<KProperty1<I, Any>>().filter { property ->
                property !is KMutableProperty<*>
            }

            val sortedArguments = arrayListOf<Any>()

            constructor.valueParameters.forEach {
                val parameter = constructorArgs.find { p ->
                    it.name == p.name
                }

                parameter?.let {
                    sortedArguments.add(parameter.get(from))
                }
            }

            return try {
                constructor.call(*sortedArguments.toTypedArray())
            } catch (indexOutOfBoundsException: IndexOutOfBoundsException) {
                throw IllegalArgumentException(getConstructorMismatchErrorMessage(from, clazz, constructor))
            }
        }
        return null
    }

    private fun <I : Any, O : Any> getConstructorMismatchErrorMessage(
        from: I,
        clazz: KClass<O>,
        constructor: KFunction<O>
    ): String {
        return "Constructor arguments do not match while mapping from ${from.javaClass.simpleName}(${
            getFormattedConstructorArguments(
                from::class.primaryConstructor!!.valueParameters
            )
        }) to ${clazz.simpleName}(${getFormattedConstructorArguments(constructor.valueParameters)}." +
                " This might be due to type mismatch or property name mismatch. You can use @AcceptValues() for property mismatch resolution."
    }

    private fun getFormattedConstructorArguments(valueParameters: List<KParameter>): String {
        return valueParameters.joinToString(separator = ", ") {
            "${it.name}: ${it.type}"
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun <I : Any, O : Any> copyMutables(from: I, to: O) {
        val properties = from::class.members.filterIsInstance<KMutableProperty1<I, Any>>()

        for (property in properties) {
            val otherProperties = to::class.members.filterIsInstance<KMutableProperty1<O, Any>>()
            for (otherProperty in otherProperties) {
                if (otherProperty.returnType.javaType.typeName == property.returnType.javaType.typeName
                    && otherProperty.name == property.name
                ) {
                    otherProperty.set(to, property.get(from))
                }
            }
        }
    }
}