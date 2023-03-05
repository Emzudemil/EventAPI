package tech.kettei.event

import java.lang.reflect.Method

class EventManager {
    private val event = HashMap<Class<out Event>, ArrayList<EventMethod>>()
    private var isRemoving = false

    /**
     * Registers all methods with the @EventListener annotation with a priority of 0.
     * @param o The object to register.
     * @return True if the object was registered, false if not.
     * */
    fun register(o: Any): Boolean {
        return register(o, 0)
    }

    /**
     * Registers all methods with the @EventListener annotation.
     * @param o The object to register.
     * @param priority The priority of the event.
     * @return True if the object was registered, false if not.
     * */
    fun register(o : Any, priority: Byte): Boolean {
        var registered = false
        try {
            for (method in o.javaClass.methods) {
                if (method.isAnnotationPresent(EventListener::class.java)) {
                    val parameters = method.parameterTypes
                    if (parameters.size == 1 && Event::class.java.isAssignableFrom(parameters[0])) {
                        val eventClass = parameters[0] as Class<out Event>
                        if (!event.containsKey(eventClass))
                            event[eventClass] = ArrayList()
                        event[eventClass]!!.add(EventMethod(method, o, priority))
                        registered = true
                    }
                }
            }
        } catch (e: Exception) {
            println("Error while registering ${o.javaClass.name}")
        }
        return registered
    }

    /**
     * Unregisters all methods with the @EventListener annotation.
     * @param o The object to unregister.
     * */
    fun unregister(o: Any) {
        for (list in event.values)
        {
            list.removeIf {
                it.source == o
            }
        }
    }

    /**
     * Fires an event.
     * @param e The event to fire.
     * */
    fun fire(e: Event) {
        if(event[e.javaClass] == null)
            return
        try {
            for(em in event[e.javaClass]!!) {
                em.invoke(e)
            }
        }
        catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    /**
     * This is Legacy code, it is not used anymore.
     */
    @Deprecated("This is Legacy code, it is not used anymore.")
    private fun isListener(m: Method): Boolean {
        for(a in m.annotations)
        {
            if(a is EventListener) {
                val p = m.parameters[0]
                if(p.type.interfaces.contains(Event::class.java))
                    return true
            }
        }
        return false
    }
}