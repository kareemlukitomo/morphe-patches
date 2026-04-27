package app.kareem.patches.threads.links.customShareDomain

import app.kareem.patches.shared.Constants.COMPATIBILITY_THREADS
import app.kareem.patches.shared.replaceStringLiterals
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.registersUsed
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

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

            classDefForEach { classDef ->
                val mutableClass = mutableClassDefBy(classDef)
                mutableClass.methods.forEach { method ->
                    if (method.implementation == null) return@forEach
                    if (method.definingClass.startsWith("Lapp/kareem/extension/")) return@forEach

                    method.instructions
                        .withIndex()
                        .mapNotNull { (index, instruction) ->
                            val rewriteMethod = instruction.extraValueRewriteMethod() ?: return@mapNotNull null
                            val extraValueRegister = instruction.registersUsed[2]
                            ExtraValueRewrite(index, extraValueRegister, rewriteMethod)
                        }
                        .asReversed()
                        .forEach { rewrite ->
                            method.addInstructions(
                                rewrite.index,
                                """
                                invoke-static/range { v${rewrite.extraValueRegister} .. v${rewrite.extraValueRegister} }, $EXTENSION_CLASS_DESCRIPTOR->${rewrite.rewriteMethod}
                                move-result-object v${rewrite.extraValueRegister}
                                """.trimIndent(),
                            )
                        }
                }
            }
        }
    }

private data class ExtraValueRewrite(
    val index: Int,
    val extraValueRegister: Int,
    val rewriteMethod: String,
)

private fun com.android.tools.smali.dexlib2.iface.instruction.Instruction.extraValueRewriteMethod(): String? {
    if (opcode != Opcode.INVOKE_VIRTUAL) return null
    val methodReference = (this as? ReferenceInstruction)?.reference as? MethodReference ?: return null
    if (registersUsed.size < 3) return null

    val isSupportedHolder =
        methodReference.definingClass == "Landroid/content/Intent;" ||
            methodReference.definingClass == "Landroid/os/Bundle;" ||
            methodReference.definingClass == "Landroid/os/BaseBundle;"
    if (!isSupportedHolder) return null
    if (methodReference.name != "putExtra" &&
        methodReference.name != "putString" &&
        methodReference.name != "putCharSequence"
    ) {
        return null
    }
    if (methodReference.parameterTypes.size != 2 ||
        methodReference.parameterTypes[0].toString() != "Ljava/lang/String;"
    ) {
        return null
    }

    return when (methodReference.parameterTypes[1].toString()) {
        "Ljava/lang/String;" -> "rewriteShareText(Ljava/lang/String;)Ljava/lang/String;"
        "Ljava/lang/CharSequence;" -> "rewriteShareCharSequence(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;"
        else -> null
    }
}
