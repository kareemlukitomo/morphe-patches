package app.kareem.patches.threads.links.customShareDomain

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object PlainTextShareIntentBuilderFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Landroid/content/Intent;",
    parameters = listOf("Ljava/lang/String;", "Ljava/lang/String;"),
    strings =
        listOf(
            "android.intent.action.SEND",
            "android.intent.extra.SUBJECT",
            "android.intent.extra.TEXT",
            "text/plain",
        ),
)
