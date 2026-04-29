/// Known slot host IDs used across the app.
///
/// Modules use these constants when creating [UISlot.slotId].
/// Host screens use them when querying [SlotRegistry.getSlotsForHost].
/// Mirrors Android [SlotIds] object.
abstract final class SlotIds {
  /// Dashboard home screen — primary widget area.
  static const String homeWidgets = 'home_widgets';

  /// Dashboard header — branding or summary info.
  static const String dashboardHeader = 'dashboard_header';

  /// Profile screen — extra actions contributed by modules.
  static const String profileActions = 'profile_actions';

  /// Bottom navigation bar — extra nav items contributed by modules.
  static const String bottomBarActions = 'bottom_bar_actions';

  /// Quick-action FAB area on the home screen.
  static const String homeQuickActions = 'home_quick_actions';
}
