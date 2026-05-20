package data

import bot.fsm.UserState
import org.ktorm.jackson.json
import org.ktorm.schema.*

object Users : Table<User>("users") {
    val id = int("id").primaryKey().bindTo { it.id }
    val tgId = long("tg_id").bindTo { it.tgId }
    val config = json<UserConfig>("config").bindTo { it.config }
    val state = enum<UserState>("state").bindTo { it.state }
}

object Profiles : Table<Profile>("profiles") {
    val id = int("id").primaryKey().bindTo { it.id }
    val userId = int("user_id").references(Users) { it.user }.bindTo { it.user.id }
    val name = varchar("name").bindTo { it.name }
    val addresses = text("addresses").bindTo { it.addresses }
}