/// Contracts — shared interfaces, enums, and abstract types.
/// All Wire Core contract types are exported here.
library contracts;

// Enums & primitives
export 'src/role.dart';

// User identity
export 'src/user.dart';
export 'src/auth_service.dart';

// Navigation routes
export 'src/routes.dart';

// Module contract
export 'src/module_metadata.dart';
export 'src/module_context.dart'; // exports EventBus, SlotRegistryBase, ModuleContext
export 'src/module_event.dart';
export 'src/app_module.dart';

// Navigation contract
export 'src/app_route.dart';
export 'src/module_route.dart';
export 'src/app_navigator.dart';

// Slot / widget contract
export 'src/ui_slot.dart';
export 'src/slot_ids.dart';

// Tenant contract
export 'src/tenant_config.dart';
