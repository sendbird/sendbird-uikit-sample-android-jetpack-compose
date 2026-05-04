# Changelog
### v1.3.4 (Mar 18, 2026) with Chat SDK `v4.34.1`
- Added `isAutoscrollMessageOverflowToTopEnabled` option to automatically scroll to the top of a new incoming message when its height overflows the visible screen.

### v1.3.3 (Nov 26, 2025) with Chat SDK `v4.32.2`
- Updated Chat SDK to [`v4.32.2`](https://github.com/sendbird/sendbird-chat-sdk-android/releases/tag/chat%2F4.32.2) to add more details in sdk request log for better debugging.

### v1.3.2 (Nov 19, 2025) with Chat SDK `v4.32.1`
- Updated Chat SDK to `v4.32.1` to prevent error screen showing due to connection issues
    - Please refer to the Chat SDK's [Release](https://github.com/sendbird/sendbird-chat-sdk-android/releases/tag/chat%2F4.32.1)
- Fixed error message shown in `ChannelScreen` when `uiState` is `SendbirdScreenUiState.Failure`
    - Changed from `Couldn't retrieve channel list.` to `Couldn't retrieve messages.`

### v1.3.1 (Nov 4, 2025) with Chat SDK `v4.31.1`
- Fixed a possible binary compatibility issue when using Chat SDK in multiple products.

### v1.3.0 (Jul 18, 2025) with Chat SDK `v4.27.3`
- Added support for TypingIndicator in UIKit Compose.
- Added `GroupChannelConfigurations` for controlling GroupChannel features.
- Fixed an issue where the NetworkCallback was registered multiple times when sending image messages repeatedly.

### v1.2.0 (May 29, 2025) with Chat SDK `v4.27.1`
- Message reactions are now supported in UIKit Compose.
- `UIKitBaseMessage` now includes a `reactions` property to hold reaction data.
- Added `isReactionsEnabled` to `UiKitConfig` to enable the feature.
- Added `onClickMessageMenuEmoji`, `onClickMessageMenuEmojiMoreButton`  to `MessageMenuDialogContract`.
- Added `ShowEmojiList` and `ShowEmojiReactedUserDialog` to `ChannelDialogAction`.
- Added new Compose UI components for reactions.
    - `EmojiList`, `EmojiReactionList`, `EmojiReactedUserList`
    - `EmojiItem`, `EmojiReactionItem`, `EmojiReactedUserItem`, `EmojiReactionCountItem`
    - `EmojiListDialog`, `EmojiReactedUserDialog`
    - `EmojiMoreButton`, `EmojiReactionMoreButton`

### v1.1.0 (Apr 23, 2025) with Chat SDK `v4.24.1`
- Added OgTag Support in UIKit-Compose. If a message has `ogMetaData` and OgTag is enabled, links shared within messages will automatically display previews using metadata.
    - Added `OgTagMessage` including `OgTagThumbnail`, `OgTagTitle`, `OgTagContent`, and `OgTagUrl`.
    - You can customize `OgTagMessage` by passing a parameter to `ogTagMessage` in the TextMessage composable.
- `UiKitBaseMessage` now includes an `ogMetaData` property to handle Open Graph metadata for link previews.
- Added `UiKitConfig` to UIKit-Compose for controlling UIKit features
    - Configuration values are managed via the Sendbird Dashboard, but developers can override them programmatically using custom options.
    - The priority of the configurations is custom options over the dashboard. If a custom option is not set, the dashboard value is used.

### v1.0.0 (Dec 6, 2024) with Chat SDK `v4.21.1`
#### UIKit for Jetpack Compose GA
🎉 Exciting Announcement: UIKit for Jetpack Compose now goes GA 🎉<p>
Previously launched as beta, UIKit for Jetpack Compose is now official.
For detailed information and documentation, please visit our [docs homepage](https://sendbird.com/docs/chat/uikit/v3/jetpack-compose/overview).

### v1.0.0-beta.2 (Oct 24, 2024) with Chat SDK `v4.19.4`
* Supported message group UI.
* Improved customization.
    * Added additional parameters to composable functions.
    * Added ViewModelContract for each screen.
    * Added composable functions for dialogs.
    * Added additional small components.

### v1.0.0-beta.1 (Aug 14, 2024) with Chat SDK `v4.18.0`

🎉 Exciting Announcement: Now Supports Jetpack Compose! 🥳

We're thrilled to introduce the latest version of UIKit, bringing modern Jetpack Compose support to our popular Chat SDK!

Our previous UIKit-based SDK allowed for rapid UI integration and simple customization. Now, we're extending these benefits to Jetpack Compose, the cutting-edge framework for building user interfaces.

For detailed information and documentation, please visit our docs homepage.

* Added composable functions for `Channels`, `Channel`, `ChannelSettings`, `ChannelCreation`, `UserInvitation` screens.
* Added small components for `Channels`, `Channel`, `ChannelSettings`, `ChannelCreation`, `UserInvitation` screens.
* Added `Channels`, `Channel`, `ChannelSettings`, `ChannelCreation`, `UserInvitation` view models.
* Added `Channels`, `Channel`, `ChannelSettings`, `ChannelCreation`, `UserInvitation` resource repositories.
