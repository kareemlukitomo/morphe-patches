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
import com.android.tools.smali.dexlib2.iface.reference.StringReference

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
                    if (!methodContainsString(method, "android.intent.extra.TEXT")) return@forEach

                    method.instructions
                        .withIndex()
                        .filter { (_, instruction) -> instruction.isIntentPutStringExtra() }
                        .map { (index, instruction) -> index to instruction.registersUsed[2] }
                        .asReversed()
                        .forEach { (index, extraValueRegister) ->
                            method.addInstructions(
                                index,
                                """
                                invoke-static/range { v$extraValueRegister .. v$extraValueRegister }, $EXTENSION_CLASS_DESCRIPTOR->rewriteShareText(Ljava/lang/String;)Ljava/lang/String;
                                move-result-object v$extraValueRegister
                                """.trimIndent(),
                            )
                        }
                }
            }
        }
    }

private fun methodContainsString(
    method: app.morphe.patcher.util.proxy.mutableTypes.MutableMethod,
    string: String,
): Boolean =
    method.instructions.any { instruction ->
        ((instruction as? ReferenceInstruction)?.reference as? StringReference)?.string == string
    }

private fun com.android.tools.smali.dexlib2.iface.instruction.Instruction.isIntentPutStringExtra(): Boolean {
    if (opcode != Opcode.INVOKE_VIRTUAL) return false
    val methodReference = (this as? ReferenceInstruction)?.reference as? MethodReference ?: return false

    return methodReference.definingClass == "Landroid/content/Intent;" &&
        methodReference.name == "putExtra" &&
        methodReference.parameterTypes == listOf("Ljava/lang/String;", "Ljava/lang/String;") &&
        methodReference.returnType == "Landroid/content/Intent;" &&
        registersUsed.size >= 3
}
