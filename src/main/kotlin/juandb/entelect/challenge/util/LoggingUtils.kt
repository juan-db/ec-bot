package juandb.entelect.challenge.util

import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

const val LOG_PATH: String = "log-%u.%g.log"
val LOG_FORMATTER: Formatter = SimpleFormatter()
val LOG_HANDLER: FileHandler = FileHandler(LOG_PATH, true).apply {
	formatter = LOG_FORMATTER
}

fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy {
        Logger.getLogger(this.javaClass.name).apply {
			addHandler(LOG_HANDLER)
		}
    }
}

fun Throwable.stringStackTrace(): String {
	val sw = StringWriter()
	val pw = PrintWriter(sw)
	this.printStackTrace(pw)
	return sw.toString()
}

