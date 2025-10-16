package bot.states

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM

suspend fun BehaviourContextWithFSM<BotState>.welcomeState(state: WelcomeState) {
    // ...
}