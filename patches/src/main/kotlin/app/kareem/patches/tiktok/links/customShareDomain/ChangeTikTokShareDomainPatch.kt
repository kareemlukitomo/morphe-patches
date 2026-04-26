package app.kareem.patches.tiktok.links.customShareDomain

import app.kareem.patches.shared.Constants.COMPATIBILITY_TIKTOK
import app.kareem.patches.shared.replaceStringLiterals
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch

private const val CUSTOM_URL = "https://sticktock.kareem.one"
private const val CUSTOM_HOST = "sticktock.kareem.one"

@Suppress("unused")
val changeTikTokShareDomainPatch =
    bytecodePatch(
        name = "Change TikTok share domain",
        description = "Rewrites generated TikTok share links to sticktock.kareem.one.",
        default = true,
    ) {
        compatibleWith(COMPATIBILITY_TIKTOK)

        execute {
            val replaced =
                replaceStringLiterals(
                    mapOf(
                        "https://www.tiktok.com" to CUSTOM_URL,
                        "https://tiktok.com" to CUSTOM_URL,
                        "www.tiktok.com" to CUSTOM_HOST,
                    ),
                )

            if (replaced == 0) {
                throw PatchException("Could not find TikTok share URL literals to rewrite")
            }
        }
    }
