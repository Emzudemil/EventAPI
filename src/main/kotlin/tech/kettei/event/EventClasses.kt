package tech.kettei.event

import java.lang.reflect.Method

/**
 * The event class.
 * All events must extend this class.
 * */
interface Event

/**
 * The cancellable event class.
 * All cancellable events must extend this class.
 * @see
 * */
interface Cancellable: Event {
    var cancelled: Boolean
}

/**
 * The event listener annotation.
 * All methods that should be called when an event is fired must be annotated with this.
 * */
annotation class EventListener

/**
 * The event method class.
 * @param method The method to invoke.
 * @param source The object that contains the method.
 * @param priority The priority of the event.
 * */
class EventMethod(private val method: Method, val source : Any, val priority : Byte) {

    /**
     * Sets the method to accessible.
     * */
    init {
        if(!method.isAccessible)
            method.isAccessible = true
    }

    /**
     * Invokes the method.
     * @param event The event to invoke the method with.
     * */
    fun invoke(event: Event) {
        method.invoke(source, event)
    }
}