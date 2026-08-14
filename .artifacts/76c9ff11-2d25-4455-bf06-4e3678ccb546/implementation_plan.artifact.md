# Implementation Plan - Layout Improvement for activity_main.xml

This plan aims to improve the `activity_main.xml` layout by fixing critical errors, externalizing resources, and modernizing the layout structure using `ConstraintLayout`.

## User Review Required

> [!IMPORTANT]
> I am proposing to switch from `LinearLayout` to `ConstraintLayout`. This will flatten the view hierarchy and provide more flexibility for future UI updates.

## Proposed Changes

### Resources

#### [MODIFY] [colors.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/src/main/res/values/colors.xml)
- Define colors used in the layout:
    - `background_dark`: `#121212`
    - `surface_dark`: `#1F1F1F`
    - `text_primary`: `#FFFFFF`
    - `text_secondary`: `#E0E0E0`

#### [MODIFY] [strings.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/src/main/res/values/strings.xml)
- Externalize strings:
    - `btn_connect`: "Connexion au Stand"
    - `label_custom_color`: "Couleur Personnalisée (WLED style)"
    - `label_demo_animations`: "Animations de Démo (Cosplay / Fursuit)"
    - `btn_mode_fire`: "Feu"
    - `btn_mode_rainbow`: "Rainbow"

#### [NEW] [dimens.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/src/main/res/values/dimens.xml)
- Define standard dimensions:
    - `padding_main`: `16dp`
    - `margin_small`: `8dp`
    - `margin_medium`: `12dp`
    - `margin_large`: `24dp`
    - `button_height_large`: `60dp`
    - `seekbar_height`: `32dp`
    - `text_size_medium`: `16sp`

### Layout

#### [MODIFY] [activity_main.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/src/main/res/layout/activity_main.xml)
- Fix `xmlns:android` namespace.
- Add `xmlns:app` and `xmlns:tools` namespaces.
- Convert root to `androidx.constraintlayout.widget.ConstraintLayout`.
- Fix duplicate `layout_height` in `btnConnect`.
- Replace hardcoded values with references to `@color/`, `@string/`, and `@dimen/`.
- Use `Flow` or `ConstraintLayout` constraints to organize buttons instead of a nested `GridLayout` with weights (if applicable, or just clean up the `GridLayout`).
- Add `android:contentDescription` for SeekBars for accessibility.

## Verification Plan

### Automated Tests
- Build the project to ensure XML resources are correctly resolved.
- Use `render_compose_preview` (or equivalent layout renderer) to verify the UI looks consistent.

### Manual Verification
- Inspect the layout in the Android Studio Layout Editor to ensure no errors or warnings remain.
