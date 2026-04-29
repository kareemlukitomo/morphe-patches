package app.kareem.patches.reddit.links

import app.kareem.patches.shared.Constants.COMPATIBILITY_REDDIT
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.Method

private const val URL_FORMATTER_CLASS = "Lvu3/f;"
private const val URL_FORMATTER_METHOD = "a"

@Suppress("unused")
val sanitizeRedditShareLinksPatch =
    bytecodePatch(
        name = "Sanitize Reddit share links",
        description = "Stops Reddit from appending tracking query parameters to shared links.",
        default = true,
    ) {
        compatibleWith(COMPATIBILITY_REDDIT)

        execute {
            var sanitizedFormatter = false

            classDefForEach { classDef ->
                if (classDef.type != URL_FORMATTER_CLASS) {
                    return@classDefForEach
                }

                val mutableClass = mutableClassDefBy(classDef)
                val formatterMethod =
                    mutableClass.methods.firstOrNull(::isUrlFormatterMethod)
                        ?: throw PatchException("Could not find URL formatter method in $URL_FORMATTER_CLASS")

                if (formatterMethod.implementation == null) {
                    throw PatchException("URL formatter method in $URL_FORMATTER_CLASS has no implementation")
                }

                formatterMethod.addInstructions(
                    0,
                    "return-object p2",
                )
                sanitizedFormatter = true
            }

            if (!sanitizedFormatter) {
                throw PatchException("Could not find URL formatter class $URL_FORMATTER_CLASS")
            }
        }
    }

private fun isUrlFormatterMethod(method: Method): Boolean {
    if (method.definingClass != URL_FORMATTER_CLASS) return false
    if (method.name != URL_FORMATTER_METHOD) return false
    if (method.returnType != "Ljava/lang/String;") return false
    if (!AccessFlags.STATIC.isSet(method.accessFlags)) return false

    return method.parameterTypes ==
        listOf(
            "Lhc3/x;",
            "Lcom/reddit/sharing/SharingNavigator\$ShareTrigger;",
            "Ljava/lang/String;",
            "Z",
        )
}
