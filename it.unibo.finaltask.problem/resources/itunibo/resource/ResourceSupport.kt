package itunibo.resource

abstract class ResourceSupport(val resourceName: String) {
	abstract fun createProperty(name: String, init: String)
	abstract fun setProperty(name: String, value: String)
	abstract fun getProperty(name: String): String
}
