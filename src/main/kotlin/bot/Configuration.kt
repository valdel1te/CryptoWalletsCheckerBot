package bot

import di
import com.typesafe.config.Config
import org.kodein.di.instance

object Configuration {
    private val config: Config by di.instance()

    fun getData(config: String): String =
        Configuration.config.getString(config)
}