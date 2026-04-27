package app.kareem.patches.threads.links.customShareDomain

import app.kareem.patches.shared.Constants.COMPATIBILITY_THREADS
import app.kareem.patches.shared.replaceStringLiterals
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

private const val CUSTOM_URL = "https://shoelace.kareem.one"
private const val CUSTOM_HOST = "shoelace.kareem.one"
private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/kareem/extension/threads/patches/ThreadsShareLinksPatch;"

@Suppress("unused")
val changeThreadsShareDomainPatch =
    bytecodePatch(
        name = "Change Threads share domain",
        description = "Rewrites generated Threads share links to shoelace.kareem.one.",
        default = true,
    ) {
        compatibleWith(COMPATIBILITY_THREADS)

        extendWith("extensions/extension.rve")

        execute {
            replaceStringLiterals(
                mapOf(
                    "https://www.threads.com" to CUSTOM_URL,
                    "https://www.threads.com/" to "$CUSTOM_URL/",
                    "https://threads.com/?" to "$CUSTOM_URL/?",
                    "https://www.threads.com/custom_feed/" to "$CUSTOM_URL/custom_feed/",
                    "https://www.threads.com/search?" to "$CUSTOM_URL/search?",
                    "https://www.threads.net" to CUSTOM_URL,
                    "l.threads.com" to CUSTOM_HOST,
                    "threads.com" to CUSTOM_HOST,
                    "threads.net" to CUSTOM_HOST,
                    "www.threads.com" to CUSTOM_HOST,
                    "www.threads.net" to CUSTOM_HOST,
                ),
            )

            PlainTextShareIntentBuilderFingerprint.method.apply {
                val textRegister = implementation!!.registerCount - 1

                addInstructions(
                    0,
                    """
                    invoke-static/range { v$textRegister .. v$textRegister }, $EXTENSION_CLASS_DESCRIPTOR->rewriteShareText(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v$textRegister
                    """.trimIndent(),
                )
            }
        }
    }
