# Reddit share-link patches

These patches port the Reddit share-link behavior from the ReVanced-style Kareem patches into Morphe bytecode patches without using extension code.

## Change Reddit share domain

`Change Reddit share domain` rewrites Reddit share URL string literals from `reddit.com`, `www.reddit.com`, and `redd.it` templates to `https://redlib.kareem.one` equivalents.

Covered templates include:

- Subreddit and user profile URL prefixes.
- Post and comment permalink templates with `{link_id}`, `{title}`, and `{comment}` placeholders.
- Short-link templates such as `https://redd.it/{link_id}`.
- `https://reddit.com%s` formatted share URLs.

The patch fails fast if none of the expected literals were rewritten.

## Sanitize Reddit share links

`Sanitize Reddit share links` targets Reddit's URL formatter method:

```smali
Lvu3/f;->a(Lhc3/x;Lcom/reddit/sharing/SharingNavigator$ShareTrigger;Ljava/lang/String;Z)Ljava/lang/String;
```

Instead of replacing the whole implementation, it inserts this instruction at the start of the formatter:

```smali
return-object p2
```

For this static method, `p2` is the incoming URL string. Returning it immediately preserves the already-generated permalink while skipping the formatter logic that appends tracking query parameters.

## Verification checklist

1. Build with `Constants.COMPATIBILITY_REDDIT` present in `app.kareem.patches.shared.Constants`.
2. Patch a Reddit APK matching the compatibility entry.
3. Share or copy a post link and verify the host is `redlib.kareem.one`.
4. Verify the shared link does not include Reddit tracking query parameters.
5. Open the shared URL externally and confirm the path still resolves to the expected post, comment, subreddit, or user page.
