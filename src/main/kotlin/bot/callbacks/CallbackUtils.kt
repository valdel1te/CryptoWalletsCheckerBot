package bot.callbacks

/** Отправка коллбек даты и аргументов */
fun generateCallbackDataWithArgs(dataName: ECallbackData, args: List<String>): String =
    "${dataName.data}:${args.joinToString(":")}"