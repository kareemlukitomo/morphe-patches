package app.kareem.patches.threads.links.customShareDomain

import app.kareem.patches.shared.Constants.COMPATIBILITY_THREADS
import app.kareem.patches.shared.replaceStringLiterals
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch

private const val CUSTOM_URL = "https://shoelace.kareem.one"
private const val CUSTOM_HOST = "shoelace.kareem.one"

@Suppress("unused")
val changeThreadsShareDomainPatch =
    bytecodePatch(
        name = "Change Threads share domain",
        description = "Rewrites generated Threads share links to shoelace.kareem.one.",
        default = true,
    ) {
        compatibleWith(COMPATIBILITY_THREADS)

        execute {
            val replaced =
                replaceStringLiterals(
                    mapOf(
                        "https://www.threads.com" to CUSTOM_URL,
                        "https://www.threads.net" to CUSTOM_URL,
                        "www.threads.com" to CUSTOM_HOST,
                        "www.threads.net" to CUSTOM_HOST,
                    ),
                )

            if (replaced == 0) {
                throw PatchException("Could not find Threads share URL literals to rewrite")
            }
        }
    }
